# Fake-Cloudinary
Microservicio desarrollado en Spring Boot para remplazar el uso de Cloudinary, 
tiene las funcionalidades de subir archivos pdf, extraer el texto de los mismos y 
generar imágenes a partir de sus páginas.

## UploadController

Un solo archivo:
```bash
curl -X POST -F "file=@Linux Device Drivers.3rd.Edition.pdf" http://localhost:8080/pdf/upload
```

Multiples archivos:
```bash
curl -X POST -F "files=@PATH/FILE.pdf" -F "files=@PATH/FILE.pdf" http://localhost:8080/pdf/upload-multiple
```

El proceso de subir un archivo PDF mostrará información relativa al mismo, asi como enlaces HATEOAS (Hypermedia as the Engine of Application State) para 
realizar las operaciones extraer el texto y generar imágenes a partir de las páginas: 
```json
{
    "uuid": "ad987ff3-8853-44df-9fc1-49f2c2dcb05a",
    "originalName": "Linux Device Drivers.3rd.Edition.pdf",
    "fileSize": 12825921,
    "contentType": "application/pdf",
    "totalPages": 630,
    "deleted": false,
    "uploadedAt": "2026-04-29T15:05:37.296578995",
    "metadata": {
        "title": null,
        "author": null,
        "subject": null,
        "keywords": null,
        "creator": "1.00 - www.pdftk.com",
        "producer": "itext-paulo (lowagie.com)[JDK1.1] - build 132",
        "creationDate": "2005-03-25",
        "modificationDate": "2005-03-25",
        "metadataKeys": [
            "CreationDate",
            "Producer",
            "Creator",
            "ModDate"
        ],
        "trapped": null,
        "dimensions": [
            {
                "width": 612.0,
                "height": 792.0,
                "orientation": "VERTICAL"
            }
        ],
        "fonts": [
            "CDBADJ+Times-Roman",
            "PJBKCO+Symbol",
            "MAGFHI+TheSansMonoCondensed-SemiLightItalic",
            "CHLLNI+TheSansMonoCondensed-SemiLightItalic",
            "PLKBJH+Birka-Italic",
            "PEKDLM+TheSansMonoCondensed-Bold",
            "PCLGPB+Myriad-CnBoldItalic",
            "PJBFNN+Myriad-Condensed",
            "PJBFIM+Myriad-CnItalic",
            "ANIBKF+TheSansMonoCondensed-Bold",
            "PJAHKM+Times-Roman",
            "AEGKJF+Birka",
            "PEJMJI+TheSansMonoCondensed-SemiLight",
            "CHLLOJ+TheSansMonoCondensed-Bold",
            "OGPNBA+Birka-Italic",
            "PJAGLM+Birka",
            "AEGKIN+Myriad-CnSemibold",
            "AEHIKJ+TheSansMonoCondensed-Bold",
            "IEACOJ+TheSansMonoCondensed-Bold",
            "AEGNJK+TheSansMonoCondensed-SemiLight",
            "AEGPOE+Myriad-CnBold",
            "IDPHIF+TheSansMonoCondensed-SemiLight",
            "PLKKAL+TheSansMonoCondensed-SemiLightItalic",
            "AJNAOC+Times-Roman",
            "PLKDCE+Times-Roman",
            "CDBAAN+Myriad-CnSemibold",
            "AEGKEE+Birka-Italic",
            "IHOHNC+Times-Roman",
            "PJBFMO+Helvetica-Condensed-Black",
            "JLBOAD+Birka-Italic",
            "DJMGCA+Birka",
            "ODJNJD+Times-Roman",
            "MAFHEC+Times-Roman",
            "PHHPFJ+Times-Roman",
            "ANHHOH+Times-Roman",
            "OHAAMO+TheSansMonoCondensed-Bold",
            "IMCGLO+Birka-Italic",
            "PHHOEH+Myriad-CnSemibold",
            "IDPGOP+Birka",
            "DJMFND+Birka-Italic",
            "MAGALE+TheSansMonoCondensed-SemiLight",
            "AEHABF+Myriad-CnItalic",
            "IMDOMO+Myriad-Condensed",
            "DIPFON+Birka-Italic",
            "ANHGLM+Birka",
            "IMEIAP+Myriad-CnBold",
            "IMCHAN+Birka",
            "AJNHII+Myriad-CnBold",
            "MAFKCO+Myriad-Condensed",
            "PJAJOC+TheSansMonoCondensed-SemiLight",
            "DJMOFC+TheSansMonoCondensed-SemiLight",
            "HPPDFA+Myriad-CnItalic",
            "HPPIOO+TheSansMonoCondensed-SemiLight",
            "CDBANE+Birka",
            "CFPMFM+Myriad-CnSemibold",
            "ONHBEL+TheSansMonoCondensed-Bold",
            "AJMPKK+Birka",
            "ANHGGM+Birka-Italic",
            "AEHIPJ+TheSansMonoCondensed-SemiLightItalic",
            "CHLLKN+TheSansMonoCondensed-SemiLight",
            "PCIFPL+Times-Roman",
            "PLKBNO+Myriad-CnSemibold",
            "IDPGOI+Myriad-CnSemibold",
            "IMCIEC+Times-Roman",
            "PEJLJK+Birka-Italic",
            "AJMPFJ+Birka-Italic",
            "CDBHMA+Myriad-Condensed",
            "PHHPDA+TheSansMonoCondensed-SemiLight",
            "DIPGDG+Myriad-CnSemibold",
            "PLKBOG+Birka",
            "IEACKG+Myriad-CnItalic",
            "DJMHEM+Times-Roman",
            "ODJPNP+TheSansMonoCondensed-SemiLightItalic",
            "ONGLNC+Birka-Italic",
            "PCLGJH+TheSansMonoCondensed-Bold",
            "CFPMJA+Birka",
            "ODKLHE+Myriad-Condensed",
            "PCLGKD+TheSansMonoCondensed-Plain",
            "PJAGHG+Birka-Italic",
            "PJAGLF+Myriad-CnSemibold",
            "PJBFPE+Myriad-CnBold",
            "JLBOEJ+Birka",
            "DJOIMA+TheSansMonoCondensed-Bold",
            "OHBCJD+Myriad-CnBold",
            "CDBHGB+Birka-SemiBoldItalic",
            "CFPMLP+Birka-Italic",
            "PJBPFG+TheSansMonoCondensed-Bold",
            "ODKLLJ+Helvetica-Condensed-Bold",
            "OPDMOD+TheSansMonoCondensed-SemiLight",
            "PEJLOF+Birka",
            "PEJMMP+Times-Roman",
            "CHLFLM+Times-Roman",
            "DJMJLO+Myriad-Condensed",
            "CHLEAJ+Myriad-CnSemibold",
            "ONGMAM+Myriad-CnSemibold",
            "OGPOHN+Times-Roman",
            "AJNDEG+TheSansMonoCondensed-SemiLight",
            "AEGLKN+Times-Roman",
            "IHOHEH+Myriad-CnSemibold",
            "CGAADO+Times-Roman",
            "HPOKJA+Birka-Italic",
            "PHHNPN+Birka-Italic",
            "ANHIOF+TheSansMonoCondensed-SemiLight",
            "ODJPPB+TheSansMonoCondensed-Bold",
            "JLBOEC+Myriad-CnSemibold",
            "HPOKNI+Myriad-CnSemibold",
            "PLKEAE+TheSansMonoCondensed-SemiLight",
            "JLBPAG+TheSansMonoCondensed-SemiLight",
            "HPOKNP+Birka",
            "OPDMBN+Times-Roman",
            "AEGPPJ+Myriad-Condensed",
            "IMDPBL+Myriad-CnItalic",
            "DJMGBJ+Myriad-CnSemibold",
            "HPPDHO+Myriad-CnBold",
            "IMEICE+TheSansMonoCondensed-Bold",
            "ODKCCA+Myriad-CnBoldItalic",
            "CHLDMB+Birka-Italic",
            "ONGNBE+Times-Roman",
            "OPEPOM+Myriad-Condensed",
            "PCIEKP+Birka",
            "ONGMON+TheSansMonoCondensed-SemiLight",
            "ODJMDM+Myriad-CnSemibold",
            "ODJLPC+Birka-Italic",
            "AJNLKN+TheSansMonoCondensed-Bold",
            "ODKLFL+Myriad-CnItalic",
            "OPDLCG+Myriad-CnSemibold",
            "IHOHIK+Birka",
            "OGPPMA+TheSansMonoCondensed-SemiLight",
            "IDPGKA+Birka-Italic",
            "OPDKOG+Birka-Italic",
            "PHHOEO+Birka",
            "JLDCAK+Myriad-Condensed",
            "IMCHAG+Myriad-CnSemibold",
            "JLBPEF+Times-Roman",
            "PEJLNN+Myriad-CnSemibold",
            "OGPNGB+Myriad-CnSemibold",
            "DIPGDO+Birka",
            "DJNGOI+Myriad-CnItalic",
            "MAFFMC+Birka",
            "AJMPKC+Myriad-CnSemibold",
            "ANHGLE+Myriad-CnSemibold",
            "IMEIDE+TheSansMonoCondensed-Plain",
            "MAFFHC+Birka-Italic",
            "IEACJB+Myriad-CnBold",
            "MAFFLK+Myriad-CnSemibold",
            "IMEIEB+ConstantWillison-Bold",
            "ODJOLH+TheSansMonoCondensed-SemiLight",
            "CFPNGJ+Myriad-Condensed",
            "IMDLDN+TheSansMonoCondensed-SemiLightItalic",
            "IDPHMI+Times-Roman",
            "PCIEGI+Birka-Italic",
            "PCLGMB+Myriad-CnItalic",
            "IHOHHH+Birka-SC",
            "PLKHJL+TheSansMonoCondensed-Bold",
            "HPOLPL+Times-Roman",
            "OGPNGJ+Birka",
            "OPDLCN+Birka",
            "AJNHND+Myriad-CnItalic",
            "OHADOH+Myriad-Condensed",
            "PCIEKI+Myriad-CnSemibold",
            "IMCKDM+TheSansMonoCondensed-SemiLight",
            "CDBAPC+Birka-Italic",
            "CHLFAM+Birka",
            "OHBCKC+TheSansMonoCondensed-Plain",
            "PCIFOC+TheSansMonoCondensed-SemiLight",
            "ODKCFN+Myriad-CnBold",
            "DIPGKO+Times-Roman",
            "ODJMEE+Birka",
            "ONGMBE+Birka",
            "ANHJMP+Myriad-Condensed"
        ]
    },
    "_links": {
        "download": {
            "href": "http://localhost:8080/downloads/ad987ff3-8853-44df-9fc1-49f2c2dcb05a"
        },
        "thumbnail": {
            "href": "http://localhost:8080/images/render/ad987ff3-8853-44df-9fc1-49f2c2dcb05a/1"
        },
        "document-images": {
            "href": "http://localhost:8080/images/ad987ff3-8853-44df-9fc1-49f2c2dcb05a"
        },
        "extractText": {
            "href": "http://localhost:8080/pdf/ad987ff3-8853-44df-9fc1-49f2c2dcb05a/text"
        }
    }
}
```
## DownloadController
También existe un mecanismo que le permitira al usuario recuperar el archivo original que ha subido,
el endpoint en cuestion soporta multiples conexiones acelerando la descarga cuando se utilizan gestores como 
lo son Internet Download Manager o Free Download Manager.

