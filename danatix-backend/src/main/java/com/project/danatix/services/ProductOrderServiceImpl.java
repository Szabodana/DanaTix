package com.project.danatix.services;

import com.project.danatix.models.DTOs.BuyOrderDTO;
import com.project.danatix.models.DTOs.PendingOrderDTO;
import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductOrderRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductOrderRepository orderRepository;

    private final ProductService productService;

    public ProductOrderServiceImpl(ProductOrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @Override
    public ProductOrder createOrder(long productId, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        Product product = productService.getProductById(productId);
        ProductOrder order = new ProductOrder(OrderStatus.PENDING, product, user);

        return orderRepository.save(order);
    }

    @Override
    public List<ProductOrder> findOrdersByUser(User user) {
        return orderRepository.findProductOrdersByUser(user);
    }

    @Override
    public List<ProductOrder> findPendingOrdersByUser(User user) {
        return orderRepository.findProductOrdersByUser(user).stream()
                .filter(order -> order.getStatus().getDisplayValue().equals("pending"))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductOrder> convertPendingOrders(List<ProductOrder> pendingOrders) {
        for (ProductOrder productOrder : pendingOrders) {
            productOrder.setStatus(OrderStatus.PAID);
            productOrder.setPaidDate(new Date(System.currentTimeMillis()));
            orderRepository.save(productOrder);
        }
        return pendingOrders;
    }

    @Override
    public Purchase convertToPurchase(ProductOrder productOrder) {
        Purchase purchase = new Purchase();

        purchase.setStatus(PurchaseStatus.NOT_ACTIVE);
        purchase.setPaidDate(productOrder.getPaidDate());
        purchase.setExpirationDate(null);

        purchase.setProduct(productOrder.getProduct());

        purchase.setUser(productOrder.getUser());

        return purchase;
    }

    @Override
    public PendingOrderDTO convertToPendingOrderDTO(ProductOrder productOrder) {
        PendingOrderDTO convertedDTO = new PendingOrderDTO();

        convertedDTO.setId(productOrder.getId());
        convertedDTO.setStatus(productOrder.getStatus().getDisplayValue());
        convertedDTO.setOrderDate(productOrder.getOrderDate());

        convertedDTO.setName(productOrder.getProduct().getName());
        convertedDTO.setPrice(productOrder.getProduct().getPrice());

        return convertedDTO;
    }

    @Override
    public BuyOrderDTO convertToBuyOrderDTO(Purchase purchase) {
        BuyOrderDTO buyOrderDTO = new BuyOrderDTO();

        buyOrderDTO.setId(purchase.getId());
        buyOrderDTO.setStatus(purchase.getStatus().getDisplayValue());
        buyOrderDTO.setPaidDate(purchase.getPaidDate());
        buyOrderDTO.setExpirationDate(null);

        buyOrderDTO.setProductName(purchase.getProduct().getName());

        return buyOrderDTO;
    }

    @Override
    public ProductOrder findOrderById(Long orderId) {
        return orderRepository.findProductOrderById(orderId);
    }

    @Override
    public void deleteOrder(ProductOrder productOrder) {
        orderRepository.delete(productOrder);
    }

    @Override
    public void deleteOrderList(List<ProductOrder> productOrderList) {
        for (ProductOrder productOrder : productOrderList) {
            deleteOrder(productOrder);
        }
    }
}
