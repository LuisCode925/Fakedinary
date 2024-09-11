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
    FilePDFRepository filePDFRepository;

    @Autowired
    FileStorageManger fileStorageManger;
    private FilePDF filePDF;

    // En caso de haber mas campos en el formulario se agregarían
    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        fileValidator(file);

        // Obteniendo el numero de paginas.
        PDDocument uploaded_pdf = PDDocument.load(file.getBytes());
        int num_pages = uploaded_pdf.getNumberOfPages();

        FilePDF pdf_info = new FilePDF();
        UUID uuid = UUID.randomUUID();
        pdf_info.setUuid(uuid);
        pdf_info.setOrigName(file.getOriginalFilename());
        pdf_info.setFileSize(file.getSize());
        pdf_info.setContentType(file.getContentType());
        pdf_info.setNumPages(num_pages);

        FilePDF saved = filePDFRepository.save(pdf_info);

        fileStorageManger.store(file, uuid.toString());

        // serveFile -> Enlace para ver el pdf
        saved.add(linkTo(methodOn(FilePDFController.class).serveFile(saved.getUuid().toString())).withSelfRel());

        // extractText -> para que te mande el texto del documento.
        saved.add(linkTo(methodOn(FilePDFController.class).extractText(saved.getUuid().toString())).withSelfRel());

        /*
         * saved.add(
         * linkTo(
         * methodOn(FilePDFController.class)
         * .showImageWithMediaType(saved.getUuid().toString(), 1)
         * 
         * )
         * .withRel("images")
         * );
         * 
         */

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<?> handleFileUploadMultiple(@RequestParam("files") MultipartFile[] files)
            throws IOException {

        List<FilePDF> archivos = new ArrayList<FilePDF>();

        for (MultipartFile file : files) {

            fileValidator(file);

            // Obteniendo el numero de paginas.
            PDDocument uploaded_pdf = PDDocument.load(file.getBytes());
            int num_pages = uploaded_pdf.getNumberOfPages();

            FilePDF pdf_info = new FilePDF();
            UUID uuid = UUID.randomUUID();
            pdf_info.setUuid(uuid);
            pdf_info.setOrigName(file.getOriginalFilename());
            pdf_info.setFileSize(file.getSize());
            pdf_info.setContentType(file.getContentType());
            pdf_info.setNumPages(num_pages);

            archivos.add(pdf_info);

            fileStorageManger.store(file, uuid.toString());
            // if (System.getProperty("os.name").contains("Windows")) {}

        }
        return ResponseEntity.ok(filePDFRepository.saveAll(archivos));
    }

    @ResponseBody
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = fileStorageManger.loadAsResource(filename + ".pdf");

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping(value = "/{filename}/{page}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] showImageWithMediaType(@PathVariable String filename, @PathVariable int page)
            throws IOException {

        Path path = Paths.get(String.format("pdf-utils/upload-dir/%s-%d.jpg", filename, page));

        // Regresar la imagen si ya existe en la carpeta.
        if (path.toFile().isFile()) {
            File inFolderImg = FileUtils.getFile(String.format("pdf-utils/upload-dir/%s-%d.jpg", filename, page));
            InputStream targetStream = FileUtils.openInputStream(inFolderImg);
            return IOUtils.toByteArray(targetStream);
        }

        Resource file = fileStorageManger.loadAsResource(filename + ".pdf");
        PDDocument pdf2img = PDDocument.load(file.getFile());
        PDFRenderer renderer = new PDFRenderer(pdf2img);

        BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);

        String imageName = String.format("pdf-utils/upload-dir/" + filename + "-%d.jpg", page);
        File outputFile = new File(imageName);
        ImageIO.write(image, "jpg", outputFile);

        InputStream targetStream = FileUtils.openInputStream(outputFile);
        return IOUtils.toByteArray(targetStream);
    }

    @GetMapping("/{filename}/extract-text")
    public ResponseEntity<?> extractText(@PathVariable String filename) throws IOException {

        Map<String, String> response = new HashMap<String, String>();

        response.put("file", filename);
        File pdfExtractText = FileUtils.getFile(String.format("upload-dir/%s.pdf", filename));
        PDFParser parser = new PDFParser(new RandomAccessFile(pdfExtractText, "r"));

        parser.parse();
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();

        PDDocument pdDoc = new PDDocument(cosDoc);
        pdfStripper.setStartPage(0);
        response.put("start_at", "0");
        pdfStripper.setEndPage(pdDoc.getNumberOfPages());
        response.put("end_at", "" + pdDoc.getNumberOfPages());
        String pdf_text = pdfStripper.getText(pdDoc);
        response.put("text", pdf_text);
        pdDoc.close();

        return ResponseEntity.ok().body(response);
    }

    @SuppressWarnings("null")
    public static void fileValidator(MultipartFile file) {

        if (file.isEmpty()) {
            throw new EmptyFileException(
                    "No se ha elegido ningún archivo en el formulario o el archivo elegido no tiene contenido.");
        }

        int index = file.getOriginalFilename().lastIndexOf('.');

        if (index > 0) {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (LIST_OF_ALLOWED_EXTENSIONS.contains(extension) == false) {
                throw new NotAllowedFileException(
                        "Invalid file format. Only these files are allowed: " + LIST_OF_ALLOWED_EXTENSIONS);
            }
        }

        long fileSize = file.getSize();
        if (fileSize <= MIN_FILE_SIZE) {
            throw new MinFileSizeException("El archivo no supera en mínimo tamaño, para ser procesado.");
        }
    }

}
