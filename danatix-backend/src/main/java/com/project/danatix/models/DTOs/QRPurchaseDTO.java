package com.project.danatix.models.DTOs;

public class QRPurchaseDTO {
    private QRUserDTO user;
    private QRProductDTO product;

    public QRPurchaseDTO() {
    }

    public QRPurchaseDTO(QRUserDTO qrUserDTO, QRProductDTO product) {
        this.user = qrUserDTO;
        this.product = product;
    }

    public QRUserDTO getUser() {
        return user;
    }

    public void setUser(QRUserDTO user) {
        this.user = user;
    }

    public QRProductDTO getProduct() {
        return product;
    }

    public void setProduct(QRProductDTO product) {
        this.product = product;
    }
}
