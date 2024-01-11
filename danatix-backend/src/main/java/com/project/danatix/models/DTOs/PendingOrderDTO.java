package com.project.danatix.models.DTOs;

import java.util.Date;

public class PendingOrderDTO {

    private long id;
    private String status;
    private Date orderDate;
    private String name;
    private int price;


    public PendingOrderDTO(long id, String status, Date orderDate, String name, int price) {
        this.id = id;
        this.status = status;
        this.orderDate = orderDate;
        this.name = name;
        this.price = price;
    }

    public PendingOrderDTO() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
