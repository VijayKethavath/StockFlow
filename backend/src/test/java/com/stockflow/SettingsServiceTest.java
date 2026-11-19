package com.stockflow;

import com.stockflow.dto.SettingsRequest;
import com.stockflow.dto.SettingsResponse;
import com.stockflow.entity.Organization;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.OrganizationSettingsRepository;
import com.stockflow.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SettingsServiceTest {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationSettingsRepository settingsRepository;

    private Long orgId;

    @BeforeEach
    void setUp() {
        Organization org = new Organization("SettingsOrg");
        org = organizationRepository.save(org);
        orgId = org.getId();

        OrganizationSettings settings = new OrganizationSettings(10, org);
        settingsRepository.save(settings);
    }

    @Test
    void testGetSettings() {
        SettingsResponse response = settingsService.getSettings(orgId);
        assertEquals(10, response.getDefaultLowStockThreshold());
    }

    @Test
    void testUpdateSettings() {
        SettingsRequest request = new SettingsRequest(25);
        SettingsResponse response = settingsService.updateSettings(orgId, request);

        assertEquals(25, response.getDefaultLowStockThreshold());

        // Verify persistence
        SettingsResponse fetched = settingsService.getSettings(orgId);
        assertEquals(25, fetched.getDefaultLowStockThreshold());
    }
}
