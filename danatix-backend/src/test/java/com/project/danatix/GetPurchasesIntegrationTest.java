package com.project.danatix;

import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.repositories.PurchaseRepository;
import com.project.danatix.repositories.UserRepository;
import com.project.danatix.services.PurchaseService;
import org.hamcrest.core.IsNull;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class GetPurchasesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

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

    private Product prepareProduct() {

        Product product = new Product(
                "Test ticket", 150, 24, "You can use this ticket for a whole day!",
                ProductType.ticket);
        return productRepository.save(product);
    }

    private Purchase preparePurchase() {

        User testUser = prepareUser();
        mockAuthenticationContext(testUser);

        Product testProduct = prepareProduct();

        Purchase testPurchase = new Purchase();
        testPurchase.setStatus(PurchaseStatus.NOT_ACTIVE);
        testPurchase.setPaidDate(new Date(System.currentTimeMillis()));
        testPurchase.setExpirationDate(null);
        testPurchase.setProduct(testProduct);
        testPurchase.setUser(testUser);

        return purchaseRepository.save(testPurchase);
    }

    private Purchase prepareUsedPurchase() {
        clearPurchases();
        Purchase testPurchase = preparePurchase();

        return purchaseService.useUp(testPurchase.getId());
    }

    private void mockAuthenticationContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    private void clearPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();
        purchaseRepository.deleteAll(purchases);
    }

    private void clearProducts() {
        List<Product> products = productRepository.findAll();
        productRepository.deleteAll(products);
    }

    @Test
    public void GETPurchasesWithValidReturnContent() throws Exception {
        Purchase testPurchase = preparePurchase();
        mockMvc.perform(
                        get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchases[0].id").exists())
                .andExpect(jsonPath("$.purchases[0].status").value("NOT_ACTIVE"))
                .andExpect(jsonPath("$.purchases[0].paidDate").exists())
                .andExpect(jsonPath("$.purchases[0].expirationDate").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.purchases[0].product").exists());
    }

    @Test
    public void GETPurchasesWithNoReturnContent() throws Exception {
        Purchase testPurchase = preparePurchase();
        clearPurchases();

        mockMvc.perform(
                        get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(content().string("There are no purchases at the moment."));
    }

    @Test
    public void GETPurchaseForQRCode() throws Exception {
        Purchase testPurchase = prepareUsedPurchase();
        String purchaseId = Long.toString(testPurchase.getId());
        String url = String.format("/api/purchases/%s", purchaseId);

        mockMvc.perform(
                        get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").exists())
                .andExpect(jsonPath("$.user.email").exists())
                .andExpect(jsonPath("$.product.name").exists())
                .andExpect(jsonPath("$.product.duration").exists())
                .andExpect(jsonPath("$.product.type").value("ticket"))
                .andExpect(jsonPath("$.product.status").value("active"))
                .andExpect(jsonPath("$.product.paidDate").exists())
                .andExpect(jsonPath("$.product.expirationDate").exists());
    }

    @AfterAll
    public void tearDown() {
        purchaseRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
    }
}
