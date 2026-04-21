package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.entities.DTO.FileResponse;
import dev.code925.pdf2img.services.FileStorageManger;
import dev.code925.pdf2img.services.UploadService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UploadController.class)
class UploadControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private UploadService uploadService;

    @MockitoBean
    private FileStorageManger fileStorageManger;

    private FileResponse response;

    @Value("classpath:Linux Device Drivers.3rd.Edition.pdf")
    private Resource pdfResource;

    @BeforeEach
    void setUp() {
        response = new FileResponse();
        response.setUuid(UUID.fromString("40820653-5d89-4552-b478-bfe75513d4aa"));
        response.setOriginalName("Linux Device Drivers.3rd.Edition.pdf");
        response.setFileSize(12825921L);
        response.setContentType("application/pdf");
        response.setTotalPages(630);
        response.setDeleted(false);
        response.setUploadedAt(LocalDateTime.parse("2026-04-18T10:20:35.90714363"));

        /*
         * Cuando marcas una clase de prueba con @WebMvcTest, Spring Boot:
         * Desactiva la configuración completa de la aplicación.
         * Solo escanea componentes web: Controller, ControllerAdvice, JsonComponent,
         * Filter, WebMvcConfigurer, etc.
         * No carga componentes de servicio (@Service), repositorios (@Repository) ni
         * entidades de JPA.
         */
    }

    @Test
    @DisplayName("POST /pdf/upload - MultipartFile")
    void uploadSingleFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                pdfResource.getFilename(),
                "application/pdf",
                pdfResource.getContentAsByteArray());
        Mockito.when(uploadService.uploadSingleFile(file)).thenReturn(Optional.of(response));

        var response = mockMvc.perform(MockMvcRequestBuilders.multipart("/pdf/upload").file(file)).getResponse();
        String json = response.getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        FileResponse dtoResponse = mapper.readValue(json, FileResponse.class);

        assertEquals(201, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals("40820653-5d89-4552-b478-bfe75513d4aa", dtoResponse.getUuid().toString());
        // TODO: hacer las otras assertions.
    }

}