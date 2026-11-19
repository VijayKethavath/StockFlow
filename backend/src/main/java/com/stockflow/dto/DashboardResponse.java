package com.stockflow.dto;

import java.util.List;

public class DashboardResponse {

    private long totalProducts;
    private long totalStockQuantity;
    private long lowStockCount;
    private List<ProductResponse> lowStockProducts;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalProducts, long totalStockQuantity, long lowStockCount,
                             List<ProductResponse> lowStockProducts) {
        this.totalProducts = totalProducts;
        this.totalStockQuantity = totalStockQuantity;
        this.lowStockCount = lowStockCount;
        this.lowStockProducts = lowStockProducts;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalStockQuantity() {
        return totalStockQuantity;
    }

    public void setTotalStockQuantity(long totalStockQuantity) {
        this.totalStockQuantity = totalStockQuantity;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public List<ProductResponse> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<ProductResponse> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }
}
