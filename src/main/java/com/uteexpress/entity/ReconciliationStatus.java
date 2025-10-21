package com.uteexpress.entity;

public enum ReconciliationStatus {
    PENDING("Chờ đối soát"),
    MATCHED("Đã khớp"),
    DISCREPANCY("Có chênh lệch"),
    RESOLVED("Đã giải quyết");

    private final String displayName;

    ReconciliationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
