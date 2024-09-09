# Fake-Cloudinary
Microservicio desarrollado en Spring Boot para remplazar el uso de Cloudinary, 
tiene las funcionalidades de subir archivos pdf, extraer el texto de los mismos y 
generar imágenes a partir de sus paginas.

Un solo archivo:
```
curl -X POST -F "file=@PATH/FILE.pdf" http://localhost:8080/pdf/upload
```

Multiples archivos:
```
curl -X POST -F "files=@PATH/FILE.pdf" -F "files=@PATH/FILE.pdf" http://localhost:8080/pdf/upload
```

El proceso de subir un archivo PDF mostrara información relativa al mismo, asi como enlaces para 
realizar las operaciones extraer el texto y generar imágenes de la paginas: 
```
{
   "uuid":"40bb3fe6-6857-4076-91b0-c9332c91b95b",
   "origName":"Difference-Between-ML-DL-AI.pdf",
   "fileSize":229386,
   "contentType":"application/pdf",
   "numPages":3,
   "deleted":false,
   "uploadAt":"2024-09-09T09:39:49.149091143",
   "_links":{
      "self":[
         {
            "href":"http://localhost:8080/pdf/40bb3fe6-6857-4076-91b0-c9332c91b95b"
         },
         {
            "href":"http://localhost:8080/pdf/40bb3fe6-6857-4076-91b0-c9332c91b95b/extract-text"
         }
      ]
   }
}
```

## Comandos docker

ejecutar maven clean && install

docker build -t fake-cloudinary:1.0 .

docker run -p 80:8080 fake-cloudinary:1.0 -d

docker exec -ti 1c88589eeadd /bin/ash

docker rmi _hash_ V para remover imágenes
