package com.stockflow;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.entity.Organization;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.entity.User;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.OrganizationSettingsRepository;
import com.stockflow.repository.UserRepository;
import com.stockflow.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationSettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long orgId;
    private Long org2Id;

    @BeforeEach
    void setUp() {
        Organization org = new Organization("TestOrg");
        org = organizationRepository.save(org);
        orgId = org.getId();

        OrganizationSettings settings = new OrganizationSettings(10, org);
        settingsRepository.save(settings);

        User user = new User("test@example.com", passwordEncoder.encode("password"), org);
        userRepository.save(user);

        // Second org for isolation tests
        Organization org2 = new Organization("TestOrg2");
        org2 = organizationRepository.save(org2);
        org2Id = org2.getId();

        OrganizationSettings settings2 = new OrganizationSettings(10, org2);
        settingsRepository.save(settings2);
    }

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest("Widget", "WDG-001", "A widget", 100,
                new BigDecimal("9.99"), new BigDecimal("19.99"), 5);
        ProductResponse response = productService.createProduct(request, orgId);

        assertNotNull(response.getId());
        assertEquals("Widget", response.getName());
        assertEquals("WDG-001", response.getSku());
        assertEquals(100, response.getQuantity());
        assertFalse(response.isLowStock());
    }

    @Test
    void testGetAllProducts() {
        productService.createProduct(new ProductRequest("P1", "SKU-1", null, 10,
                null, null, null), orgId);
        productService.createProduct(new ProductRequest("P2", "SKU-2", null, 20,
                null, null, null), orgId);

        List<ProductResponse> products = productService.getAllProducts(orgId);
        assertEquals(2, products.size());
    }

    @Test
    void testGetProductById() {
        ProductResponse created = productService.createProduct(
                new ProductRequest("Single", "SNG-001", "desc", 50, null, null, null), orgId);

        ProductResponse found = productService.getProductById(created.getId(), orgId);
        assertEquals("Single", found.getName());
        assertEquals("SNG-001", found.getSku());
    }

    @Test
    void testUpdateProduct() {
        ProductResponse created = productService.createProduct(
                new ProductRequest("Original", "OG-001", null, 10, null, null, null), orgId);

        ProductRequest updateRequest = new ProductRequest("Updated", "OG-001", "updated desc", 20,
                new BigDecimal("5.00"), new BigDecimal("10.00"), 3);
        ProductResponse updated = productService.updateProduct(created.getId(), updateRequest, orgId);

        assertEquals("Updated", updated.getName());
        assertEquals(20, updated.getQuantity());
        assertEquals("updated desc", updated.getDescription());
    }

    @Test
    void testDeleteProduct() {
        ProductResponse created = productService.createProduct(
                new ProductRequest("ToDelete", "DEL-001", null, 10, null, null, null), orgId);

        productService.deleteProduct(created.getId(), orgId);

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(created.getId(), orgId));
    }

    @Test
    void testDuplicateSku() {
        productService.createProduct(new ProductRequest("P1", "DUP-SKU", null, 10,
                null, null, null), orgId);

        assertThrows(DuplicateResourceException.class, () ->
                productService.createProduct(new ProductRequest("P2", "DUP-SKU", null, 5,
                        null, null, null), orgId));
    }

    @Test
    void testSearchProducts() {
        productService.createProduct(new ProductRequest("Alpha Widget", "AW-001", null, 10,
                null, null, null), orgId);
        productService.createProduct(new ProductRequest("Beta Gadget", "BG-001", null, 20,
                null, null, null), orgId);

        List<ProductResponse> results = productService.searchProducts(orgId, "widget");
        assertEquals(1, results.size());
        assertEquals("Alpha Widget", results.get(0).getName());

        List<ProductResponse> skuResults = productService.searchProducts(orgId, "BG");
        assertEquals(1, skuResults.size());
        assertEquals("Beta Gadget", skuResults.get(0).getName());
    }

    @Test
    void testOrganizationIsolation() {
        productService.createProduct(new ProductRequest("Org1Product", "ISO-001", null, 10,
                null, null, null), orgId);

        List<ProductResponse> org2Products = productService.getAllProducts(org2Id);
        assertTrue(org2Products.isEmpty());

        // Same SKU allowed in different org
        ProductResponse org2Product = productService.createProduct(
                new ProductRequest("Org2Product", "ISO-001", null, 5, null, null, null), org2Id);
        assertNotNull(org2Product.getId());
    }
}
