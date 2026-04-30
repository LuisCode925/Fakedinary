package dev.code925.pdf2img.entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRResponse {

    String originalFile;

    Long size;

    String contentType;

    Set<String> languages;

    String text;
}
