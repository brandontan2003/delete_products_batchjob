package com.example.delete_products_batchjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.delete_products_batchjob")
public class DeleteProductsBatchjobApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DeleteProductsBatchjobApplication.class, args);
		SpringApplication.exit(context);
	}

}
