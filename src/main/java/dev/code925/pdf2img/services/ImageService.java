package dev.code925.pdf2img.services;

import dev.code925.pdf2img.repository.FileRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private FileStorageManger fileManager;

    @Autowired
    private FileRepository fileRepository;

    public Optional<Resource> getImageFromPdf(String uuid, int page) throws IOException {

        String imagePath = String.format("upload-dir/%s-%d.jpg", uuid, page);
        Resource resource = new FileSystemResource(Paths.get(imagePath));

        // Regresar la imagen si ya existe en la carpeta.
        if (resource.exists()) {
            return Optional.of(resource);
        }

        String pdfPath = String.format("%s.pdf", uuid);

        return Optional.of(this.generateImage(pdfPath, uuid, page));
    }
    private Resource generateImage(String pdfPath, String pdfUuid, int page) throws IOException {

        Resource file = this.fileManager.loadAsResource(pdfPath);
        PDDocument pdfBox = Loader.loadPDF(file.getFile());

        // Renderizado de la imagen
        PDFRenderer renderer = new PDFRenderer(pdfBox);
        BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);

        String imagePath = String.format("upload-dir/%s-%d.jpg", pdfUuid, page);
        File outputFile = new File(imagePath);
        ImageIO.write(image, "jpg", outputFile);

        pdfBox.close();

        return new FileSystemResource(Paths.get(imagePath));
    }


}
