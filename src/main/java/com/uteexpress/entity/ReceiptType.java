package com.uteexpress.entity;

public enum ReceiptType {
    PAYMENT("Phiếu thu"),
    REFUND("Phiếu hoàn"),
    COMMISSION("Hoa hồng"),
    EXPENSE("Chi phí");

    private final String displayName;

    ReceiptType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
