package com.project.danatix.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;

import java.util.Date;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(value = EnumType.STRING)
    private PurchaseStatus status;

    private Date paidDate;

    private Date expirationDate;
    @ManyToOne
    private Product product;

    @JsonIgnore
    @ManyToOne
    private User user;

    public Purchase() {
    }

    public Purchase(PurchaseStatus status, Date paidDate, Date expirationDate, Product product, User user) {
        this.status = status;
        this.paidDate = paidDate;
        this.expirationDate = expirationDate;
        this.product = product;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
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
}
