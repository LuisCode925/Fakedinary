package com.luiscode925.apirestpdf2img.entities;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaInfo {
    
    private String author;
    
    private LocalDateTime creationDate;
    
    private String creator;
    
    private String keywords;
      
    private Set<String> metadataKeys;
      
    private LocalDateTime modificationDate;
    
    private String producer; // Contiene el software creador y la version
          
    private String subject;

    private String title;
            
    private String trapped;

    private Float pdfVersion;

    private Integer totalPages;

    private Set<PDFSize> dimensions;

    // private Set<String> fonts;

}

// https://javadoc.io/doc/org.apache.pdfbox/pdfbox/latest/index.html



/*
    Cifrado (Encriptación): Los PDFs pueden estar cifrados para proteger su contenido. El cifrado impide que personas no autorizadas accedan al contenido del PDF sin la contraseña correcta. 
    Los algoritmos de cifrado comúnmente utilizados en PDFs incluyen RC4 y AES (Advanced Encryption Standard).
    Contraseña de apertura: Un PDF puede requerir una contraseña para abrirlo. Esta contraseña se utiliza para descifrar el contenido del PDF.
    Contraseña de permisos: Algunos PDFs pueden tener una contraseña de permisos que restringe ciertas acciones, como imprimir, copiar o editar el contenido del PDF.
        Restricciones de permisos: Los PDFs pueden tener restricciones de permisos que limitan las acciones que se pueden realizar con el documento, como:
            Imprimir: restringe la capacidad de imprimir el PDF.
            Copiar: restringe la capacidad de copiar texto o imágenes del PDF.
            Editar: restringe la capacidad de editar el contenido del PDF.
            Anotaciones: restringe la capacidad de agregar anotaciones o comentarios al PDF.
    Firma digital: Un PDF puede contener una firma digital que verifica la autenticidad y la integridad del documento. La firma digital se crea utilizando un certificado digital y un algoritmo de firma.

    // Sin soporte para PDFBox 3
    Certificado de seguridad: Un PDF puede estar asociado con un certificado de seguridad que verifica la identidad del autor o emisor del documento.
    Protección contra malware: Algunos PDFs pueden contener características de seguridad que protegen contra malware, como la capacidad de detectar y bloquear contenido malicioso.
 */