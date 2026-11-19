package com.stockflow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "organization_settings")
public class OrganizationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer defaultLowStockThreshold = 10;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", unique = true)
    private Organization organization;

    public OrganizationSettings() {
    }

    public OrganizationSettings(Organization organization) {
        this.organization = organization;
    }

    public OrganizationSettings(Integer defaultLowStockThreshold, Organization organization) {
        this.defaultLowStockThreshold = defaultLowStockThreshold;
        this.organization = organization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDefaultLowStockThreshold() {
        return defaultLowStockThreshold;
    }

    public void setDefaultLowStockThreshold(Integer defaultLowStockThreshold) {
        this.defaultLowStockThreshold = defaultLowStockThreshold;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
