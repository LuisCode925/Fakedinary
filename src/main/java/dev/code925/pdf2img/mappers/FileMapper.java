package dev.code925.pdf2img.mappers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.code925.pdf2img.controller.DownloadController;
import dev.code925.pdf2img.controller.ExtractTextController;
import org.mapstruct.*;

import dev.code925.pdf2img.controller.ImageController;
import dev.code925.pdf2img.entities.File;
import dev.code925.pdf2img.entities.DTO.FileResponse;

import java.util.List;
import java.util.Set;

import org.springframework.hateoas.Link;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper {

    @Mapping(target = "links", ignore = true)
    FileResponse toFileResponse(File pdf);

     @AfterMapping
     default void addLinksHATEOAS(File pdf, @MappingTarget FileResponse response) {
         try {
             Link self =linkTo(methodOn(DownloadController.class).serveFile(pdf.getUuid().toString())).withRel("download");
             response.add(self);

             Link thumbnailLink = linkTo(methodOn(ImageController.class).renderImageFromPage(pdf.getUuid().toString(), 1)).withRel("thumbnail");
             response.add(thumbnailLink);

             Link documentImages = linkTo(methodOn(ImageController.class).getAllImagesFromPdf(pdf.getUuid().toString())).withRel("document-images");
             response.add(documentImages);

             Link extractText = linkTo(methodOn(ExtractTextController.class).extractText(pdf.getUuid().toString())).withRel("extractText");
             response.add(extractText);

         } catch (Exception e) {
             e.printStackTrace();
         }
      }


    List<FileResponse> toListResponse(List<File> listEntities);

    Set<FileResponse> toSetResponse(Set<File> listEntities);
}
