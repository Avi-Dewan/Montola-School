package com.montola.school.shop.repository;

import com.montola.school.payment.enums.PaymentStatus;
import com.montola.school.shop.model.ShopPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 */
@Repository
public interface ShopPaymentRepository extends JpaRepository<ShopPayment, Long> {

    List<ShopPayment> findByUserIdOrderByIdDesc(Long userId);

    List<ShopPayment> findByStatusOrderByIdDesc(PaymentStatus status);

    List<ShopPayment> findAllByOrderByIdDesc();
}
