package com.stockflow.service;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.entity.Product;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.OrganizationSettingsRepository;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationSettingsRepository settingsRepository;

    public ProductService(ProductRepository productRepository,
                          OrganizationRepository organizationRepository,
                          OrganizationSettingsRepository settingsRepository) {
        this.productRepository = productRepository;
        this.organizationRepository = organizationRepository;
        this.settingsRepository = settingsRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Long organizationId) {
        int defaultThreshold = getDefaultThreshold(organizationId);
        return productRepository.findAllByOrganizationId(organizationId)
                .stream()
                .map(p -> toProductResponse(p, defaultThreshold))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id, Long organizationId) {
        int defaultThreshold = getDefaultThreshold(organizationId);
        Product product = productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toProductResponse(product, defaultThreshold);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, Long organizationId) {
        if (productRepository.existsBySkuAndOrganizationId(request.getSku(), organizationId)) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setCostPrice(request.getCostPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setLowStockThreshold(request.getLowStockThreshold());
        product.setOrganization(organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found")));

        product = productRepository.save(product);
        int defaultThreshold = getDefaultThreshold(organizationId);
        return toProductResponse(product, defaultThreshold);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, Long organizationId) {
        Product product = productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (productRepository.existsBySkuAndOrganizationIdAndIdNot(request.getSku(), organizationId, id)) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setCostPrice(request.getCostPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setLowStockThreshold(request.getLowStockThreshold());

        product = productRepository.save(product);
        int defaultThreshold = getDefaultThreshold(organizationId);
        return toProductResponse(product, defaultThreshold);
    }

    @Transactional
    public void deleteProduct(Long id, Long organizationId) {
        Product product = productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(Long organizationId, String query) {
        int defaultThreshold = getDefaultThreshold(organizationId);
        return productRepository.searchByNameOrSku(organizationId, query)
                .stream()
                .map(p -> toProductResponse(p, defaultThreshold))
                .collect(Collectors.toList());
    }

    private int getDefaultThreshold(Long organizationId) {
        return settingsRepository.findByOrganizationId(organizationId)
                .map(OrganizationSettings::getDefaultLowStockThreshold)
                .orElse(10);
    }

    private ProductResponse toProductResponse(Product product, int defaultThreshold) {
        int effectiveThreshold = product.getLowStockThreshold() != null
                ? product.getLowStockThreshold()
                : defaultThreshold;
        boolean lowStock = product.getQuantity() <= effectiveThreshold;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getDescription(),
                product.getQuantity(),
                product.getCostPrice(),
                product.getSellingPrice(),
                product.getLowStockThreshold(),
                lowStock,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
