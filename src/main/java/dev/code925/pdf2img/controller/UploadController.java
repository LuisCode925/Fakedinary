package dev.code925.pdf2img.controller;

import java.util.*;
import java.util.stream.Collectors;

import dev.code925.pdf2img.entities.DTO.FileResponse;
import dev.code925.pdf2img.services.UploadService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pdf")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping(path = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Optional<FileResponse> response = this.uploadService.uploadSingleFile(file);
        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response.get());
    }

    @SneakyThrows
    @PostMapping("/upload-multiple")
    public ResponseEntity<?> handleFileUploadMultiple(@RequestParam("files") MultipartFile[] multipleFiles) throws Exception {

        if(multipleFiles.length == 0){
            Map<String, String> response = new HashMap<>();
            response.put("message","Por favor, selecciona al menos un archivo.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Set<FileResponse> response = Arrays.stream(multipleFiles)
                .map(this.uploadService::uploadSingleFile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
