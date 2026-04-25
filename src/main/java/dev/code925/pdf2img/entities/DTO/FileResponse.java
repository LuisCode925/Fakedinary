package dev.code925.pdf2img.entities.DTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import dev.code925.pdf2img.entities.FileMetadata;
import dev.code925.pdf2img.entities.HashImage;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse extends RepresentationModel<FileResponse> {

    private UUID uuid;

    private String originalName;

    private Long fileSize;

    private String contentType;

    private Integer totalPages;

    private boolean deleted;

    private LocalDateTime uploadedAt;

    private FileMetadata metadata;

}
