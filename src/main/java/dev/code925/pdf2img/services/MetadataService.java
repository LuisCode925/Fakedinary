package dev.code925.pdf2img.services;

import dev.code925.pdf2img.entities.File;
import dev.code925.pdf2img.entities.FileDimensions;
import dev.code925.pdf2img.entities.FileMetadata;
import dev.code925.pdf2img.exception.ExtractDataException;
import dev.code925.pdf2img.repository.FileRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.security.MessageDigest;
import java.io.IOException;
import java.util.*;

@Log4j2
@Service
public class MetadataService {

    @Autowired
    FileStorageManger fileManger;

    @Autowired
    FileRepository fileRepository;

    public File extractInformation(MultipartFile file, String requestOriginalName) throws Exception {
        File pdfInfo = new File(); // uuid, deleted y uploadedAt tienen valores por defecto.
        try {
            PDDocument pdfBox = Loader.loadPDF(file.getBytes());

            pdfInfo.setOriginalName(requestOriginalName);
            pdfInfo.setSha256sum(this.calculateSHA256(file));
            pdfInfo.setFileSize((Long) file.getSize());
            pdfInfo.setContentType(file.getContentType());
            pdfInfo.setTotalPages((Integer) pdfBox.getNumberOfPages());

            // Obtención de la metadata de archivo subido
            FileMetadata meta = this.extractMetadata(pdfBox.getDocumentInformation(), pdfBox);
            pdfInfo.setMetadata(meta);

            pdfBox.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExtractDataException("Hubo un error al extraer la información del documento.");
        }
        return pdfInfo;
    }

    private String calculateSHA256(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192]; // Buffer de 8KB
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        // Convertir los bytes del hash a formato Hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest.digest()) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private FileMetadata extractMetadata(PDDocumentInformation metadata, PDDocument pdfBox) throws IOException {
        FileMetadata meta = new FileMetadata();

        // Metadata Estándar (Document Information)
        meta.setTitle(metadata.getTitle());
        meta.setAuthor(metadata.getAuthor());
        meta.setSubject(metadata.getSubject());
        meta.setKeywords(metadata.getKeywords());
        meta.setCreator(metadata.getCreator());
        meta.setProducer(metadata.getProducer());
        meta.setCreationDate(
                metadata.getCreationDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        meta.setModificationDate(
                metadata.getModificationDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        meta.setMetadataKeys(metadata.getMetadataKeys()); // Propiedades Personalizadas
        meta.setTrapped(metadata.getTrapped()); // Corrección para impresión

        // Metadata Avanzada (XMP Metadata) PDF/A
        // document.getDocumentCatalog().getMetadata()
        // org.apache.pdfbox.pdmodel.common.PDMetadata

        // Dimensions & Fonts
        Set<FileDimensions> allDimensions = new HashSet<>();
        Set<String> pdfFonts = new HashSet<>();
        for (PDPage page : pdfBox.getPages()) {
            // Obtención de las fuentes
            PDResources resources = page.getResources();
            if (resources != null) {
                for (org.apache.pdfbox.cos.COSName fontName : resources.getFontNames()) {
                    PDFont font = resources.getFont(fontName);
                    pdfFonts.add(font.getName());
                }
            }
            // Obtención de las medidas del document
            PDRectangle box = page.getMediaBox();
            FileDimensions mediaBox = new FileDimensions((Float) box.getWidth(),(Float) box.getHeight());
            allDimensions.add(mediaBox);

        }
        meta.setDimensions(allDimensions);
        meta.setFonts(pdfFonts);

        return meta;
    }

    @Async
    protected void extractImages(UUID uuid) throws IOException {

        Resource file = fileManger.loadAsResource(String.format("%s.pdf", uuid.toString()));
        PDDocument document = Loader.loadPDF(file.getFile());
        Set<String> images = new HashSet<>();

        for (PDPage page : document.getPages()) {
            PDResources resources = page.getResources();
            for (COSName name : resources.getXObjectNames()) {
                PDXObject object = resources.getXObject(name);
                if (object instanceof PDImageXObject image) {
                    UUID imageUuid = UUID.randomUUID();
                    String imageName = String.format("upload-dir/%s.png", imageUuid.toString());

                    java.io.File outputFile = new java.io.File(imageName);
                    ImageIO.write(image.getImage(), "png", outputFile);

                    images.add(imageUuid.toString());
                }
            }
        }

        fileRepository.updateAssetsImages(images, uuid);
        log.info(String.format("El documento %s tiene %d images.", uuid, images.size()));
    }
}
