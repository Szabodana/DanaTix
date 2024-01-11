package com.project.danatix;

import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductOrderRepository;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.repositories.UserRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class PatchOrdersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        Product product = prepareProduct();
        User user = prepareUser();
        mockAuthenticationContext(user);
        ProductOrder productOrder = prepareProductOrder();
    }

    private User prepareUser() {

        Optional<User> user = userRepository.findUserByEmail("test@email.com");

        if (userRepository.findUserByEmail("test@email.com").isPresent()) {
            return userRepository.findUserByEmail("test@email.com").get();
        } else {
            User testUser = new User();
            testUser.setName("Johny Test");
            testUser.setEmail("test@email.com");
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setRole(Role.ROLE_USER);
            testUser.setEmailVerified(true);

            return userRepository.save(testUser);
        }
    }

    private Product prepareProduct() {

        Product product = new Product(
                "Test ticket", 150, 24, "You can use this ticket for a whole day!",
                ProductType.ticket);
        return productRepository.save(product);
    }

    private ProductOrder prepareProductOrder() {

        ProductOrder productOrder = new ProductOrder();
        productOrder.setStatus(OrderStatus.PENDING);

        Optional<Product> optionalProduct = productRepository.findProductByName("Test ticket");
        productOrder.setProduct(optionalProduct.orElseThrow(() -> new IllegalStateException("Cannot find test product!")));

        Optional<User> optionalUser = userRepository.findUserByEmail("test@email.com");
        productOrder.setUser(optionalUser.orElseThrow(() -> new IllegalStateException("Cannot find user!")));

        return productOrderRepository.save(productOrder);
    }

    private void mockAuthenticationContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    @Test
    public void PatchPendingOrdersWithValidReturnContent() throws Exception {
        prepareProductOrder();
        mockMvc.perform(
                        patch("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchases[0].id").exists())
                .andExpect(jsonPath("$.purchases[0].status", is("not active")))
                .andExpect(jsonPath("$.purchases[0].paidDate").exists())
                .andExpect(jsonPath("$.purchases[0].expirationDate").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.purchases[0].productName", is("Test ticket")));
    }

    @AfterAll
    public void tearDown() {
        productOrderRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
    }
}
