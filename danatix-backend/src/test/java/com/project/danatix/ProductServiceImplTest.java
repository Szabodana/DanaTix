package com.project.danatix;

import com.project.danatix.models.Product;
import com.project.danatix.models.ProductType;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.services.ProductService;
import com.project.danatix.services.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {

    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> products = Arrays.asList(
                new Product("Product 1", 10, 30, "Description 1", ProductType.ticket),
                new Product("Product 2", 20, 60, "Description 2", ProductType.pass)
        );

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        verify(productRepository, times(1)).findAll();

        assertEquals(products, result);
    }
}
