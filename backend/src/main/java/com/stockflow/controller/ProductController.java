package com.stockflow.controller;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.security.CustomUserDetails;
import com.stockflow.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(value = "search", required = false) String search,
            HttpServletRequest request) {
        Long orgId = getOrganizationId(request);
        List<ProductResponse> products;
        if (search != null && !search.isBlank()) {
            products = productService.searchProducts(orgId, search);
        } else {
            products = productService.getAllProducts(orgId);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id,
                                                          HttpServletRequest request) {
        Long orgId = getOrganizationId(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(productService.getProductById(id, orgId));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest,
                                                         HttpServletRequest request) {
        Long orgId = getOrganizationId(request);
        return new ResponseEntity<>(productService.createProduct(productRequest, orgId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest productRequest,
                                                         HttpServletRequest request) {
        Long orgId = getOrganizationId(request);
        return ResponseEntity.ok(productService.updateProduct(id, productRequest, orgId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        Long orgId = getOrganizationId(request);
        productService.deleteProduct(id, orgId);
        return ResponseEntity.noContent().build();
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
