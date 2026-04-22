package dev.code925.pdf2img.mappers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.code925.pdf2img.controller.DownloadController;
import dev.code925.pdf2img.controller.FileTextController;
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
         Link self =linkTo(methodOn(DownloadController.class).serveFile(pdf.getUuid().toString())).withRel("download");
         response.add(self);

         try {
             Link thumbnailLink = linkTo(methodOn(ImageController.class).showImage(pdf.getUuid().toString(), 1)).withRel("thumbnail");
             response.add(thumbnailLink);
         } catch (Exception e) {
             e.printStackTrace();
         }

          Link extractText = linkTo(methodOn(FileTextController.class).extractText(pdf.getUuid().toString())).withRel("extractText");
          response.add(extractText);
      }


    List<FileResponse> toListResponse(List<File> listEntities);

    Set<FileResponse> toSetResponse(Set<File> listEntities);
}
