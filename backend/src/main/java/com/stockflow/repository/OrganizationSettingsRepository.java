package com.stockflow.repository;

import com.stockflow.entity.OrganizationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationSettingsRepository extends JpaRepository<OrganizationSettings, Long> {

    @Query("SELECT s FROM OrganizationSettings s WHERE s.organization.id = :organizationId")
    Optional<OrganizationSettings> findByOrganizationId(@Param("organizationId") Long organizationId);
}
