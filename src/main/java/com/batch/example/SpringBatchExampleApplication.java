package com.batch.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchExampleApplication {

	public static void main(String[] args)  {
		System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchExampleApplication.class, args)));
	}

}
