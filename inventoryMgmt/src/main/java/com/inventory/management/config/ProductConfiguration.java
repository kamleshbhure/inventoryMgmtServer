package com.inventory.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.inventory.consumingwb.ProductClient;

@Configuration
public class ProductConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// this package must match the package in the <generatePackage> specified in
		// pom.xml
		marshaller.setContextPath("com.products.consumingwebservice.wsdl");
		return marshaller;
	}

	@Bean
	public ProductClient countryClient(Jaxb2Marshaller marshaller) {
		ProductClient client = new ProductClient();
		client.setDefaultUri("http://localhost:8090/soap-api");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

}