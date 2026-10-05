package com.montola.school.shop.dto;

import com.montola.school.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopPaymentDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productTitle;
    private Long bundleId;
    private String bundleTitle;
    private String itemTitle;
    private Double amount;
    private String senderNumber;
    private String transactionId;
    private String paymentMethod;
    private PaymentStatus status;
    private LocalDateTime verifiedAt;
}
