package com.stockflow.controller;

import com.stockflow.dto.SettingsRequest;
import com.stockflow.dto.SettingsResponse;
import com.stockflow.security.CustomUserDetails;
import com.stockflow.service.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Long orgId = getOrganizationId(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(settingsService.getSettings(orgId));
    }

    @PutMapping
    public ResponseEntity<SettingsResponse> updateSettings(@Valid @RequestBody SettingsRequest settingsRequest,
                                                           HttpServletRequest request) {
        Long orgId = getOrganizationId(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(settingsService.updateSettings(orgId, settingsRequest));
    }

    private Long getOrganizationId(HttpServletRequest request) {
        Object orgIdAttr = request.getAttribute("organizationId");
        if (orgIdAttr instanceof Number) {
            return ((Number) orgIdAttr).longValue();
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getOrganizationId();
        }
        throw new IllegalArgumentException("Organization context missing");
    }
}
