package com.uteexpress.entity;

public enum PayrollStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy");

    private final String displayName;

    PayrollStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
