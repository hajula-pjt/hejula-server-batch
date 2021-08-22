package com.hejula.server.batch.hejulaserverbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class HejulaServerBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(HejulaServerBatchApplication.class, args);
	}

}
