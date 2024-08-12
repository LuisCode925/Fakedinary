/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package com.luiscode925.apirestpdf2img.repository;

import com.luiscode925.apirestpdf2img.entities.FilePDF;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Luis
 */
public interface FilePDFRepository extends JpaRepository<FilePDF, UUID> {

}
