package com.project.danatix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.project.danatix.models.Product;
import com.project.danatix.models.ProductType;
import com.project.danatix.models.Role;
import com.project.danatix.models.User;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AddOrdersBackendIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        prepareProduct();
        User user = prepareUser();
        mockAuthenticationContext(user);
    }

    private User prepareUser() {
        if(userRepository.findUserByEmail("test@email.com").isPresent()) {
            return userRepository.findUserByEmail("test@email.com").get();
        }

        User testUser = new User();
        testUser.setName("Johny Test");
        testUser.setEmail("test@email.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setEmailVerified(true);
        testUser.setRole(Role.ROLE_USER);

        return userRepository.save(testUser);
    }

    private void prepareProduct() {
        if(productRepository.findProductById(1).isEmpty()) {
            Product product = new Product(
                    "Test ticket", 150, 24, "Have fun with testing", ProductType.ticket
            );
            productRepository.save(product);
        }
    }

    private void mockAuthenticationContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    @Test
    public void POSTordersWithValidProductId() throws Exception {

        mockMvc.perform(
                post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"productId\": 1\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.orderDate").exists())
                .andExpect(jsonPath("$.productId", is(1)));
    }

    @Test
    public void POSTordersWithMissingProductId() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Product ID is required.")));
    }

    @Test
    public void POSTordersWithEmptyProductId() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"productId\": \"\"\n" +
                                        "}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Product ID is required.")));
    }

    @Test
    public void POSTordersWithMissingRequestBody() throws Exception {

        mockMvc.perform(post("/api/orders"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Product ID is required.")));
    }

    @Test
    public void POSTordersWithNonexistentProductId() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"productId\": 1000\n" +
                                        "}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Product doesn't exist.")));
    }
}
