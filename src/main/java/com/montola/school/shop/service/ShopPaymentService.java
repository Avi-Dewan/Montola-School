package com.montola.school.shop.service;

import com.montola.school.shop.dto.ShopPaymentDto;
import com.montola.school.shop.dto.ShopPaymentRequestDto;

import java.util.List;

/**
 * Shop payment submission and admin verification.
 *
 * @author avidewan
 */
public interface ShopPaymentService {

    ShopPaymentDto submitPayment(Long userId, ShopPaymentRequestDto request);

    List<ShopPaymentDto> getMyPayments(Long userId);

    List<ShopPaymentDto> getAllPayments();

    List<ShopPaymentDto> getUnverifiedPayments();

    /**
     * Marks the payment VERIFIED and grants the corresponding entitlement
     * (idempotent: an already-verified payment is returned unchanged).
     */
    ShopPaymentDto verifyPayment(Long paymentId, Long adminUserId);

    ShopPaymentDto rejectPayment(Long paymentId, Long adminUserId);
}
