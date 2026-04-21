package dev.code925.pdf2img.services;

import dev.code925.pdf2img.entities.File;
import dev.code925.pdf2img.entities.FileDimensions;
import dev.code925.pdf2img.entities.FileMetadata;
import dev.code925.pdf2img.exception.ExtractDataException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class MetadataService {

    public File extractInformation(MultipartFile file, String requestOriginalName) throws Exception {
        File pdfInfo = new File(); // uuid, deleted y uploadedAt tienen valores por defecto.
        try {
            PDDocument pdfBox = Loader.loadPDF(file.getBytes());

            pdfInfo.setOriginalName(requestOriginalName);
            pdfInfo.setSha256sum(this.calculateSHA256(file));
            pdfInfo.setFileSize(file.getSize());
            pdfInfo.setContentType(file.getContentType());
            pdfInfo.setTotalPages(pdfBox.getNumberOfPages());

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
            FileDimensions mediaBox = new FileDimensions(box.getWidth(), box.getHeight());
            allDimensions.add(mediaBox);

        }
        meta.setDimensions(allDimensions);
        meta.setFonts(pdfFonts);

        return meta;
    }
}
