package com.uteexpress.entity;

public enum ServiceType {
    CHUAN("Chuẩn", 1.0),
    NHANH("Nhanh", 1.5),
    TIET_KIEM("Tiết kiệm", 0.8);

    private final String displayName;
    private final double multiplier;

    ServiceType(String displayName, double multiplier) {
        this.displayName = displayName;
        this.multiplier = multiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
