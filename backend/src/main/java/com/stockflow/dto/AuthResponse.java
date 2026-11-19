package com.stockflow.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String organizationName;
    private Long organizationId;

    public AuthResponse() {
    }

    public AuthResponse(String token, String email, String organizationName, Long organizationId) {
        this.token = token;
        this.email = email;
        this.organizationName = organizationName;
        this.organizationId = organizationId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
