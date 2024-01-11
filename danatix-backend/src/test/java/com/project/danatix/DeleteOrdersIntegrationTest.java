package com.project.danatix;

import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductOrderRepository;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
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

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class DeleteOrdersIntegrationTest {

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

    private User prepareUser(String name, String email) {

        Optional<User> user = userRepository.findUserByEmail(email);

        if (userRepository.findUserByEmail(email).isPresent()) {
            return userRepository.findUserByEmail(email).get();
        } else {
            User testUser = new User();
            testUser.setName(name);
            testUser.setEmail(email);
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

        User testUser = prepareUser();
        mockAuthenticationContext(testUser);
        Product testProduct = prepareProduct();
        ProductOrder productOrder = new ProductOrder();

        productOrder.setStatus(OrderStatus.PENDING);
        productOrder.setProduct(testProduct);
        productOrder.setUser(testUser);

        return productOrderRepository.save(productOrder);
    }

    private void mockAuthenticationContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    private void clearProducts() {
        List<Product> products = productRepository.findAll();
        productRepository.deleteAll(products);
    }

    @Test
    public void DELETEOrderById() throws Exception {
        ProductOrder productOrder = prepareProductOrder();
        String orderIdStr = Long.toString(productOrder.getId());
        String url = String.format("/api/orders/%s", orderIdStr);

        mockMvc.perform(
                        delete(url))
                .andExpect(status().isOk())
                .andExpect(content().string("Order successfully deleted"));
    }

    @Test
    public void DELETEOrderByIdOtherUser() throws Exception {
        ProductOrder productOrder = prepareProductOrder();
        String orderIdStr = Long.toString(productOrder.getId());
        String url = String.format("/api/orders/%s", orderIdStr);

        User testUser2 = prepareUser("TestUser2", "testuser2@email.com");
        productOrder.setUser(testUser2);
        productOrderRepository.save(productOrder);

        mockMvc.perform(
                        delete(url))
                .andExpect(status().is(400))
                .andExpect(content().string("Order does not belong to the user, cannot be deleted"));
    }

    @Test
    public void DELETEOrderByIdWrongStatus() throws Exception {
        ProductOrder productOrder = prepareProductOrder();
        String orderIdStr = Long.toString(productOrder.getId());
        String url = String.format("/api/orders/%s", orderIdStr);

        productOrder.setStatus(OrderStatus.PAID);
        productOrderRepository.save(productOrder);

        mockMvc.perform(
                        delete(url))
                .andExpect(status().is(400))
                .andExpect(content().string("Order is not in PENDING status, cannot be deleted"));
    }

    @Test
    public void DELETEPendingOrders() throws Exception {
        ProductOrder productOrder = prepareProductOrder();

        mockMvc.perform(
                        delete("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pending orders have been successfully deleted"));
    }

    @Test
    public void DELETEPendingOrdersWithNoPendingOrders() throws Exception {
        User testUser = prepareUser();
        mockAuthenticationContext(testUser);

        mockMvc.perform(
                        delete("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().string("There are no pending orders to be deleted at the moment"));
    }

    @AfterAll
    public void tearDown() {
        productOrderRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
    }
}