package com.montola.school.payment.dto;

import com.montola.school.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for payment response.
 *
 * @author avidewan
 * @date 12/14/2025
 */
@Getter
@Setter
@Builder
public class PaymentResponseDto {
    private Long id;
    private Long userId;
    private String userName; // Optional, useful for admins
    private Long chapterId;
    private String chapterTitle;
    private String senderNumber;
    private String transactionId;
    private Double amount;
    private String paymentMethod;
    private PaymentStatus status;
    private LocalDateTime verifiedAt;
    
    // Admin only fields (can be null for students)
    private Long verifiedByUserId;
    private String verifiedByName;
}
