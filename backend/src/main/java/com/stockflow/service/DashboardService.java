package com.stockflow.service;

import com.stockflow.dto.DashboardResponse;
import com.stockflow.dto.ProductResponse;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.entity.Product;
import com.stockflow.repository.OrganizationSettingsRepository;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final OrganizationSettingsRepository settingsRepository;

    public DashboardService(ProductRepository productRepository,
                            OrganizationSettingsRepository settingsRepository) {
        this.productRepository = productRepository;
        this.settingsRepository = settingsRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long organizationId) {
        long totalProducts = productRepository.countByOrganizationId(organizationId);
        long totalStockQuantity = productRepository.sumQuantityByOrganizationId(organizationId);

        int defaultThreshold = settingsRepository.findByOrganizationId(organizationId)
                .map(OrganizationSettings::getDefaultLowStockThreshold)
                .orElse(10);

        List<Product> allProducts = productRepository.findAllByOrganizationId(organizationId);

        List<ProductResponse> lowStockProducts = allProducts.stream()
                .filter(p -> {
                    int effectiveThreshold = p.getLowStockThreshold() != null
                            ? p.getLowStockThreshold()
                            : defaultThreshold;
                    return p.getQuantity() <= effectiveThreshold;
                })
                .map(p -> {
                    int effectiveThreshold = p.getLowStockThreshold() != null
                            ? p.getLowStockThreshold()
                            : defaultThreshold;
                    return new ProductResponse(
                            p.getId(),
                            p.getName(),
                            p.getSku(),
                            p.getDescription(),
                            p.getQuantity(),
                            p.getCostPrice(),
                            p.getSellingPrice(),
                            effectiveThreshold,
                            true,
                            p.getCreatedAt(),
                            p.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());

        return new DashboardResponse(totalProducts, totalStockQuantity,
                lowStockProducts.size(), lowStockProducts);
    }
}
