package dev.code925.pdf2img.controller;

import dev.code925.pdf2img.exception.ExtractTextException;
import dev.code925.pdf2img.services.FileStorageManger;
import jakarta.validation.constraints.Size;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@Validated
@RestController
@RequestMapping("/pdf")
public class ExtractTextController {

    @Autowired
    private FileStorageManger fileManger;

    @GetMapping("/{uuid}/text")
    public ResponseEntity<StreamingResponseBody> extractText(@PathVariable @Size(min = 36, max = 36) @org.hibernate.validator.constraints.UUID String uuid){

        Resource documentPDF = fileManger.loadAsResource(String.format("%s.pdf", uuid));

        if (!documentPDF.exists()) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody responseBody = outputStream -> {
            try(PDDocument pdfDoc = Loader.loadPDF(documentPDF.getFile())) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    int totalPages = pdfDoc.getNumberOfPages();
                    int pageRange = 10;

                    for (int index = 1; index < totalPages; index += pageRange) {

                        int endIndex = index + pageRange;
                        if(endIndex > totalPages) endIndex = totalPages;

                        // Se establece el rango de las páginas.
                        pdfStripper.setStartPage(index);
                        pdfStripper.setEndPage(endIndex);

                        pdfStripper.setSortByPosition(true);

                        // Texto del rango de páginas establecido.
                        String textRange = pdfStripper.getText(pdfDoc);

                        // Se envía al stream el contenido.
                        outputStream.write(textRange.trim().getBytes());
                        outputStream.flush();
                    }
                    pdfDoc.close();
                } catch (IOException e) {
                    throw new ExtractTextException("Hubo un error al extraer el texto del PDF.");
                }
            };
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
