package com.project.danatix.controllers;

import com.project.danatix.models.DTOs.BuyOrderDTO;
import com.project.danatix.models.DTOs.OrderRequestDTO;
import com.project.danatix.models.DTOs.PendingOrderDTO;
import com.project.danatix.models.DTOs.ProductOrderDTO;
import com.project.danatix.models.OrderStatus;
import com.project.danatix.models.ProductOrder;
import com.project.danatix.models.Purchase;
import com.project.danatix.models.User;
import com.project.danatix.repositories.PurchaseRepository;
import com.project.danatix.services.ProductOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final ProductOrderService productOrderService;
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public OrdersController(ProductOrderService productOrderService, PurchaseRepository purchaseRepository) {
        this.productOrderService = productOrderService;
        this.purchaseRepository = purchaseRepository;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map> handleMissingProductId() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Product ID is required.");

        return ResponseEntity.status(400).body(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map> handleWrongProductId(Exception e) {
        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());

        return ResponseEntity.status(400).body(result);
    }

    @PostMapping
    public ResponseEntity<ProductOrderDTO> makeNewOrder(
            @Valid @RequestBody OrderRequestDTO request,
            @AuthenticationPrincipal User user) {

        ProductOrderDTO newOrder = new ProductOrderDTO(productOrderService.createOrder(request.getProductId(), user));

        return ResponseEntity.ok(newOrder);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPendingOrders(@AuthenticationPrincipal User user) {

        Map<String, List<PendingOrderDTO>> pendingOrdersResponse = new HashMap<>();
        List<ProductOrder> productOrderList = productOrderService.findPendingOrdersByUser(user);
        List<PendingOrderDTO> pendingOrderDTOList = new ArrayList<>();

        if (productOrderList.isEmpty()) {
            return ResponseEntity.ok().body("There are no pending orders");

        } else {
            for (ProductOrder productOrder : productOrderList) {
                pendingOrderDTOList.add(productOrderService.convertToPendingOrderDTO(productOrder));
            }

            pendingOrdersResponse.put("orders", pendingOrderDTOList);

            return ResponseEntity.ok().body(pendingOrdersResponse);
        }
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buyPendingOrders(@AuthenticationPrincipal User user) {

        Map<String, List<BuyOrderDTO>> buyOrdersResponse = new HashMap<>();
        List<BuyOrderDTO> buyOrderDTOList = new ArrayList<>();

        List<ProductOrder> productOrderList = productOrderService.findPendingOrdersByUser(user);

        productOrderService.convertPendingOrders(productOrderList);

        for (ProductOrder productOrder : productOrderList) {
            Purchase purchase = productOrderService.convertToPurchase(productOrder);
            purchaseRepository.save(purchase);

            buyOrderDTOList.add(productOrderService.convertToBuyOrderDTO(purchase));
        }
        buyOrdersResponse.put("purchases", buyOrderDTOList);
        return ResponseEntity.ok().body(buyOrdersResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrderById(@AuthenticationPrincipal User user,
                                             @PathVariable Long orderId) {

        ProductOrder productOrder = productOrderService.findOrderById(orderId);

        if(productOrder == null){
            return ResponseEntity.status(400).body("Order not found");
        }

        if(productOrder.getStatus() != OrderStatus.PENDING){
            return ResponseEntity.status(400).body("Order is not in PENDING status, cannot be deleted");
        }

        if(productOrder.getUser().getId() != user.getId()){
            return ResponseEntity.status(400).body("Order does not belong to the user, cannot be deleted");
        }

        productOrderService.deleteOrder(productOrder);

        return ResponseEntity.ok().body("Order successfully deleted");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<?> deletePendingOrders(@AuthenticationPrincipal User user){

        List<ProductOrder> pendingOrderList = productOrderService.findPendingOrdersByUser(user);

        if(pendingOrderList.isEmpty()){
            return ResponseEntity.ok().body("There are no pending orders to be deleted at the moment");
        }

        productOrderService.deleteOrderList(pendingOrderList);
        return ResponseEntity.ok().body("Pending orders have been successfully deleted");
    }
}