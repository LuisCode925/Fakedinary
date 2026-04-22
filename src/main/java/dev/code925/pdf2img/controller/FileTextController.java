package dev.code925.pdf2img.controller;

import jakarta.validation.constraints.Size;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;

@Validated
@RestController
@RequestMapping("/pdf")
public class FileTextController {

    @GetMapping("/{uuid}/text")
    public ResponseEntity<StreamingResponseBody> extractText(@PathVariable @Size(min = 36, max = 36) @org.hibernate.validator.constraints.UUID String uuid){

        File resource = new File(String.format("upload-dir/%s.pdf", uuid));

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody responseBody = outputStream -> {
            // INFO: PDDocument de PDFBox no es thread-safe
            try(PDDocument pdfDoc = Loader.loadPDF(resource)) {

                PDFTextStripper pdfStripper = new PDFTextStripper();
                int totalPages = pdfDoc.getNumberOfPages();
                int pageRange = 10;

                for (int index = 1; index < totalPages; index+=pageRange) {
                    // Se establece el rango de las páginas.
                    pdfStripper.setStartPage(index);
                    pdfStripper.setEndPage(index+pageRange);

                    pdfStripper.setSortByPosition(true);

                    // Texto del rango de páginas establecido.
                    String textRange = pdfStripper.getText(pdfDoc);

                    // Se envía al stream el contenido.
                    outputStream.write(textRange.trim().getBytes());
                    outputStream.flush();
                }
                pdfDoc.close();
            } catch (IOException e) {

            }
        };

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
