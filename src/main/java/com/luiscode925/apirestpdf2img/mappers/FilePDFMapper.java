package com.luiscode925.apirestpdf2img.mappers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.luiscode925.apirestpdf2img.controller.FilePDFController;
import com.luiscode925.apirestpdf2img.entities.FilePDF;
import com.luiscode925.apirestpdf2img.entities.FilePDFResponse;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapping;
import org.springframework.hateoas.Link;

@Mapper(componentModel = "spring")
public interface FilePDFMapper {

    @Mapping(target="uuid", source="uuid")   
    @Mapping(target="originalName", source="originalName")    
    @Mapping(target="fileSize", source="fileSize")
    @Mapping(target="contentType", source="contentType")    
    @Mapping(target="totalPages", source="totalPages")    
    @Mapping(target="deleted", source="deleted")   
    @Mapping(target="uploadedAt", source="uploadedAt")  
    @Mapping(target="metadata", source="metadata") 
    @Mapping(target="add", ignore = true) 
    FilePDFResponse toFilePDFResponse(FilePDF pdf);

    @AfterMapping
    default void addLinksHATEOAS(FilePDF pdf, @MappingTarget FilePDFResponse response){
        Link self = linkTo(methodOn(FilePDFController.class).serveFile(pdf.getUuid().toString())).withSelfRel();
        Link extractText = linkTo(methodOn(FilePDFController.class).extractText(pdf.getUuid().toString())).withRel("extractText");
        response.add(self);
        response.add(extractText);
    }
    
    List<FilePDFResponse> toListResponse(List<FilePDF> listEntities);
     
    Set<FilePDFResponse> toSetResponse(Set<FilePDF> listEntities);
}
