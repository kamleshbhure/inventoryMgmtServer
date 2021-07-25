package com.inventory.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.management.library.AppProperties;


@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class InventoryappApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(InventoryappApplication.class, args);
	}
}
