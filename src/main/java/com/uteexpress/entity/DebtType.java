package com.uteexpress.entity;

public enum DebtType {
    CUSTOMER_DEBT("Công nợ khách hàng"),
    SHIPPER_DEBT("Công nợ shipper"),
    PARTNER_DEBT("Công nợ đối tác"),
    SUPPLIER_DEBT("Công nợ nhà cung cấp");

    private final String displayName;

    DebtType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
