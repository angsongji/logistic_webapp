package com.uteexpress.entity;

public enum PartnerStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động"),
    SUSPENDED("Tạm dừng"),
    TERMINATED("Chấm dứt");

    private final String displayName;

    PartnerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
