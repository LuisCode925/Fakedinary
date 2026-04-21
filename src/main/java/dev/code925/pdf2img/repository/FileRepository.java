
package dev.code925.pdf2img.repository;

import dev.code925.pdf2img.entities.File;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, UUID> {

    @Query("SELECT f.totalPages FROM File f WHERE f.uuid = :uuid")
    int getTotalPages(@Param("uuid") UUID uuid);
}
