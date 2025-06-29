package com.luiscode925.apirestpdf2img.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilePDFResponse extends RepresentationModel<FilePDFResponse>{

    private UUID uuid;

    private String originalName;

    private Long fileSize;

    private String contentType;

    private Integer totalPages;

    private boolean deleted;

    private LocalDateTime uploadedAt;
    
    private MetaInfo metadata;

}
