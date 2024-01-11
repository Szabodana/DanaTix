package com.project.danatix.models;

public enum OrderStatus {
    PENDING("pending"),
    PAID("paid");

    private final String displayValue;

    OrderStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
