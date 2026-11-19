package com.stockflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SettingsRequest {

    @NotNull
    @Min(0)
    private Integer defaultLowStockThreshold;

    public SettingsRequest() {
    }

    public SettingsRequest(Integer defaultLowStockThreshold) {
        this.defaultLowStockThreshold = defaultLowStockThreshold;
    }

    public Integer getDefaultLowStockThreshold() {
        return defaultLowStockThreshold;
    }

    public void setDefaultLowStockThreshold(Integer defaultLowStockThreshold) {
        this.defaultLowStockThreshold = defaultLowStockThreshold;
    }
}
