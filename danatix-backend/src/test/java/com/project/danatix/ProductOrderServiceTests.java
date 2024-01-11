package com.project.danatix;

import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductOrderRepository;
import com.project.danatix.services.ProductOrderService;
import com.project.danatix.services.ProductOrderServiceImpl;
import com.project.danatix.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductOrderServiceTests {

    @Mock
    private ProductService productService;

    @Mock
    private ProductOrderRepository orderRepository;


    private ProductOrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new ProductOrderServiceImpl(orderRepository, productService);
    }

    @Test
    public void createOrderReturnsOrderInstanceWithValidProductIdAndUser() {
        User user = new User();
        user.setName("Johny Test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);

        long productId = 1;
        Product product = new Product(
                "Test ticket", 150, 24, "This is only test, dummy", ProductType.ticket
        );

        ProductOrder order = new ProductOrder(OrderStatus.PENDING, product, user);

        when(productService.getProductById(productId)).thenReturn(product);
        when(orderRepository.save(any(ProductOrder.class))).thenReturn(order);

        assertEquals(order, orderService.createOrder(productId, user));
    }

    @Test
    public void createOrderThrowsCorrectExceptionForNonexistentProductId() {
        User user = new User();
        user.setName("Johny Test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);

        long productId = 1000;

        when(productService.getProductById(productId))
                .thenThrow(new IllegalArgumentException("Product doesn't exist."));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> orderService.createOrder(productId, user)
        );

        assertEquals("Product doesn't exist.", exception.getMessage());
    }

    @Test
    public void createOrderThrowsCorrectExceptionForNullUser() {
        User user = null;
        long productId = 1;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> orderService.createOrder(productId, user)
        );

        assertEquals("User cannot be null.", exception.getMessage());
    }


}
