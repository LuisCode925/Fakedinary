package com.luiscode925.apirestpdf2img.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextResponse {
    
    private String file;
    
    private Integer totalPages;
    
    private String text;

    private LocalDateTime uploadedAt;

}
