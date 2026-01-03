package com.montola.school.payment.repository;

import com.montola.school.payment.enums.PaymentStatus;
import com.montola.school.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Payment entities.
 *
 * @author avidewan
 * @date 12/14/2025
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByUserId(Long userId);
}
