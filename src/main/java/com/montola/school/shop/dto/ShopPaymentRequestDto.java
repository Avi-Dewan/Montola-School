package com.montola.school.shop.dto;

import com.montola.school.payment.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shop payment submission. Exactly one of productId / bundleId is expected;
 * that is validated in the service because bean validation cannot express it cleanly.
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopPaymentRequestDto {

    private Long productId;

    private Long bundleId;

    private Double amount;

    @NotBlank(message = "Sender number is required")
    @Pattern(regexp = "^(01)[3-9][0-9]{8}$", message = "Enter a valid mobile number (e.g. 01712345678)")
    private String senderNumber;

    @NotBlank(message = "Transaction id is required")
    private String transactionId;

    private String paymentMethod;
}
