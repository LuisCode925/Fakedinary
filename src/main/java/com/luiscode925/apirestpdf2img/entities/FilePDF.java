
package com.luiscode925.apirestpdf2img.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Luis
 */
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FilePDF extends RepresentationModel<FilePDF>{

    @Id
    private UUID uuid;

    private String origName;

    private Long fileSize;

    private String contentType;

    private Integer numPages;

    @Builder.Default
    private boolean deleted = false;

    @Builder.Default
    private LocalDateTime uploadAt = LocalDateTime.now();
}
