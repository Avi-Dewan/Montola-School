package com.montola.school.shop.controller;

import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.shop.dto.ShopDownloadDto;
import com.montola.school.shop.dto.ShopPaymentDto;
import com.montola.school.shop.dto.ShopPaymentRequestDto;
import com.montola.school.shop.dto.ShopProductContentDto;
import com.montola.school.shop.service.ShopAccessService;
import com.montola.school.shop.service.ShopPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gated shop content, purchases and shop payments for the signed-in user.
 *
 * @author avidewan
 */
@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
@Tag(name = "Shop Purchases", description = "Content access, purchases and payments")
@Slf4j
public class ShopPurchaseController {

    private final ShopAccessService accessService;
    private final ShopPaymentService paymentService;

    @Operation(summary = "Full content for an entitled product")
    @GetMapping("/products/{id}/content")
    public ResponseEntity<ShopProductContentDto> getContent(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                            @PathVariable Long id) {
        return ResponseEntity.ok(accessService.getContent(currentUser.getId(), id));
    }

    @Operation(summary = "Time-limited download link for a downloadable product")
    @GetMapping("/products/{id}/download")
    public ResponseEntity<ShopDownloadDto> getDownload(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                       @PathVariable Long id) {
        return ResponseEntity.ok(accessService.getDownload(currentUser.getId(), id));
    }

    @Operation(summary = "Everything the signed-in user owns")
    @GetMapping("/my-purchases")
    public ResponseEntity<List<Object>> getMyPurchases(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(accessService.getMyPurchases(currentUser.getId()));
    }

    @Operation(summary = "Submit a shop payment")
    @PostMapping("/payments/submit")
    public ResponseEntity<ShopPaymentDto> submitPayment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                        @Valid @RequestBody ShopPaymentRequestDto request) {
        log.info("Shop payment submission from user {}", currentUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.submitPayment(currentUser.getId(), request));
    }

    @Operation(summary = "The signed-in user's shop payments")
    @GetMapping("/payments/my")
    public ResponseEntity<List<ShopPaymentDto>> getMyPayments(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(paymentService.getMyPayments(currentUser.getId()));
    }
}
