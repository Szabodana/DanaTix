package com.project.danatix.models.DTOs;

import java.util.Date;

public class PurchaseDTO {

    private long id;
    private String status;
    private Date paidDate;
    private Date expirationDate;
    private long productId;

    public PurchaseDTO() {
    }

    public PurchaseDTO(long id, String status, Date paidDate, Date expirationDate, long productId) {
        this.id = id;
        this.status = status;
        this.paidDate = paidDate;
        this.expirationDate = expirationDate;
        this.productId = productId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}

