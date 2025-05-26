package com.example.skala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.skala.model")
public class SkalaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkalaApplication.class, args);
	}

}
