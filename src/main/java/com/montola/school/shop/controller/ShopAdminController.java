package com.montola.school.shop.controller;

import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.shop.dto.ShopBundleDto;
import com.montola.school.shop.dto.ShopBundleRequestDto;
import com.montola.school.shop.dto.ShopPaymentDto;
import com.montola.school.shop.dto.ShopProductCardDto;
import com.montola.school.shop.dto.ShopProductRequestDto;
import com.montola.school.shop.service.ShopAdminService;
import com.montola.school.shop.service.ShopPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin/manager management of shop payments, products and bundles.
 *
 * @author avidewan
 */
@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Shop Admin", description = "Shop payment verification and catalog management")
@Slf4j
public class ShopAdminController {

    private final ShopPaymentService paymentService;
    private final ShopAdminService adminService;

    // ---------------------------------------------------------------- payments

    @Operation(summary = "All shop payments")
    @GetMapping("/payments")
    public ResponseEntity<List<ShopPaymentDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @Operation(summary = "Shop payments awaiting verification")
    @GetMapping("/payments/unverified")
    public ResponseEntity<List<ShopPaymentDto>> getUnverifiedPayments() {
        return ResponseEntity.ok(paymentService.getUnverifiedPayments());
    }

    @Operation(summary = "Verify a shop payment (grants the entitlement)")
    @PutMapping("/payments/{id}/verify")
    public ResponseEntity<ShopPaymentDto> verifyPayment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.verifyPayment(id, currentUser.getId()));
    }

    @Operation(summary = "Reject a shop payment")
    @PutMapping("/payments/{id}/reject")
    public ResponseEntity<ShopPaymentDto> rejectPayment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.rejectPayment(id, currentUser.getId()));
    }

    // ---------------------------------------------------------------- products

    @Operation(summary = "All products, including drafts")
    @GetMapping("/admin/products")
    public ResponseEntity<List<ShopProductCardDto>> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    @Operation(summary = "Create a product")
    @PostMapping("/admin/products")
    public ResponseEntity<ShopProductCardDto> createProduct(@Valid @RequestBody ShopProductRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createProduct(request));
    }

    @Operation(summary = "Update a product")
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ShopProductCardDto> updateProduct(@PathVariable Long id,
                                                            @Valid @RequestBody ShopProductRequestDto request) {
        return ResponseEntity.ok(adminService.updateProduct(id, request));
    }

    @Operation(summary = "Delete a product (soft delete)")
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload the PDF for a product")
    @PostMapping("/admin/products/{id}/file")
    public ResponseEntity<ShopProductCardDto> uploadProductFile(@PathVariable Long id,
                                                                @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(adminService.uploadProductFile(id, file));
    }

    // ----------------------------------------------------------------- bundles

    @Operation(summary = "All bundles, including drafts")
    @GetMapping("/admin/bundles")
    public ResponseEntity<List<ShopBundleDto>> getAllBundles() {
        return ResponseEntity.ok(adminService.getAllBundles());
    }

    @Operation(summary = "Create a bundle")
    @PostMapping("/admin/bundles")
    public ResponseEntity<ShopBundleDto> createBundle(@Valid @RequestBody ShopBundleRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createBundle(request));
    }

    @Operation(summary = "Update a bundle")
    @PutMapping("/admin/bundles/{id}")
    public ResponseEntity<ShopBundleDto> updateBundle(@PathVariable Long id,
                                                      @Valid @RequestBody ShopBundleRequestDto request) {
        return ResponseEntity.ok(adminService.updateBundle(id, request));
    }

    @Operation(summary = "Delete a bundle (soft delete)")
    @DeleteMapping("/admin/bundles/{id}")
    public ResponseEntity<Void> deleteBundle(@PathVariable Long id) {
        adminService.deleteBundle(id);

        return ResponseEntity.noContent().build();
    }
}
