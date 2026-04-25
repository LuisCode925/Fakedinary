package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.repository.FileRepository;
import dev.code925.pdf2img.services.FileStorageManger;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/downloads")
public class DownloadController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileStorageManger  fileManger;

      @GetMapping("/{uuid}")
      public ResponseEntity<Resource> serveFile(@PathVariable  @Size(min = 36, max = 36) @org.hibernate.validator.constraints.UUID String uuid) {

      Resource file = fileManger.loadAsResource(String.format("%s.pdf", uuid));

      if (!file.exists()){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      String originalName = fileRepository.getOriginalFilename(UUID.fromString(uuid));

      return ResponseEntity.status(HttpStatus.OK)
              .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", originalName) )
              .body(file);
      }
}
