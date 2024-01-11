package com.project.danatix.models.DTOs;

import com.project.danatix.models.ProductOrder;

import java.util.Date;

public class ProductOrderDTO {
    private long id;
    private String status;
    private Date orderDate;
    private long productId;

    public ProductOrderDTO() {
    }

    public ProductOrderDTO(ProductOrder order) {
        this.id = order.getId();
        this.status = order.getStatus().toString();
        this.orderDate = order.getOrderDate();
        this.productId = order.getProduct().getId();
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
