package dev.code925.pdf2img.services;

import dev.code925.pdf2img.entities.DTO.FileResponse;
import dev.code925.pdf2img.entities.File;
import dev.code925.pdf2img.exception.EmptyFileException;
import dev.code925.pdf2img.exception.FileWithPassException;
import dev.code925.pdf2img.exception.MinFileSizeException;
import dev.code925.pdf2img.exception.NotAllowedFileException;
import dev.code925.pdf2img.mappers.FileMapper;
import dev.code925.pdf2img.repository.FileRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UploadService {

    private static final long MIN_FILE_SIZE = 1024;
    private static final List<String> LIST_OF_ALLOWED_EXTENSIONS = List.of("pdf");

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileStorageManger fileManger;

    @Autowired
    FileMapper FileMapper;

    @Autowired
    MetadataService metadataService;

    public Optional<FileResponse> uploadSingleFile(MultipartFile fileUploaded) throws Exception {

        this.fileValidator(fileUploaded);

        // Extracción de la información
        File extractedInfo = this.metadataService.extractInformation(fileUploaded, fileUploaded.getOriginalFilename());
        File saved = fileRepository.save(extractedInfo);

        // Mover el archivo a el directorio
        fileManger.store(fileUploaded, saved.getUuid().toString());

        // Extrayendo las imágenes originales del document - SIN 762ms  CON 1.78s
        metadataService.extractImages(saved.getUuid());

        return Optional.of(FileMapper.toFileResponse(saved));
    }

    public void fileValidator(MultipartFile file) throws IOException {

        // Métodos principales del MultipartFile
        // getName(): Devuelve el nombre del parámetro en el formulario (ej: "foto
        // Perfil").
        // getOriginalFilename(): El nombre real del archivo en la computadora del
        // cliente (ej: "vacaciones.jpg").
        // getContentType(): El tipo MIME (ej: image/png, application/pdf).
        // getSize(): El tamaño en bytes.
        // getBytes(): Permite obtener todo el contenido binario de una sola vez.
        // getInputStream(): Ideal para procesar archivos grandes sin cargar todo en
        // memoria (streaming).
        // transferTo(File destino): Un método muy útil para guardar el archivo
        // directamente en una ruta del servidor.

        if (file.isEmpty()) {
            throw new EmptyFileException(
                    "No se ha elegido ningún archivo en el formulario o el archivo elegido no tiene contenido.");
        }

        if (file.getSize() <= MIN_FILE_SIZE) {
            throw new MinFileSizeException("El archivo no supera en mínimo tamaño, para ser procesado.");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!LIST_OF_ALLOWED_EXTENSIONS.contains(extension)) {
            throw new NotAllowedFileException(
                    "Formato de archivo invalido. Solo se permiten estos archivos: " + LIST_OF_ALLOWED_EXTENSIONS);
        }

        // PDDocument pdfBox = PDDocument.load(file.getBytes());
        PDDocument pdfBox = Loader.loadPDF(file.getBytes());
        if (pdfBox.isEncrypted()) {
            pdfBox.close();
            throw new FileWithPassException("El Documento esta protegido con una contraseña.");
        }
    }

}
