package com.project.danatix.services;

import com.project.danatix.models.Product;
import com.project.danatix.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product getProductById(long id) throws IllegalArgumentException {
        return productRepository.findProductById(id).orElseThrow(() -> new IllegalArgumentException("Product doesn't exist."));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
  }