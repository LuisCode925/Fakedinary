package com.luiscode925.apirestpdf2img.services;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    // Cambiar a variable de entorno del sistema
    private String location = "/pdf-utils/upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
