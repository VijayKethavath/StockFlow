package com.stockflow.controller;

import com.stockflow.dto.DashboardResponse;
import com.stockflow.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
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
        Long orgId = (Long) request.getAttribute("organizationId");
        return ResponseEntity.ok(dashboardService.getDashboard(orgId));
    }
}
