package com.project.danatix;

import com.project.danatix.controllers.ProductController;
import com.project.danatix.models.DTOs.ProductListDTO;
import com.project.danatix.models.Product;
import com.project.danatix.models.ProductType;
import com.project.danatix.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductsTests {

    private ProductController productController;
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = Mockito.mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> mockProductList = createMockProductList();

        Mockito.when(productService.getAllProducts()).thenReturn(mockProductList);

        ResponseEntity<ProductListDTO> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProductListDTO productListDTO = response.getBody();
        assertEquals(mockProductList.size(), productListDTO.getProducts().size());
    }

    @Test
    public void testGetAllProductsEmptyList() {

        List<Product> emptyProductList = new ArrayList<>();
        Mockito.when(productService.getAllProducts()).thenReturn(emptyProductList);

        ResponseEntity<ProductListDTO> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProductListDTO productListDTO = response.getBody();
        assertEquals(0, productListDTO.getProducts().size());
    }

    private List<Product> createMockProductList() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Product 1", 10, 30, "Description 1", ProductType.ticket));
        products.add(new Product("Product 2", 20, 60, "Description 2", ProductType.pass));
        return products;
    }
}
