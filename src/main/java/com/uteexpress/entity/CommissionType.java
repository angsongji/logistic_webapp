package com.uteexpress.entity;

public enum CommissionType {
    DELIVERY("Hoa hồng giao hàng"),
    PICKUP("Hoa hồng lấy hàng"),
    BONUS("Thưởng"),
    PENALTY("Phạt");

    private final String displayName;

    CommissionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
