package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.repository.FileRepository;
import dev.code925.pdf2img.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UploadController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private FileRepository fileRepository;

    @BeforeEach
    void setUp() {
        UUID uuid = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /images/{uuid}/{page} - Bad Uuid")
    void getImageFromPdfWithBadUuid() throws Exception {

        String invalidUuid = "40820653-5d89-4552-b478";
        String image = String.format("/images/%s/%d", invalidUuid, 1);

        mockMvc.perform(get(image)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /images/{uuid}/{page} - Bad Page Number")
    void getImageFromPdfWithBadPageNumber() throws Exception {

        String uuid = "baee570d-c697-41bd-b95d-86e35a185fb7";
        String image = String.format("/images/%s/%d", uuid, -1);

        mockMvc.perform(get(image)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /images/{uuid}/{page} - Out of Range Page Number")
    void imagePageIsOutOfRange() throws Exception {

        String uuid = "baee570d-c697-41bd-b95d-86e35a185fb7";
        String image = String.format("/images/%s/%d", uuid, 35);

        Mockito.when(fileRepository.getTotalPages(UUID.fromString(uuid))).thenReturn(30);

        mockMvc.perform(get(image)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /images/{uuid}/{page} - Image generated")
    void imageGeneratedSuccessfully() throws Exception {

        Resource imagen = new ClassPathResource("1.jpeg");
        byte[] expectedImageInBytes = imagen.getInputStream().readAllBytes();

        String uuid = "baee570d-c697-41bd-b95d-86e35a185fb7";
        String requestUrl = String.format("/images/%s/%d", uuid, 35);

        Mockito.when(fileRepository.getTotalPages(UUID.fromString(uuid))).thenReturn(300);
        Mockito.when(imageService.getImageFromPdf(uuid, 35)).thenReturn(Optional.of(imagen));

        mockMvc.perform(get(requestUrl))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(expectedImageInBytes))
                .andExpect(status().isOk());
    }

}