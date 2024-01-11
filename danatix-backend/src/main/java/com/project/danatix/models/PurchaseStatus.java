package com.project.danatix.models;

public enum PurchaseStatus {
    NOT_ACTIVE("not active"),
    ACTIVE("active"),
    EXPIRED("expired");

    private final String displayValue;

    PurchaseStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
