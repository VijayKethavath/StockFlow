package com.stockflow.controller;

import com.stockflow.dto.SettingsRequest;
import com.stockflow.dto.SettingsResponse;
import com.stockflow.service.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<SettingsResponse> getSettings(HttpServletRequest request) {
        Long orgId = (Long) request.getAttribute("organizationId");
        return ResponseEntity.ok(settingsService.getSettings(orgId));
    }

    @PutMapping
    public ResponseEntity<SettingsResponse> updateSettings(@Valid @RequestBody SettingsRequest settingsRequest,
                                                           HttpServletRequest request) {
        Long orgId = (Long) request.getAttribute("organizationId");
        return ResponseEntity.ok(settingsService.updateSettings(orgId, settingsRequest));
    }
}
