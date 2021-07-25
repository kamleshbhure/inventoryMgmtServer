package com.inventory.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.management.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
