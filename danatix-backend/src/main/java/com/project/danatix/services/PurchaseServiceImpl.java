package com.project.danatix.services;

import com.project.danatix.models.*;
import com.project.danatix.models.DTOs.*;
import com.project.danatix.repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public List<Purchase> findAllPurchases(User user) {

        List<Purchase> purchases = purchaseRepository.findAllByUser(user);
        purchases.forEach(purchase -> {
            if(purchase.getStatus().equals(PurchaseStatus.ACTIVE) && purchase.getExpirationDate().before(new Date())) {
                purchase.setStatus(PurchaseStatus.EXPIRED);
            }
        });

        return purchases;
    }

    @Override
    public PurchaseDTO convertToPurchaseDTO(Purchase purchase) {
        PurchaseDTO convertedDTO = new PurchaseDTO();

        convertedDTO.setId(purchase.getId());
        convertedDTO.setStatus(purchase.getStatus().getDisplayValue());
        convertedDTO.setPaidDate(purchase.getPaidDate());
        convertedDTO.setExpirationDate(purchase.getExpirationDate());
        convertedDTO.setProductId(purchase.getProduct().getId());

        return convertedDTO;
    }

    @Override
    public QRPurchaseDTO convertToQRPurchaseDTO(Purchase purchase) {

        QRUserDTO qrUserDTO = new QRUserDTO();
        QRProductDTO qrProductDTO = new QRProductDTO();
        QRPurchaseDTO qrPurchaseDTO = new QRPurchaseDTO();

        qrUserDTO.setName(purchase.getUser().getName());
        qrUserDTO.setEmail(purchase.getUser().getEmail());

        qrProductDTO.setName(purchase.getProduct().getName());
        qrProductDTO.setDuration(purchase.getProduct().getDuration());
        qrProductDTO.setType(purchase.getProduct().getType());
        qrProductDTO.setStatus(purchase.getStatus().getDisplayValue());
        qrProductDTO.setPaidDate(purchase.getPaidDate());
        qrProductDTO.setExpirationDate(purchase.getExpirationDate());

        qrPurchaseDTO.setUser(qrUserDTO);
        qrPurchaseDTO.setProduct(qrProductDTO);

        return qrPurchaseDTO;
    }

    @Override
    public Purchase findPurchaseById(long id){
        return purchaseRepository.findById(id);
    }

    @Override
    public void savePurchase(Purchase purchase){
        purchaseRepository.save(purchase);
    }

    @Override
    public Purchase useUp(long id){
        Purchase purchase= purchaseRepository.findById(id);
        purchase.setStatus(PurchaseStatus.ACTIVE);
        Product product = purchase.getProduct();

        int ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
        int durationInMillis = (product.getType().equals(ProductType.ticket))
                ? ONE_HOUR_IN_MILLIS * product.getDuration()
                : ONE_HOUR_IN_MILLIS * product.getDuration() * 24;

        purchase.setExpirationDate(new Date(System.currentTimeMillis() + durationInMillis));
        purchaseRepository.save(purchase);

        return purchase;
    }

    @Override
    public boolean checkUserValidity(Purchase purchase, User user){
        return purchase.getUser().getId()==user.getId();
    }
}
