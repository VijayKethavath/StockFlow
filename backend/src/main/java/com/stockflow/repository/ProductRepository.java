package com.stockflow.repository;

import com.stockflow.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrganizationId(Long organizationId);

    Optional<Product> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsBySkuAndOrganizationId(String sku, Long organizationId);

    boolean existsBySkuAndOrganizationIdAndIdNot(String sku, Long organizationId, Long id);

    @Query("SELECT p FROM Product p WHERE p.organization.id = :orgId AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchByNameOrSku(@Param("orgId") Long orgId, @Param("query") String query);

    long countByOrganizationId(Long organizationId);

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p WHERE p.organization.id = :organizationId")
    long sumQuantityByOrganizationId(@Param("organizationId") Long organizationId);
}
