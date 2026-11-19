package com.stockflow;

import com.stockflow.dto.DashboardResponse;
import com.stockflow.dto.ProductRequest;
import com.stockflow.entity.Organization;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.OrganizationSettingsRepository;
import com.stockflow.service.DashboardService;
import com.stockflow.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationSettingsRepository settingsRepository;

    private Long orgId;

    @BeforeEach
    void setUp() {
        Organization org = new Organization("DashOrg");
        org = organizationRepository.save(org);
        orgId = org.getId();

        OrganizationSettings settings = new OrganizationSettings(10, org);
        settingsRepository.save(settings);
    }

    @Test
    void testEmptyDashboard() {
        DashboardResponse response = dashboardService.getDashboard(orgId);

        assertEquals(0, response.getTotalProducts());
        assertEquals(0, response.getTotalStockQuantity());
        assertEquals(0, response.getLowStockCount());
        assertTrue(response.getLowStockProducts().isEmpty());
    }

    @Test
    void testDashboardWithProducts() {
        productService.createProduct(new ProductRequest("P1", "SKU-1", null, 100,
                new BigDecimal("10.00"), new BigDecimal("20.00"), null), orgId);
        productService.createProduct(new ProductRequest("P2", "SKU-2", null, 50,
                null, null, null), orgId);

        DashboardResponse response = dashboardService.getDashboard(orgId);

        assertEquals(2, response.getTotalProducts());
        assertEquals(150, response.getTotalStockQuantity());
        assertEquals(0, response.getLowStockCount());
    }

    @Test
    void testDashboardLowStock() {
        // Default threshold is 10
        productService.createProduct(new ProductRequest("LowStock", "LS-001", null, 5,
                null, null, null), orgId);
        productService.createProduct(new ProductRequest("OkStock", "OS-001", null, 50,
                null, null, null), orgId);

        DashboardResponse response = dashboardService.getDashboard(orgId);

        assertEquals(2, response.getTotalProducts());
        assertEquals(55, response.getTotalStockQuantity());
        assertEquals(1, response.getLowStockCount());
        assertEquals("LowStock", response.getLowStockProducts().get(0).getName());
    }

    @Test
    void testDashboardCustomThreshold() {
        // Product with custom threshold of 20
        productService.createProduct(new ProductRequest("Custom", "CT-001", null, 15,
                null, null, 20), orgId);
        // Product using default threshold (10), quantity 15 is above 10
        productService.createProduct(new ProductRequest("Default", "DF-001", null, 15,
                null, null, null), orgId);

        DashboardResponse response = dashboardService.getDashboard(orgId);

        assertEquals(2, response.getTotalProducts());
        assertEquals(1, response.getLowStockCount());
        assertEquals("Custom", response.getLowStockProducts().get(0).getName());
    }
}
