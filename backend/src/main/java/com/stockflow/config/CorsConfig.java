package com.stockflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:https://stock-flow-omega-gilt.vercel.app,https://stockflow-omega-gilt.vercel.app,http://localhost:5173,http://127.0.0.1:5173,http://localhost:3000}")
    private String allowedOriginsConfig;

    public List<String> resolveAllowedOrigins() {
        List<String> origins = new ArrayList<>();
        // 1. Exact Vercel frontend origins
        origins.add("https://stock-flow-omega-gilt.vercel.app");
        origins.add("https://stockflow-omega-gilt.vercel.app");
        // 2. Local development origins
        origins.add("http://localhost:5173");
        origins.add("http://127.0.0.1:5173");
        origins.add("http://localhost:3000");

        // Merge from property configuration if provided
        if (allowedOriginsConfig != null && !allowedOriginsConfig.isBlank()) {
            for (String raw : allowedOriginsConfig.split(",")) {
                String sanitized = sanitizeOrigin(raw);
                if (!sanitized.isEmpty() && !origins.contains(sanitized)) {
                    origins.add(sanitized);
                }
            }
        }

        // Merge from CORS_ALLOWED_ORIGINS environment variable if set on Render
        String envOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (envOrigins != null && !envOrigins.isBlank()) {
            for (String raw : envOrigins.split(",")) {
                String sanitized = sanitizeOrigin(raw);
                if (!sanitized.isEmpty() && !origins.contains(sanitized)) {
                    origins.add(sanitized);
                }
            }
        }

        return origins;
    }

    private String sanitizeOrigin(String raw) {
        String trimmed = raw.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = resolveAllowedOrigins();
        configuration.setAllowedOrigins(origins);

        // 4. Allow GET, POST, PUT, DELETE, OPTIONS, PATCH
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 2 & 3. Prefer allowedHeaders("*") to allow all request headers (Content-Type, Authorization, Cache-Control, Pragma, Accept, etc.)
        configuration.setAllowedHeaders(List.of("*"));

        // Exposed headers for client access
        configuration.setExposedHeaders(Arrays.asList(
                "*",
                "Authorization",
                "Content-Type",
                "Cache-Control",
                "Pragma",
                "Expires"
        ));

        // Enable credentials (compatible with explicit origins and wildcard headers)
        configuration.setAllowCredentials(true);

        // 5. Preflight cache max age (1 hour)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 6. Ensure CORS processing happens before Spring Security.
     * Registering CorsFilter with HIGHEST_PRECEDENCE ensures the servlet container executes it
     * BEFORE Spring Security's DelegatingFilterProxy (order -100).
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean(CorsConfigurationSource corsConfigurationSource) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        bean.addUrlPatterns("/*");
        return bean;
    }
}
