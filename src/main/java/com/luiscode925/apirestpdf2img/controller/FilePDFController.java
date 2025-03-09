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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
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
import com.luiscode925.apirestpdf2img.exception.EmptyFileException;
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
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile requestFile) throws IOException {

        fileValidator(requestFile);

        // Obteniendo el numero de paginas.
        PDDocument pdf = PDDocument.load(requestFile.getBytes());
        
        // Obteniendo la informacion del pdf para la base de datos
        FilePDF pdf_info = new FilePDF();
        pdf_info.setOriginalName(requestFile.getOriginalFilename());
        pdf_info.setFileSize(requestFile.getSize());
        pdf_info.setContentType(requestFile.getContentType());
        pdf_info.setTotalPages(pdf.getNumberOfPages());

        // Guardando la informacion en la base de datos y moviendo el archivo
        FilePDF saved = pdfRepository.save(pdf_info);
        fileManger.store(requestFile, pdf_info.getUuid().toString());

        saved.add(linkTo(methodOn(FilePDFController.class).serveFile(saved.getUuid().toString())).withSelfRel());
        saved.add(linkTo(methodOn(FilePDFController.class).extractText(saved.getUuid().toString())).withSelfRel());

        // TODO: enviar todas las imagenes de las paginas del libro.

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<?> handleFileUploadMultiple(@RequestParam("files") MultipartFile[] arrayRequestFiles) throws IOException {

        List<FilePDF> archivos = new ArrayList<FilePDF>();

        for (MultipartFile requestFile : arrayRequestFiles) {

            fileValidator(requestFile);

            // Obteniendo el numero de paginas.
            PDDocument pdf = PDDocument.load(requestFile.getBytes());
    
            // Obteniendo la informacion del pdf para la base de datos
            FilePDF pdf_info = new FilePDF();
            pdf_info.setOriginalName(requestFile.getOriginalFilename());
            pdf_info.setFileSize(requestFile.getSize());
            pdf_info.setContentType(requestFile.getContentType());
            pdf_info.setTotalPages(pdf.getNumberOfPages());

            fileManger.store(requestFile, pdf_info.getUuid().toString());
        
            archivos.add(pdf_info);
        }

        archivos = pdfRepository.saveAll(archivos);

        return ResponseEntity.ok(archivos);
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
        PDDocument pdf2img = PDDocument.load(file.getFile());
        PDFRenderer renderer = new PDFRenderer(pdf2img);

        BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);

        String imageName = String.format("upload-dir/%s-%d.jpg", uuid, page);
        File outputFile = new File(imageName);
        ImageIO.write(image, "jpg", outputFile);

        InputStream targetStream = FileUtils.openInputStream(outputFile);
        return IOUtils.toByteArray(targetStream);
    }

    @GetMapping("/{uuid}/extract-text")
    public ResponseEntity<?> extractText(@PathVariable String uuid) throws IOException {

        Optional<FilePDF> pdf_info = pdfRepository.findById(UUID.fromString(uuid));
        
        if (pdf_info.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        File pdf = FileUtils.getFile(String.format("upload-dir/%s.pdf", uuid));

        PDFParser parser = new PDFParser(new RandomAccessFile(pdf, "r"));
        parser.parse();

        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        
        PDDocument pdDoc = new PDDocument(cosDoc);
        pdfStripper.setStartPage(0);
        pdfStripper.setEndPage(pdDoc.getNumberOfPages());
        
        String documentText = pdfStripper.getText(pdDoc);
        pdDoc.close();
        
        // Response Body
        Map<String, String> response = new HashMap<String, String>();
        response.put("file", pdf_info.get().getOriginalName());
        response.put("totalPages", ""+pdDoc.getNumberOfPages());
        response.put("text", documentText);

        return ResponseEntity.ok().body(response);
    }

    public static void fileValidator(MultipartFile file) {

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
        
    }

}
