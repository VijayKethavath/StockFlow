package com.stockflow.controller;

import com.stockflow.dto.DashboardResponse;
import com.stockflow.security.CustomUserDetails;
import com.stockflow.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(HttpServletRequest request) {
        Long orgId = getOrganizationId(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(dashboardService.getDashboard(orgId));
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
