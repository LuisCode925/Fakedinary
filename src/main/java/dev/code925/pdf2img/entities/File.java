
package dev.code925.pdf2img.entities;

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

@Entity
@Getter @Setter
@AllArgsConstructor
public class File {

    @Id
    private UUID uuid;

    private String originalName;

    private String sha256sum;

    private Long fileSize;

    private String contentType;

    private Integer totalPages;

    private boolean deleted;

    private LocalDateTime uploadedAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    private FileMetadata metadata;

    public File(){
        this.uuid = UUID.randomUUID();
        this.deleted = false;
        this.uploadedAt = LocalDateTime.now();
    }
}
