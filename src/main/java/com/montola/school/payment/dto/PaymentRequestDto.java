package com.montola.school.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for submitting a payment.
 *
 * @author avidewan
 * @date 12/14/2025
 */
@Getter
@Setter
public class PaymentRequestDto {

    private Long chapterId;
    private String transactionId;
    private String senderNumber;
    private Double amount;
    private String paymentMethod;
}
