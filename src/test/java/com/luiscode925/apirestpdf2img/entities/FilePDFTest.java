package com.luiscode925.apirestpdf2img.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilePDFTest {

    private FilePDF pdf;

    @BeforeEach
    void setUp(){
        pdf = new FilePDF();
    }

    @Test
    void testUuidFilePDF() {
        assertEquals(36, pdf.getUuid().toString().length());
    }

    @Test
    void testOriginalNameFilePDF() {
        pdf.setOriginalName("TestName");
        assertEquals("TestName", pdf.getOriginalName());
    }

    @Test
    void testFileSizeFilePDF() {
        pdf.setFileSize(1027L);
        assertEquals(1027L, pdf.getFileSize());
    }

    @Test
    void testContentTypeFilePDF() {
        pdf.setContentType("application/pdf");
        assertNotNull(pdf.getContentType());
        assertEquals("application/pdf", pdf.getContentType());
    }

    @Test
    void testTotalPagesFilePDF(){
        pdf.setTotalPages(783);
        assertEquals(783, pdf.getTotalPages());
    }

    @Test
    void testIsDeletedFilePDF() {
        assertFalse(pdf.isDeleted());
    }

    @Test
    void testUpdatedAtFilePDF() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.getYear(), pdf.getUploadedAt().getYear());
        assertEquals(now.getMonth(), pdf.getUploadedAt().getMonth());
        assertEquals(now.getDayOfYear(), pdf.getUploadedAt().getDayOfYear());
    }

}
