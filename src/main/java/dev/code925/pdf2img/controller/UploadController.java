package dev.code925.pdf2img.controller;

import java.util.Optional;

import dev.code925.pdf2img.entities.DTO.FileResponse;
import dev.code925.pdf2img.services.UploadService;
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

    @PostMapping("/upload-multiple")
    public ResponseEntity<?> handleFileUploadMultiple(@RequestParam("files") MultipartFile[] multipleFiles)
            throws Exception {
        // Optional<?> response = this.uploadService.uploadMultipleFiles(multipleFiles);
        // return ResponseEntity.ok(FilePDFMapper.toListResponse(repositoryResponse));
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /*
     * @ResponseBody
     * 
     * @GetMapping("/{uuid}")
     * public ResponseEntity<Resource> serveFile(@PathVariable String uuid) {
     * Resource file = fileManger.loadAsResource(String.format("%s.pdf", uuid));
     * Optional<File> pdf_info = fileRepository.findById(UUID.fromString(uuid));
     * 
     * if (file == null){
     * return ResponseEntity.notFound().build();
     * }
     * 
     * return ResponseEntity.ok().header(
     * HttpHeaders.CONTENT_DISPOSITION,
     * String.format("attachment; filename=\"%s\"",
     * pdf_info.get().getOriginalName())
     * ).body(file);
     * }
     */

}
