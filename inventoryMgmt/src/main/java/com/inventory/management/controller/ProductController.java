package com.inventory.management.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.management.model.Product;
import com.inventory.management.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private ProductService service;

	@GetMapping("/products")
	public List<Product> getAllProducts() {
		return service.listAll();
	}

	@PostMapping("/products")
	public Product createProduct(@Valid @RequestBody Product product) {
		return service.save(product);
	}

	@GetMapping("/products/{id}")
	public Product getProductById(@PathVariable(value = "id") Long productId) {
		return service.get(productId);
	}

	@PutMapping("/products/{id}")
	public Product updateProduct(@PathVariable(value = "id") Long productId, @Valid @RequestBody Product productDetails) {

		Product product = service.get(productId);

		product.setName(productDetails.getName());
		product.setBrand(productDetails.getBrand());
		product.setMadein(productDetails.getMadein());
		product.setPrice(productDetails.getPrice());

		Product updatedProduct = service.save(product);
		return updatedProduct;
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") Long productId) {

		service.delete(productId);
		return ResponseEntity.ok(productId);
	}
}