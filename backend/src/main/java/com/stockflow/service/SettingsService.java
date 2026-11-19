package com.stockflow.service;

import com.stockflow.dto.SettingsRequest;
import com.stockflow.dto.SettingsResponse;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.repository.OrganizationSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

    private final OrganizationSettingsRepository settingsRepository;

    public SettingsService(OrganizationSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public SettingsResponse getSettings(Long organizationId) {
        OrganizationSettings settings = settingsRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found for organization"));
        return new SettingsResponse(settings.getDefaultLowStockThreshold());
    }

    @Transactional
    public SettingsResponse updateSettings(Long organizationId, SettingsRequest request) {
        OrganizationSettings settings = settingsRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found for organization"));
        settings.setDefaultLowStockThreshold(request.getDefaultLowStockThreshold());
        settingsRepository.save(settings);
        return new SettingsResponse(settings.getDefaultLowStockThreshold());
    }
}
