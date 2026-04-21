package dev.code925.pdf2img.entities.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private String message;

    private String exception;

    private String exceptionClass;

    private LocalDateTime dateTime;

    public ErrorResponse() {
        this.dateTime = LocalDateTime.now();
    }

}
