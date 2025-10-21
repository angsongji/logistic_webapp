package com.uteexpress.entity;

public enum PartnerType {
    SHIPPING_PARTNER("Đối tác vận chuyển"),
    SUPPLIER("Nhà cung cấp"),
    BANK("Ngân hàng"),
    INSURANCE("Bảo hiểm"),
    OTHER("Khác");

    private final String displayName;

    PartnerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
