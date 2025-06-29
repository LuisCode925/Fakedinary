
package com.luiscode925.apirestpdf2img.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter @Setter
@AllArgsConstructor
@Entity
public class FilePDF {

    @Id
    private UUID uuid;

    private String originalName;

    private Long fileSize;

    private String contentType;

    private Integer totalPages;

    private boolean deleted;

    private LocalDateTime uploadedAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    private MetaInfo metadata;

    public FilePDF(){
        this.uuid = UUID.randomUUID();
        this.deleted = false;
        this.uploadedAt = LocalDateTime.now();
    }
}
