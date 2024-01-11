package com.project.danatix.controllers;

import com.project.danatix.models.DTOs.ProductListDTO;
import com.project.danatix.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductListDTO> getAllProducts(){
        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(productService.getAllProducts());
        return ResponseEntity.ok(productListDTO);
    }
}