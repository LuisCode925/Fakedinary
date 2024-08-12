package com.luiscode925.apirestpdf2img.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.luiscode925.apirestpdf2img.repository.FilePDFRepository;
import com.luiscode925.apirestpdf2img.services.FileStorageManagerImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pdf")
public class FilePDFAsyncController {

    @Autowired
    FileStorageManagerImpl fileStoreManager;

    @Autowired
    FilePDFRepository filePDFRepository;

    // curl -X POST -F
    // "files=@C:/Users/Luis/Desktop/mapstruct-reference-guide_ESP.pdf" -F
    // "files=@C:/Users/Luis/Desktop/test.txt"
    // http://localhost:8080/pdf/upload-async
    /*
     * @Async
     * 
     * @PostMapping("/upload-async")
     * public CompletableFuture<ResponseEntity<?>> handleConcurrentFilesUpload(
     * 
     * @RequestParam("files") MultipartFile[] files) throws IOException {
     * 
     * // Handle empty file error
     * if (files.length == 0) {
     * return CompletableFuture
     * .completedFuture(ResponseEntity.badRequest().body("No files submitted"));
     * }
     * // File upload process is submitted
     * else {
     * 
     * for (MultipartFile file : files) {
     * fileStoreManager.save(file);
     * }
     * return
     * CompletableFuture.completedFuture(ResponseEntity.ok("File upload started"));
     * }
     * }
     */

}
