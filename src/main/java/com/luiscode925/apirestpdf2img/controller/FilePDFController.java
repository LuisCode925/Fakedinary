package com.luiscode925.apirestpdf2img.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.luiscode925.apirestpdf2img.entities.FilePDF;
import com.luiscode925.apirestpdf2img.entities.TextResponse;
import com.luiscode925.apirestpdf2img.exception.EmptyFileException;
import com.luiscode925.apirestpdf2img.exception.FileWithPassException;
import com.luiscode925.apirestpdf2img.exception.MinFileSizeException;
import com.luiscode925.apirestpdf2img.exception.NotAllowedFileException;
import com.luiscode925.apirestpdf2img.repository.FilePDFRepository;
import com.luiscode925.apirestpdf2img.services.FileStorageManger;

@RestController
@RequestMapping("/pdf")
public class FilePDFController {

    private static final long MIN_FILE_SIZE = 1024;
    private static final List<String> LIST_OF_ALLOWED_EXTENSIONS = List.of("pdf");

    @Autowired
    FilePDFRepository pdfRepository;

    @Autowired
    FileStorageManger fileManger;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile requestFile) throws Exception {

        // Validacion 
        fileValidator(requestFile);

        // Extraccion de la informacion 
        FilePDF saved = pdfRepository.save(extractInformation(requestFile, requestFile.getOriginalFilename()));

        // Mover el archivo a el directorio 
        fileManger.store(requestFile, saved.getUuid().toString());

        // Links HATEOAS
        saved.add(linkTo(methodOn(FilePDFController.class).serveFile(saved.getUuid().toString())).withSelfRel());
        saved.add(linkTo(methodOn(FilePDFController.class).extractText(saved.getUuid().toString())).withRel("extractText"));

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<?> handleFileUploadMultiple(@RequestParam("files") MultipartFile[] multipleFiles) throws Exception {

        List<FilePDF> response = new ArrayList<FilePDF>();

        for (MultipartFile singleFile : multipleFiles) {
            fileValidator(singleFile);
            FilePDF saved = pdfRepository.save(extractInformation(singleFile, singleFile.getOriginalFilename()));
            fileManger.store(singleFile, saved.getUuid().toString());
            response.add(saved);
        }

        for (FilePDF filePDF : response) {
            filePDF.add(linkTo(methodOn(FilePDFController.class).serveFile(filePDF.getUuid().toString())).withSelfRel());
            filePDF.add(linkTo(methodOn(FilePDFController.class).extractText(filePDF.getUuid().toString())).withRel("extractText"));
        }

        //TODO: utilizar en vez de uno por uno? pdfRepository.saveAll(null);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @GetMapping("/{uuid}")
    public ResponseEntity<Resource> serveFile(@PathVariable String uuid) {
        Resource file = fileManger.loadAsResource(String.format("%s.pdf", uuid));
        Optional<FilePDF> pdf_info = pdfRepository.findById(UUID.fromString(uuid));

        if (file == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header(
            HttpHeaders.CONTENT_DISPOSITION, 
            String.format("attachment; filename=\"%s\"", pdf_info.get().getOriginalName())
        ).body(file);
    }

    @GetMapping(value = "/{uuid}/{page}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] showImageWithMediaType(@PathVariable String uuid, @PathVariable int page) throws IOException {

        Path path = Paths.get(String.format("upload-dir/%s-%d.jpg", uuid, page));

        // Regresar la imagen si ya existe en la carpeta.
        if (path.toFile().isFile()) {
            File inFolderImg = FileUtils.getFile(String.format("upload-dir/%s-%d.jpg", uuid, page));
            InputStream targetStream = FileUtils.openInputStream(inFolderImg);
            return IOUtils.toByteArray(targetStream);
        }

        Resource file = fileManger.loadAsResource(String.format("%s.pdf", uuid));
        PDDocument pdf2img = Loader.loadPDF(file.getFile());
        PDFRenderer renderer = new PDFRenderer(pdf2img);

        BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);

        String imageName = String.format("upload-dir/%s-%d.jpg", uuid, page);
        File outputFile = new File(imageName);
        ImageIO.write(image, "jpg", outputFile);

        InputStream targetStream = FileUtils.openInputStream(outputFile);
        return IOUtils.toByteArray(targetStream);
    }

    @GetMapping("/{uuid}/extract-text")
    public ResponseEntity<TextResponse> extractText(@PathVariable String uuid){

        Optional<FilePDF> pdfInfo = pdfRepository.findById(UUID.fromString(uuid));
        
        if (pdfInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TextResponse response = new TextResponse();

        try {

            response.setFile(pdfInfo.get().getOriginalName());
            response.setTotalPages(pdfInfo.get().getTotalPages());
            response.setUploadedAt(pdfInfo.get().getUploadedAt());

            File pdf = FileUtils.getFile(String.format("upload-dir/%s.pdf", uuid));
            PDDocument pdfDoc = Loader.loadPDF(pdf);

            PDFTextStripper pdfStripper = new PDFTextStripper();
            String documentText = pdfStripper.getText(pdfDoc);

            response.setText(documentText);
            pdfDoc.close();

        } catch (Exception e) {
            // TODO: Error al extraer la informacion.
        }
        
        return ResponseEntity.ok(response);
    }

    public static FilePDF extractInformation(MultipartFile file, String uploadName) throws Exception{
        FilePDF pdfInfo = new FilePDF();
        try {
            PDDocument pdfBox = Loader.loadPDF(file.getBytes());
            // PDDocumentInformation information = pdfBox.getDocumentInformation();
        
            pdfInfo.setOriginalName(uploadName);
            pdfInfo.setFileSize(file.getSize());
            pdfInfo.setContentType(file.getContentType());
            pdfInfo.setTotalPages(pdfBox.getNumberOfPages());
            pdfBox.close();
        } catch (IOException e) {
            e.printStackTrace(); // TODO: lanzar un error personalizado
        }         
        return pdfInfo;
    }

    public static void fileValidator(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new EmptyFileException("No se ha elegido ningún archivo en el formulario o el archivo elegido no tiene contenido.");
        }

        if (file.getSize() <= MIN_FILE_SIZE) {
            throw new MinFileSizeException("El archivo no supera en mínimo tamaño, para ser procesado.");
        }
       
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (LIST_OF_ALLOWED_EXTENSIONS.contains(extension) == false) {
            throw new NotAllowedFileException("Formato de archivo invalido. Solo se permiten estos archivos: " + LIST_OF_ALLOWED_EXTENSIONS);
        }

        // PDDocument pdfBox = PDDocument.load(file.getBytes());
        PDDocument pdfBox = Loader.loadPDF(file.getBytes());
        if (pdfBox.isEncrypted()) {
            pdfBox.close();
            throw new FileWithPassException("El Documento esta protegido con una contraseña.");
        }    
    }

}
