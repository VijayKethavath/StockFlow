package com.stockflow.controller;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Long orgId = (Long) request.getAttribute("organizationId");
        List<ProductResponse> products;
        if (search != null && !search.isBlank()) {
            products = productService.searchProducts(orgId, search);
        } else {
            products = productService.getAllProducts(orgId);
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id,
                                                          HttpServletRequest request) {
        Long orgId = (Long) request.getAttribute("organizationId");
        return ResponseEntity.ok(productService.getProductById(id, orgId));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest,
                                                         HttpServletRequest request) {
        Long orgId = (Long) request.getAttribute("organizationId");
        return new ResponseEntity<>(productService.createProduct(productRequest, orgId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest productRequest,
                                                         HttpServletRequest request) {
        Long orgId = (Long) request.getAttribute("organizationId");
        return ResponseEntity.ok(productService.updateProduct(id, productRequest, orgId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        Long orgId = (Long) request.getAttribute("organizationId");
        productService.deleteProduct(id, orgId);
        return ResponseEntity.noContent().build();
    }
}
