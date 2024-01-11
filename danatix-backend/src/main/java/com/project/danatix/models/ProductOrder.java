package com.project.danatix.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "orders")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private Date orderDate;

    private Date paidDate;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;

    public ProductOrder() {
        this.orderDate = new Date(System.currentTimeMillis());
    }

    public ProductOrder(OrderStatus status, Product product, User user) {
        this.status = status;
        this.orderDate = new Date();
        this.product = product;
        this.user = user;
        this.paidDate = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }
}
