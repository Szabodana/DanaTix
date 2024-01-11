package com.project.danatix.controllers;

import com.project.danatix.models.DTOs.PurchaseDTO;
import com.project.danatix.models.DTOs.QRPurchaseDTO;
import com.project.danatix.models.Purchase;
import com.project.danatix.models.PurchaseStatus;
import com.project.danatix.models.User;
import com.project.danatix.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal User user) {

        Map<String, List<Purchase>> purchaseResponse = new HashMap<>();
        List<Purchase> purchaseList = purchaseService.findAllPurchases(user);

        if (purchaseList.isEmpty()) {
            return ResponseEntity.ok().body("There are no purchases at the moment.");

        }

        purchaseResponse.put("purchases", purchaseList);

        return ResponseEntity.ok().body(purchaseResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{purchaseId}")
    public ResponseEntity<?> getPurchase(@PathVariable Long purchaseId) {

        Purchase purchase = purchaseService.findPurchaseById(purchaseId);
    
        if (purchase == null) {
            return ResponseEntity.badRequest().body("The purchase was not found!");
        }

        QRPurchaseDTO response = purchaseService.convertToQRPurchaseDTO(purchase);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{purchaseId}")
    public ResponseEntity<?> updatePurchase(@PathVariable Long purchaseId, @RequestBody Map<String, String> requestBody, @AuthenticationPrincipal User user) {

        String statusRequest = requestBody.get("status");
        if (statusRequest == null || !statusRequest.equalsIgnoreCase(PurchaseStatus.ACTIVE.getDisplayValue())) {
            return ResponseEntity.badRequest().body("Invalid status. Only 'active' is allowed.");
        }
        Purchase purchase = purchaseService.findPurchaseById(purchaseId);

        if (purchase == null) {
            return ResponseEntity.badRequest().body("The purchase was not found!");
        }

        if (!(purchaseService.checkUserValidity(purchase, user))) {
            return ResponseEntity.badRequest().body("This purchase does not belong to the logged in user.");
        }

        Purchase updatedPurchase = purchaseService.useUp(purchaseId);

        PurchaseDTO response = purchaseService.convertToPurchaseDTO(updatedPurchase);
        return ResponseEntity.ok(response);
    }
}

