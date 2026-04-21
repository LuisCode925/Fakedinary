package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.exception.OutOfRangeException;
import dev.code925.pdf2img.repository.FileRepository;

import dev.code925.pdf2img.services.ImageService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private FileRepository fileRepository;

    @GetMapping(path = "/{uuid}/{page}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> showImage(
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


}