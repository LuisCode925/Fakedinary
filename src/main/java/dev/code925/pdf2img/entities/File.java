
package dev.code925.pdf2img.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@AllArgsConstructor
public class File {

    @Id
    private UUID uuid;

    private String originalName;

    private String sha256sum;

    private Long fileSize;

    private String contentType;

    private Integer totalPages;

    private Set<String> images;

    private boolean deleted;

    private LocalDateTime uploadedAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    private FileMetadata metadata;

    public File(){
        this.uuid = UUID.randomUUID();
        this.deleted = false;
        this.uploadedAt = LocalDateTime.now();
        this.images  = new HashSet<>();
    }

    public  void addImage(String image){
        this.images.add(image);
    }
}
