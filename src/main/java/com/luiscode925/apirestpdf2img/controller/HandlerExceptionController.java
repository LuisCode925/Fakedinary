package com.luiscode925.apirestpdf2img.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.luiscode925.apirestpdf2img.exception.NotAllowedFileException;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> genericException(Exception ex) {
        Map<String, String> errores = new HashMap<>();
        errores.put("message", "Ha ocurrido un error.");
        errores.put("Exception", ex.getMessage());
        errores.put("date", LocalDateTime.now().toString());
        return ResponseEntity.internalServerError().body(errores);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> fileToBig(MaxUploadSizeExceededException ex) {
        Map<String, String> errores = new HashMap<>();
        errores.put("message", "Ha ocurrido un error, alguno de los archivos supera el limite del servicio.");
        errores.put("date", LocalDateTime.now().toString());
        return ResponseEntity.internalServerError().body(errores);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, String>> failToUpload(MultipartException ex) {
        Map<String, String> errores = new HashMap<>();
        errores.put("message",
                "Ha ocurrido un error al subir su(s) archivo(s), alguno de los archivos supera el limite del servicio.");
        errores.put("date", LocalDateTime.now().toString());
        return ResponseEntity.internalServerError().body(errores);
    }

    @ExceptionHandler(NotAllowedFileException.class)
    public ResponseEntity<Map<String, String>> failToUpload(NotAllowedFileException ex) {
        Map<String, String> errores = new HashMap<>();
        errores.put("message", "Ha ocurrido problema con la extension.");
        errores.put("Exception", ex.getMessage());
        errores.put("date", LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(errores);
    }
}
