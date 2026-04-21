package dev.code925.pdf2img.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileTest {

    private File file;
    private UUID testUuid;
    private final LocalDateTime UPLOAD_DATE = LocalDateTime.of(2020, Month.AUGUST, 13, 3, 33);

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        file = new File();
    }

    @Test
    void testObjectInitialization() {
        assertNotNull(file, "The File object should not be null.");
    }

    @Test
    void testGettersAndSetters() {
        file.setUuid(testUuid);
        file.setOriginalName("TestFile.pdf");
        file.setFileSize(1024L);

        file.setContentType("application/pdf");
        file.setTotalPages(9);
        file.setDeleted(false);
        file.setUploadedAt(UPLOAD_DATE);

        assertEquals(testUuid, file.getUuid());
        assertEquals("TestFile.pdf", file.getOriginalName());
        assertEquals(1024L, file.getFileSize());

        assertEquals("application/pdf", file.getContentType());
        assertEquals(9, file.getTotalPages());
        assertFalse(file.isDeleted());
        assertEquals(UPLOAD_DATE, file.getUploadedAt());
    }

    // TODO: test private FileMetadata metadata;

}