```
curl -X GET http://localhost:8080/downloads/ad987ff3-8853-44df-9fc1-49f2c2dcb05a
```
## ExtractTextController
El endpoint `/pdf/{uuid}/text` permite a los usuarios solicitar la extracción de texto de un documento PDF identificado por su `uuid`. 
La funcionalidad realiza una extracción parcial del contenido utilizando la estructura del PDF (no OCR) utilizando la libreria PDFBox 
procesando el texto para todas las páginas del documento y devolviendo el resultado a través de un stream.

Para probar este endpoint, debes reemplazar `{UUID_DEL_DOCUMENTO}` con el ID real de un PDF que hayas cargado en el sistema.

```bash
curl -X GET http://localhost:8080/pdf/a1b2c3d4-e5f6-7890-1234-567890abcdef/text
```

## ImageController
Este controlador gestiona las operaciones relacionadas con la gestión de la imagenes del documento PDF (embebidas), 
extracción de texto mediante OCR (utilizando Tesseract) y renderizado de imágenes asociadas a documentos PDF.

### Descripción de los Endpoints

Este controlador expone tres funcionalidades principales para interactuar con los recursos de imagen
y una para la extraction de texto mediante OCR:

#### 1. Obtener Todas las Imágenes de un PDF (`getAllImagesFromPdf`)
*   **Ruta:** `/images/{documentId}`
*   **Función:** Recupera la lista de todas las imágenes incrustadas a un documento PDF, contienen las imagenes que PDFBox puede extraer. Como respuesta se devuelve el conteo total de imágenes y enlaces para cada una de ellas esto utilizando HATEOAS.
*   **Parámetros:** `documentId` (UUID del PDF).

