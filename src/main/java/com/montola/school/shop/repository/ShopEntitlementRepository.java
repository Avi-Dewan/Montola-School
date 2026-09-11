package com.montola.school.shop.repository;

import com.montola.school.shop.model.ShopEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 */
@Repository
public interface ShopEntitlementRepository extends JpaRepository<ShopEntitlement, Long> {

    List<ShopEntitlement> findByUserIdOrderByGrantedAtDesc(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndBundleId(Long userId, Long bundleId);

    List<ShopEntitlement> findByUserIdAndBundleIsNotNull(Long userId);
}
