package com.inventory.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.consumingwb.ProductClient;
import com.products.consumingwebservice.wsdl.Product;
import com.products.consumingwebservice.wsdl.ProductDetailsResponse;

@RestController
public class SoapClientController {


    @Autowired
    private ProductClient quoteClient;

	@GetMapping("/soap/consume/{id}")
    public Product getAccountDetails(@PathVariable(value = "id") Long productId) {
		ProductDetailsResponse response = quoteClient.getProduct(productId);
		return response.getProductDetails();
    }
}
