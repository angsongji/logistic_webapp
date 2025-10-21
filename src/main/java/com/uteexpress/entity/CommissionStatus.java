package com.uteexpress.entity;

public enum CommissionStatus {
    PENDING("Chờ thanh toán"),
    APPROVED("Đã duyệt"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy");

    private final String displayName;

    CommissionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
