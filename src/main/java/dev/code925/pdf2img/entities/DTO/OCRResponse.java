package dev.code925.pdf2img.entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRResponse {

    String originalFile;

    Long size;

    String contentType;

    String text;
}
