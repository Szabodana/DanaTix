package com.project.danatix.models.DTOs;

import com.project.danatix.models.ProductType;
import com.project.danatix.models.PurchaseStatus;

import java.util.Date;

public class QRProductDTO {
    private String name;
    private int duration;
    private ProductType type;
    private String status;
    private Date paidDate;
    private Date expirationDate;

    public QRProductDTO() {
    }

    public QRProductDTO(String name,
                        int duration,
                        ProductType type,
                        String status,
                        Date paidDate,
                        Date expirationDate) {
        this.name = name;
        this.duration = duration;
        this.type = type;
        this.status = status;
        this.paidDate = paidDate;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
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
}
