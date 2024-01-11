package com.project.danatix.services;

import com.project.danatix.models.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    Product getProductById(long id) throws IllegalArgumentException;

    List<Product> getAllProducts();
}