#### 2. Extracción de Texto de Imagen (OCR) (`extractTextFromImage`)
*   **Ruta:** `/images/ocr`
*   **Función:** Realiza el Reconocimiento Óptico de Caracteres (OCR) en una imagen subida por el usuario.
*   **Parámetros:**
    *   `file`: Archivo de imagen a procesar solo en formato PNG (multipart/form-data).
    *   `lang`: Idioma del texto a extraer (por defecto: `spa`), si se tiene más de un lenguaje se separa por una coma.
*   **Respuesta:** `OCRResponse` con el texto extraído.

#### 3. Renderizar una Página del PDF como Imagen (`renderImageFromPage`)
*   **Ruta:** `/images/render/{uuid}/{page}`
*   **Función:** Genera y devuelve la imagen correspondiente a una página específica de un documento PDF.
*   **Parámetros:**
    *   `uuid`: ID del documento PDF.
    *   `page`: Número de la página a renderizar (debe ser $\ge 1$).
*   **Respuesta:** La imagen renderizada en formato JPEG.

#### 4. Mostrar Imagen Incrustada (`showImage`)
*   **Ruta:** `/images/embedded/{imageId}`
*   **Función:** Recupera y sirve el archivo de imagen (PNG) asociado por su ID.
*   **Parámetros:** `imageId` (UUID de la imagen).
*   **Respuesta:** El recurso de imagen en formato JPEG.

## Comandos docker

ejecutar maven clean && install

docker build -t fake-cloudinary:1.0 .

docker run -p 80:8080 fake-cloudinary:1.0 -d

docker exec -ti 1c88589eeadd /bin/ash

docker rmi _hash_ V para remover imágenes

docker rm -f $(docker ps -a -q --filter "status=exited")