package dev.code925.pdf2img.entities.DTO;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextResponse {
    
    private String file;
    
    private Integer totalPages;
    
    private String text;

    private LocalDateTime uploadedAt;

}
