package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.entities.DTO.ImagesResponse;
import dev.code925.pdf2img.entities.DTO.OCRResponse;
import dev.code925.pdf2img.exception.EmptyFileException;
import dev.code925.pdf2img.exception.OutOfRangeException;
import dev.code925.pdf2img.repository.FileRepository;

import dev.code925.pdf2img.services.FileStorageManger;
import dev.code925.pdf2img.services.ImageService;
import dev.code925.pdf2img.utils.LanguageConverter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@Validated
@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileStorageManger fileManger;

    // La secuencia de bytes que identifica a un archivo PNG
    private static final byte[] PNG_MAGIC_BYTES = {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, // PNG Signature
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A  // Chunk Length
    };

    @GetMapping(path = "/{documentId}")
    public ResponseEntity<ImagesResponse> getAllImagesFromPdf(@PathVariable @Size(min = 36, max = 36) @org.hibernate.validator.constraints.UUID String documentId){

        String input = fileRepository.getAssetsImages(UUID.fromString(documentId));

        if (input.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        Set<String> allImages = Arrays.stream(input.split(",")).collect(Collectors.toSet());

        ImagesResponse response = new ImagesResponse();
        response.setUuid(documentId);
        response.setSize(allImages.size());

        allImages.forEach(image -> {
            Link embeddedImages = null;
            try {
                embeddedImages = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).showImage(image)).withRel("images");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response.add(embeddedImages);
        });

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/ocr", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> extractTextFromImage(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "spa") String lang) throws IOException {

        if (file.isEmpty()) {
            throw  new EmptyFileException("No se proporcionó ningún archivo.");
        }

        if (!this.isValidPng(file)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Error: El archivo no es un formato PNG válido.");
        }

        ITesseract instance = new Tesseract();
        // Directorio de tessdata de tesseract en Ubuntu Linux
        instance.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        instance.setLanguage(lang.replace(',', '+'));

        // java --enable-native-access=ALL-UNNAMED -jar tu-archivo.jar
        // ls | xargs -I {} dwebp {} -o {}.png

        OCRResponse ocrResponse = null;
        try {
            UUID ocrUuid = UUID.randomUUID();
            fileManger.store(file, ocrUuid.toString(), "png");
            Resource imageFile = fileManger.loadAsResource(ocrUuid.toString()+".png");
            String result = instance.doOCR(imageFile.getFile());

            Set<String> languages = LanguageConverter.convertToLanguageSet(lang);

            ocrResponse = new OCRResponse();
            ocrResponse.setOriginalFile(file.getOriginalFilename());
            ocrResponse.setContentType(file.getContentType());
            ocrResponse.setSize(file.getSize());
            ocrResponse.setLanguages(languages);
            ocrResponse.setText(result);

            fileManger.delete(ocrUuid.toString()+".png");
        } catch (TesseractException | IOException e) {
            System.err.println("OCR Error: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(ocrResponse);
    }


    @GetMapping(path = "/render/{uuid}/{page}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> renderImageFromPage(
            @PathVariable @Size(min = 36, max = 36) @org.hibernate.validator.constraints.UUID String uuid,
            @PathVariable @Min(1) Integer page
    ) throws IOException {

        int lastPage = this.fileRepository.getTotalPages(UUID.fromString(uuid));

        if (page > lastPage) {
            throw new OutOfRangeException(String.format("El documento puede generar imágenes en el rango 1-%d", lastPage));
        }

        Optional<Resource> response = this.imageService.getImageFromPdf(uuid, page);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(response.get());
    }

    @GetMapping(path = "/embedded/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> showImage(@PathVariable @Size(min = 36, max = 36) @org.hibernate.validator.constraints.UUID String imageId) throws IOException {
        Resource image = this.fileManger.loadAsResource(String.format("%s.png", imageId));

        if (!image.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(image);
    }


    private boolean isValidPng(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return false;
        }

        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[PNG_MAGIC_BYTES.length];
            int bytesRead = is.read(buffer);

            if (bytesRead < PNG_MAGIC_BYTES.length) {
                // El archivo es demasiado pequeño para contener el header de un PNG
                return false;
            }

            // Comparar los bytes leídos con la firma PNG
            for (int i = 0; i < PNG_MAGIC_BYTES.length; i++) {
                if (buffer[i] != PNG_MAGIC_BYTES[i]) {
                    return false; // No coincide con la firma PNG
                }
            }

            return true; // Coincide, es probable que sea un PNG
        }
    }
}