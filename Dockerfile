FROM openjdk:17-jdk-alpine

# Crear las 2 carpetas de upload-dir e imagenes
WORKDIR /pdf-utils
CMD mkdir /pdf-utils/upload-dir
ARG JAR_FILE=target/*.jar
COPY ./target/api-rest-pdf2img-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/pdf-utils/app.jar"]