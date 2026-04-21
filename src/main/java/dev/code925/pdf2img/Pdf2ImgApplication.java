package dev.code925.pdf2img;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import dev.code925.pdf2img.services.FileStorageManger;
import dev.code925.pdf2img.services.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Pdf2ImgApplication {

	public static void main(String[] args) {
		SpringApplication.run(Pdf2ImgApplication.class, args);
	}

	@Bean
	CommandLineRunner initFileManager(FileStorageManger fileStorageManger) {
		return (args) -> {
			fileStorageManger.deleteAll();
			fileStorageManger.init();
		};
	}
}
