package com.montola.school.payment.controller;

import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.payment.dto.PaymentRequestDto;
import com.montola.school.payment.dto.PaymentResponseDto;
import com.montola.school.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for payments.
 *
 * @author avidewan
 * @date 12/14/2025
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // --- Student Endpoints ---
    @PostMapping("/submit")
    public ResponseEntity<PaymentResponseDto> submitPayment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                            @RequestBody PaymentRequestDto request) {
        log.info("Request to submit payment by user {}", currentUser.getId());
        PaymentResponseDto payment = paymentService.submitPayment(currentUser.getId(), request);

        return ResponseEntity.ok(payment);
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(paymentService.getMyPayments(currentUser.getId()));
    }

    @GetMapping("/my-payments/chapter/{chapterId}")
    public ResponseEntity<PaymentResponseDto> getMyPaymentForChapter(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                     @PathVariable Long chapterId) {
        return ResponseEntity.ok(paymentService.getMyPaymentForChapter(currentUser.getId(), chapterId));
    }

    // --- Admin Endpoints ---
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<PaymentResponseDto>> getUnverifiedPayments() {
        return ResponseEntity.ok(paymentService.getUnverifiedPayments());
    }

    @PutMapping("/{paymentId}/verify")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<PaymentResponseDto> verifyPayment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                            @PathVariable Long paymentId) {
        log.info("Request to verify payment {} by admin {}", paymentId, currentUser.getId());
        PaymentResponseDto payment = paymentService.verifyPayment(paymentId, currentUser.getId());

        return ResponseEntity.ok(payment);
    }
}
