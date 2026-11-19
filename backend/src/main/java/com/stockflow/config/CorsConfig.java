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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:https://stockflow-omega-gilt.vercel.app,http://localhost:5173,http://127.0.0.1:5173,http://localhost:3000}")
    private String allowedOriginsConfig;

    public List<String> resolveAllowedOrigins() {
        List<String> origins = new ArrayList<>();
        // Baseline allowed origins: exact production frontend + local development
        origins.add("https://stockflow-omega-gilt.vercel.app");
        origins.add("http://localhost:5173");
        origins.add("http://127.0.0.1:5173");
        origins.add("http://localhost:3000");

        // Merge from application.properties / application-test.properties configuration
        if (allowedOriginsConfig != null && !allowedOriginsConfig.isBlank()) {
            for (String raw : allowedOriginsConfig.split(",")) {
                String trimmed = sanitizeOrigin(raw);
                if (!trimmed.isEmpty() && !origins.contains(trimmed)) {
                    origins.add(trimmed);
                }
            }
        }

        // Merge from System environment variable CORS_ALLOWED_ORIGINS if set in production (e.g. Render)
        String envOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (envOrigins != null && !envOrigins.isBlank()) {
            for (String raw : envOrigins.split(",")) {
                String trimmed = sanitizeOrigin(raw);
                if (!trimmed.isEmpty() && !origins.contains(trimmed)) {
                    origins.add(trimmed);
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
        // Requirement 8: Do not use wildcard origins
        configuration.setAllowedOrigins(origins);

        // Requirement 1: Allow GET, POST, PUT, DELETE and OPTIONS methods (plus PATCH)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Requirement 2: Allow Content-Type and Authorization headers (plus common request headers)
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Exposed headers for client consumption
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Requirement 3: Handle OPTIONS preflight requests correctly with 1 hour caching
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Requirement 4: Ensure CORS runs before Spring Security filters.
     * Registering CorsFilter with HIGHEST_PRECEDENCE ensures it executes in the servlet container
     * BEFORE Spring Security's DelegatingFilterProxy (order -100).
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean(CorsConfigurationSource corsConfigurationSource) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = resolveAllowedOrigins();
        registry.addMapping("/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
