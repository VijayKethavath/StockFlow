package com.stockflow;

import com.stockflow.dto.DashboardResponse;
import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.dto.SettingsRequest;
import com.stockflow.entity.Organization;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.OrganizationSettingsRepository;
import com.stockflow.service.DashboardService;
import com.stockflow.service.ProductService;
import com.stockflow.service.SettingsService;
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
    private SettingsService settingsService;

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
        assertEquals(5, response.getLowStockProducts().get(0).getQuantity());
        assertEquals(10, response.getLowStockProducts().get(0).getLowStockThreshold());
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
        assertEquals(20, response.getLowStockProducts().get(0).getLowStockThreshold());
    }

    @Test
    void testProductQuantityUpdateAndDefaultThresholdScenario() {
        // User scenario:
        // Product initially created with quantity = 20 (not low stock with threshold = 10)
        ProductResponse created = productService.createProduct(
                new ProductRequest("Widget", "WDG-001", "A sample widget", 20,
                        new BigDecimal("5.00"), new BigDecimal("10.00"), null), orgId);

        // Before update: 0 low stock items
        DashboardResponse initialDashboard = dashboardService.getDashboard(orgId);
        assertEquals(0, initialDashboard.getLowStockCount());

        // Update product quantity to 5
        productService.updateProduct(created.getId(),
                new ProductRequest("Widget", "WDG-001", "A sample widget", 5,
                        new BigDecimal("5.00"), new BigDecimal("10.00"), null), orgId);

        // Set default low-stock threshold to 10
        settingsService.updateSettings(orgId, new SettingsRequest(10));

        // Verify Dashboard immediately reflects:
        // Product quantity = 5, Default threshold = 10, Expected low-stock count = 1
        DashboardResponse updatedDashboard = dashboardService.getDashboard(orgId);
        assertEquals(1, updatedDashboard.getLowStockCount());
        assertEquals(1, updatedDashboard.getLowStockProducts().size());

        ProductResponse lowStockItem = updatedDashboard.getLowStockProducts().get(0);
        assertEquals("Widget", lowStockItem.getName());
        assertEquals("WDG-001", lowStockItem.getSku());
        assertEquals(5, lowStockItem.getQuantity());
        assertEquals(10, lowStockItem.getLowStockThreshold());
        assertTrue(lowStockItem.isLowStock());
    }
}
