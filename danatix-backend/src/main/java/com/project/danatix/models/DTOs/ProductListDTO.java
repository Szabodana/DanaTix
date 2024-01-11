package com.project.danatix.models.DTOs;

import com.project.danatix.models.Product;

import java.util.List;

public class ProductListDTO {

    private List<Product> Products;

    public ProductListDTO() {
    }

    public ProductListDTO(List<Product> products) {
        Products = products;
    }

    public List<Product> getProducts() {
        return Products;
    }

    public void setProducts(List<Product> products) {
        Products = products;
    }
}
