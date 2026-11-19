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

    @Query("SELECT p FROM Product p WHERE p.organization.id = :organizationId")
    List<Product> findAllByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.organization.id = :organizationId")
    Optional<Product> findByIdAndOrganizationId(@Param("id") Long id, @Param("organizationId") Long organizationId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.sku = :sku AND p.organization.id = :organizationId")
    boolean existsBySkuAndOrganizationId(@Param("sku") String sku, @Param("organizationId") Long organizationId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.sku = :sku AND p.organization.id = :organizationId AND p.id <> :id")
    boolean existsBySkuAndOrganizationIdAndIdNot(@Param("sku") String sku, @Param("organizationId") Long organizationId, @Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.organization.id = :orgId AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchByNameOrSku(@Param("orgId") Long orgId, @Param("query") String query);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.organization.id = :orgId")
    long countByOrganizationId(@Param("orgId") Long orgId);

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p WHERE p.organization.id = :organizationId")
    long sumQuantityByOrganizationId(@Param("organizationId") Long organizationId);
}
