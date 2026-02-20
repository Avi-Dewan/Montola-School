package com.montola.school.payment.service;

import com.montola.school.payment.dto.PaymentRequestDto;
import com.montola.school.payment.dto.PaymentResponseDto;

import java.util.List;

/**
 * Service for handling payment submissions and verification.
 *
 * @author avidewan
 * @date 12/14/2025
 */
public interface PaymentService {

    /**
     * Submits a new payment for verification.
     */
    PaymentResponseDto submitPayment(Long userId, PaymentRequestDto request);

    /**
     * Verifies a payment and enrolls the user in the chapter.
     */
    PaymentResponseDto verifyPayment(Long paymentId, Long adminUserId);

    /**
     * Retrieves all payments (Admin view).
     */
    List<PaymentResponseDto> getAllPayments();

    /**
     * Retrieves all unverified (PENDING) payments (Admin view).
     */
    List<PaymentResponseDto> getUnverifiedPayments();

    /**
     * Retrieves payments for a specific user (Student view).
     */
    List<PaymentResponseDto> getMyPayments(Long userId);

    /**
     * Retrieves payment status for a specific user and chapter.
     */
    PaymentResponseDto getMyPaymentForChapter(Long userId, Long chapterId);
}
