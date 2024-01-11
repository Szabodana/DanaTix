package com.project.danatix.services;

import com.project.danatix.models.DTOs.BuyOrderDTO;
import com.project.danatix.models.DTOs.PendingOrderDTO;
import com.project.danatix.models.ProductOrder;
import com.project.danatix.models.Purchase;
import com.project.danatix.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductOrderService {
    ProductOrder createOrder(long productId, User user);

    List<ProductOrder> findOrdersByUser(User user);

    List<ProductOrder> findPendingOrdersByUser(User user);

    List<ProductOrder> convertPendingOrders(List<ProductOrder> pendingOrders);

    Purchase convertToPurchase(ProductOrder productOrder);

    PendingOrderDTO convertToPendingOrderDTO(ProductOrder productOrder);

    BuyOrderDTO convertToBuyOrderDTO(Purchase purchase);

    ProductOrder findOrderById (Long orderId);

    void deleteOrder(ProductOrder productOrder);

    void deleteOrderList(List<ProductOrder> productOrderList);
}
