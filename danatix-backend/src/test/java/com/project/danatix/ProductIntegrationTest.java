package com.project.danatix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockLoggedUser();
        this.clearProducts();
        objectMapper = new ObjectMapper();
    }

    public void mockLoggedUser(){
        User user1 = new User();
        user1.setName("Gandalf");
        user1.setEmail(user1.getEmail());
        user1.setPassword("Gollum");
        user1.setRole(Role.ROLE_USER);
        user1.setEmailVerified(true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userRepository.save(user1),null,user1.getAuthorities()));
    }

    private void clearProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product:products) {
            productRepository.delete(product);
        }
    }

    @Test
    void contextLoad(){
    }

    @Test
    public void testGetAllProducts() throws Exception {
        productRepository.save(new Product("Product 1", 50, 20, "Description 1", ProductType.ticket));
        productRepository.save(new Product("Product 2", 75, 15, "Description 2", ProductType.pass));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products.length()").value(2))
                .andExpect(jsonPath("$.products[0].name").value("Product 1"))
                .andExpect(jsonPath("$.products[1].name").value("Product 2"));
    }

    @AfterAll
    public void tearDown() {
        userRepository.deleteAll();
        productRepository.deleteAll();
    }
}