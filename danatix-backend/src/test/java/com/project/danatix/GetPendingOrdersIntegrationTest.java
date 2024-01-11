package com.project.danatix;

import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductOrderRepository;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.repositories.UserRepository;
import org.junit.jupiter.api.*;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class GetPendingOrdersIntegrationTest {

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
        prepareProduct();
        User user = prepareUser();
        prepareProductOrder();
        mockAuthenticationContext(user);
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

    private void prepareProduct() {

        Product product = new Product(
                "Test ticket", 150, 24, "You can use this ticket for a whole day!",
                ProductType.ticket);
        productRepository.save(product);
    }

    private void prepareProductOrder() {

        ProductOrder productOrder = new ProductOrder();
        productOrder.setStatus(OrderStatus.PENDING);

        Optional<Product> optionalProduct = productRepository.findProductById(1);
        productOrder.setProduct(optionalProduct.orElseThrow(() -> new IllegalStateException("Cannot find test product!")));

        Optional<User> optionalUser = userRepository.findUserByEmail("test@email.com");
        productOrder.setUser(optionalUser.orElseThrow(() -> new IllegalStateException("Cannot find user!")));

        productOrderRepository.save(productOrder);
    }

    private void mockAuthenticationContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    private void clearProductOrders() {
        List<ProductOrder> productOrders = productOrderRepository.findAll();
        productOrderRepository.deleteAll(productOrders);
    }

    @Test
    public void GETPendingOrdersWithValidReturnContent() throws Exception {
        prepareProductOrder();
        mockMvc.perform(
                        get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders[0].id").exists())
                .andExpect(jsonPath("$.orders[0].status").value("pending"))
                .andExpect(jsonPath("$.orders[0].orderDate").exists())
                .andExpect(jsonPath("$.orders[0].name").exists())
                .andExpect(jsonPath("$.orders[0].price").exists());
    }

    @Test
    public void GETPendingOrdersWithNoReturnContent() throws Exception {

        clearProductOrders();

        mockMvc.perform(
                        get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().string("There are no pending orders"));
    }

    @AfterAll
    public void tearDown() {
        productOrderRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
    }
}