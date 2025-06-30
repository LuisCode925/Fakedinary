package com.luiscode925.apirestpdf2img.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.luiscode925.apirestpdf2img.entities.MetaInfo;
import com.luiscode925.apirestpdf2img.entities.PDFSize;
import com.luiscode925.apirestpdf2img.entities.TextResponse;
import com.luiscode925.apirestpdf2img.exception.EmptyFileException;
import com.luiscode925.apirestpdf2img.exception.FileWithPassException;
import com.luiscode925.apirestpdf2img.exception.MinFileSizeException;
import com.luiscode925.apirestpdf2img.exception.NotAllowedFileException;
import com.luiscode925.apirestpdf2img.mappers.FilePDFMapper;
import com.luiscode925.apirestpdf2img.repository.FilePDFRepository;
import com.luiscode925.apirestpdf2img.services.FileStorageManger;
import com.luiscode925.apirestpdf2img.utils.DateTimeConverter;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@RestController
@RequestMapping("/pdf")
public class FilePDFController {

    private static final long MIN_FILE_SIZE = 1024;
    private static final List<String> LIST_OF_ALLOWED_EXTENSIONS = List.of("pdf");

    @Autowired
    FilePDFRepository pdfRepository;

    @Autowired
    FileStorageManger fileManger;
    
    @Autowired
    FilePDFMapper FilePDFMapper;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile requestFile) throws Exception {

        // Validacion 
        fileValidator(requestFile);

        // Extraccion de la informacion 
        FilePDF saved = pdfRepository.save(extractInformation(requestFile, requestFile.getOriginalFilename()));

        // Mover el archivo a el directorio 
        fileManger.store(requestFile, saved.getUuid().toString());

        return ResponseEntity.ok(FilePDFMapper.toFilePDFResponse(saved));
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<?> handleFileUploadMultiple(@RequestParam("files") MultipartFile[] multipleFiles) throws Exception {

        List<FilePDF> response = new ArrayList<FilePDF>();

        for (MultipartFile singleFile : multipleFiles) {
            fileValidator(singleFile);
            FilePDF saved = extractInformation(singleFile, singleFile.getOriginalFilename());
            fileManger.store(singleFile, saved.getUuid().toString());
            response.add(saved);
        }
 
        List<FilePDF> repositoryResponse = pdfRepository.saveAll(response);

        return ResponseEntity.ok(FilePDFMapper.toListResponse(repositoryResponse));
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

    @GetMapping(value = "/{uuid}/{page}")
    public ResponseEntity<?> showImageWithMediaType(@PathVariable String uuid, @PathVariable int page) throws IOException {

        Path path = Paths.get(String.format("upload-dir/%s-%d.jpg", uuid, page));

        // Regresar la imagen si ya existe en la carpeta.
        if (path.toFile().isFile()) {
            File inFolderImg = FileUtils.getFile(String.format("upload-dir/%s-%d.jpg", uuid, page));
            InputStream targetStream = FileUtils.openInputStream(inFolderImg);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(IOUtils.toByteArray(targetStream));
        }

        // TODO lanzar error con una pagina out of range

        Resource file = fileManger.loadAsResource(String.format("%s.pdf", uuid));
        PDDocument pdfBox = Loader.loadPDF(file.getFile());

        // Renderizado de la imagen
        PDFRenderer renderer = new PDFRenderer(pdfBox);
        BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);

        File outputFile = new File(String.format("upload-dir/%s-%d.jpg", uuid, page));
        ImageIO.write(image, "jpg", outputFile);

        InputStream targetStream = FileUtils.openInputStream(outputFile);

        pdfBox.close();

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(IOUtils.toByteArray(targetStream));
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
        
            pdfInfo.setOriginalName(uploadName);
            pdfInfo.setFileSize(file.getSize());
            pdfInfo.setContentType(file.getContentType());
            pdfInfo.setTotalPages(pdfBox.getNumberOfPages());
            
            PDDocumentInformation metadata = pdfBox.getDocumentInformation();
            MetaInfo meta = new MetaInfo();
            meta.setAuthor(metadata.getAuthor());
            // getCOSObject()
            meta.setCreationDate(DateTimeConverter.toLocalDateTime(metadata.getCreationDate()));
            meta.setCreator(metadata.getCreator());
            // getCustomMetadataValue(String fieldName)
            meta.setKeywords(metadata.getKeywords());
            meta.setMetadataKeys(metadata.getMetadataKeys());
            meta.setModificationDate(DateTimeConverter.toLocalDateTime(metadata.getModificationDate()));
            meta.setProducer(metadata.getProducer());
            // getPropertyStringValue(String propertyKey)
            meta.setSubject(metadata.getSubject());
            meta.setTitle(metadata.getTitle());
            meta.setTrapped(metadata.getTrapped());
            // Adicionales
            meta.setPdfVersion(pdfBox.getVersion());
            meta.setTotalPages(pdfBox.getNumberOfPages());

            Set<PDFSize> allDimensions =  new HashSet<>();
            for (PDPage page : pdfBox.getPages()) {
                // TODO: Obtencion de las fuentes
                // Obtencion de las medidas del docuemnto
                PDRectangle box = page.getMediaBox();
                PDFSize mediaBox = new PDFSize(box.getWidth(), box.getHeight());
                allDimensions.add(mediaBox);
            }
            meta.setDimensions(allDimensions);

            pdfInfo.setMetadata(meta);
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
