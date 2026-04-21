package dev.code925.pdf2img.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileMetadataTest {

    private FileMetadata fileMetadata;

    private final String TEST_AUTHOR = "Jane Doe";
    private final LocalDate NOW = LocalDate.of(2026, Month.MARCH, 16);
    private final LocalDate ONE_MONTH_AGO = LocalDate.of(2026, Month.APRIL, 16);

    @BeforeEach
    void setUp() {
        // Inicializamos el objeto con datos válidos para las pruebas
        fileMetadata = new FileMetadata(
                "Q3 Sales Figures", // title
                TEST_AUTHOR,
                "Marketing Data", // subject
                "document,keywords",
                "System A",
                "Adobe Acrobat Pro v11.0", // producer
                NOW,
                ONE_MONTH_AGO, // modificationDate
                new HashSet<String>(Arrays.asList("Web", "Report")),
                "false", // trapped
                new HashSet<>(), // dimensions (inicialmente vacío)
                new HashSet<>() // fonts (inicialmente vacío)
        );
    }

    @Test
    void testAllArgsConstructor_DataIntegrity() {
        assertEquals(TEST_AUTHOR, fileMetadata.getAuthor());
        assertEquals(NOW, fileMetadata.getCreationDate());
        assertEquals(ONE_MONTH_AGO, fileMetadata.getModificationDate());

        assertThat(fileMetadata.getMetadataKeys()).isNotNull().hasSize(3).contains("report", "web", "data").doesNotContain("popipo");

        assertEquals("System A", fileMetadata.getCreator());
        assertEquals("Web Report", fileMetadata.getKeywords());

        assertEquals("Adobe Acrobat Pro v11.0", fileMetadata.getProducer());
        assertEquals("Marketing Data", fileMetadata.getSubject());
        assertEquals("Q3 Sales Figures", fileMetadata.getTitle());
        assertEquals("Confidential", fileMetadata.getTrapped());
    }

    @Test
    void testMetadataFonts() {
        Set<String> fonts = new HashSet<>(Arrays.asList("Helvetica", "Arial", "Times New Roman", "Futura", "Roboto","Open Sans"));
        fileMetadata.setFonts(fonts);
        assertThat(fileMetadata.getFonts()).isNotNull().hasSize(6).contains("Helvetica", "Arial", "Times New Roman", "Futura", "Roboto","Open Sans");
    }

    @Test
    void testCreationDateAndModificationDate() {
        assertEquals(NOW, fileMetadata.getCreationDate());
        assertEquals(ONE_MONTH_AGO, fileMetadata.getModificationDate());

        assertTrue(fileMetadata.getCreationDate().isBefore(fileMetadata.getModificationDate()));
    }

    // TODO: private Set<FileDimensions> dimensions;
}