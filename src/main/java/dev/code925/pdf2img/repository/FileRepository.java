
package dev.code925.pdf2img.repository;

import dev.code925.pdf2img.entities.File;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FileRepository extends JpaRepository<File, UUID> {

    @Query("SELECT f.totalPages FROM File f WHERE f.uuid = :uuid")
    int getTotalPages(@Param("uuid") UUID uuid);

    @Query("SELECT f.originalName FROM File f WHERE f.uuid = :uuid")
    String getOriginalFilename(@Param("uuid") UUID uuid);

    @Query("SELECT f.images FROM File f WHERE f.uuid = :uuid")
    String getAssetsImages(@Param("uuid") UUID uuid);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE File f SET f.images =:images WHERE f.uuid = :uuid")
    void updateAssetsImages(@Param("images") Set<String> images, @Param("uuid") UUID uuid);

}
