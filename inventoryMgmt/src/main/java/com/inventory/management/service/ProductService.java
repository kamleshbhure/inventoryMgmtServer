package com.inventory.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Product;
import com.inventory.management.repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository repo;

	public List<Product> listAll() {
		return repo.findAll();
	}

	public Product save(Product product) {
		repo.save(product);
		return product;
	}

	public Product get(Long id) {
		return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}
