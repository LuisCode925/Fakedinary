package com.luiscode925.apirestpdf2img;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.luiscode925.apirestpdf2img.services.FileStorageManger;
import com.luiscode925.apirestpdf2img.services.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ApiRestPdf2imgApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRestPdf2imgApplication.class, args);
	}

	@Bean
	CommandLineRunner initFileManager(FileStorageManger fileStorageManger) {
		return (args) -> {
			fileStorageManger.deleteAll();
			fileStorageManger.init();
		};
	}
}
