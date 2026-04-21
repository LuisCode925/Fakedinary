package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.exception.OutOfRangeException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import dev.code925.pdf2img.entities.DTO.ErrorResponse;
import dev.code925.pdf2img.exception.NotAllowedFileException;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericException(Exception ex) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Ha ocurrido un error.");
        response.setException(String.format("Exception %s", ex.getMessage()));
        response.setExceptionClass(ex.getClass().toString());

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(OutOfRangeException.class)
    public ResponseEntity<ErrorResponse> badPathVariables(OutOfRangeException ex) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Se esta intentando generar una imagen de un pagina inexistente en el documento.");
        response.setException(String.format("Exception %s", ex.getMessage()));
        response.setExceptionClass(ex.getClass().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> badPathVariables(ConstraintViolationException ex) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Ha ocurrido un error en algún parámetro de la url.");
        response.setException(String.format("Exception %s", ex.getMessage()));
        response.setExceptionClass(ex.getClass().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> fileToBig(MaxUploadSizeExceededException ex) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Ha ocurrido un error, alguno de los archivos supera el limite del servicio.");
        response.setException(String.format("Exception %s", ex.getMessage()));
        response.setExceptionClass(ex.getClass().toString());

        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(response);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> failToUpload(MultipartException ex) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Ha ocurrido un error al subir su(s) archivo(s), alguno de los archivos supera el limite del servicio.");
        response.setException(String.format("Exception %s", ex.getMessage()));
        response.setExceptionClass(ex.getClass().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NotAllowedFileException.class)
    public ResponseEntity<ErrorResponse> failToUpload(NotAllowedFileException ex) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Ha ocurrido problema con la extension de sus archivos.");
        response.setException(String.format("Exception %s", ex.getMessage()));
        response.setExceptionClass(ex.getClass().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
