package com.stockflow.service;

import com.stockflow.dto.SettingsRequest;
import com.stockflow.dto.SettingsResponse;
import com.stockflow.entity.Organization;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.OrganizationSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

    private final OrganizationSettingsRepository settingsRepository;
    private final OrganizationRepository organizationRepository;

    public SettingsService(OrganizationSettingsRepository settingsRepository,
                           OrganizationRepository organizationRepository) {
        this.settingsRepository = settingsRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public SettingsResponse getSettings(Long organizationId) {
        OrganizationSettings settings = settingsRepository.findByOrganizationId(organizationId)
                .orElseGet(() -> createDefaultSettings(organizationId));
        return new SettingsResponse(settings.getDefaultLowStockThreshold());
    }

    @Transactional
    public SettingsResponse updateSettings(Long organizationId, SettingsRequest request) {
        OrganizationSettings settings = settingsRepository.findByOrganizationId(organizationId)
                .orElseGet(() -> createDefaultSettings(organizationId));
        settings.setDefaultLowStockThreshold(request.getDefaultLowStockThreshold());
        settings = settingsRepository.save(settings);
        return new SettingsResponse(settings.getDefaultLowStockThreshold());
    }

    private OrganizationSettings createDefaultSettings(Long organizationId) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        OrganizationSettings settings = new OrganizationSettings(10, org);
        return settingsRepository.save(settings);
    }
}
