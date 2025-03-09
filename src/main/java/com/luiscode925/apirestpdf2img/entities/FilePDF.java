
package com.luiscode925.apirestpdf2img.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Luis
 */
@Builder
@Getter @Setter
@AllArgsConstructor
@Entity
public class FilePDF extends RepresentationModel<FilePDF>{

    @Id
    private UUID uuid;

    private String originalName;

    private Long fileSize;

    private String contentType;

    private Integer totalPages;

    private boolean deleted;

    private LocalDateTime uploadedAt;

    public FilePDF(){
        this.uuid = UUID.randomUUID();
        this.deleted = false;
        this.uploadedAt = LocalDateTime.now();
    }
}
