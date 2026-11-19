package com.stockflow.dto;

public class SettingsResponse {

    private Integer defaultLowStockThreshold;

    public SettingsResponse() {
    }

    public SettingsResponse(Integer defaultLowStockThreshold) {
        this.defaultLowStockThreshold = defaultLowStockThreshold;
    }

    public Integer getDefaultLowStockThreshold() {
        return defaultLowStockThreshold;
    }

    public void setDefaultLowStockThreshold(Integer defaultLowStockThreshold) {
        this.defaultLowStockThreshold = defaultLowStockThreshold;
    }
}
