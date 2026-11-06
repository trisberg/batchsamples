package com.example.batchapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.exit(SpringApplication.run(BatchApplication.class, args));
	}
}
