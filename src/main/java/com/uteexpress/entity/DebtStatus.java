package com.uteexpress.entity;

public enum DebtStatus {
    PENDING("Chờ thanh toán"),
    PARTIAL("Thanh toán một phần"),
    PAID("Đã thanh toán"),
    OVERDUE("Quá hạn"),
    CANCELLED("Đã hủy");

    private final String displayName;

    DebtStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
