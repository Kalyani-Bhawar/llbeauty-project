package com.llbeauty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LlbeautyApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlbeautyApplication.class, args);
	}

}
