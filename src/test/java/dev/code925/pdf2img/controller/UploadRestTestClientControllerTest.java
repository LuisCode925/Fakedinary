package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.entities.DTO.FileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UploadRestTestClientControllerTest {

    // https://docs.spring.io/spring-framework/reference/testing/resttestclient.html
    // https://www.youtube.com/watch?v=xWcqvrpj2PM

    private RestTestClient client;

    @Value("classpath:Linux Device Drivers.3rd.Edition.pdf")
    private Resource pdfResource;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        // bindToController | Just your controller | Quick init test
        // bindToMockMvc | Controller + Spring MVC | Testing validation, security
        // bindToApplicationContext | Full app (no HTTP) | Real Database test
        // bindToServer | Everything with HTTP | Complete end-to-end test
        // bindToRouterFunction | Functional endpoints | Webflux functional routes
        client = RestTestClient.bindToApplicationContext(context).build();

        //client = RestTestClient.bindToServer().baseUrl("http://localhost:8080").build();
    }

    @Test
    void testHealth() {
        client.get().uri("/pdf/health")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    assertEquals("OK", response.getResponseBody());
                });
    }

    @Test
    void testUploadSingleFile() {
        // MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // body.add("file", file);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", pdfResource);

        FileResponse response = client.post().uri("/pdf/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(FileResponse.class)
                .returnResult().getResponseBody();

    }
}