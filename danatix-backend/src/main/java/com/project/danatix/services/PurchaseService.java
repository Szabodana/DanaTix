package com.project.danatix.services;

import com.project.danatix.models.DTOs.PurchaseDTO;
import com.project.danatix.models.DTOs.QRPurchaseDTO;
import com.project.danatix.models.Purchase;
import com.project.danatix.models.User;

import java.util.List;

public interface PurchaseService {

    List<Purchase> findAllPurchases(User user);

    PurchaseDTO convertToPurchaseDTO(Purchase purchase);

    QRPurchaseDTO convertToQRPurchaseDTO(Purchase purchase);

    Purchase findPurchaseById(long id);

    void savePurchase(Purchase purchase);

    public Purchase useUp(long id);

    boolean checkUserValidity(Purchase purchase, User user);
}
