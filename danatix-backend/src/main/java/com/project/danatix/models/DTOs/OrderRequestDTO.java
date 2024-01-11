package com.project.danatix.models.DTOs;

import jakarta.validation.constraints.Min;

public class OrderRequestDTO {

    @Min(1)
    private long productId;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(long productId) {
        this.productId = productId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
