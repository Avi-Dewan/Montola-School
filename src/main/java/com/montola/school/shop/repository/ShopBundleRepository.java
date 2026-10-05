package com.montola.school.shop.repository;

import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.model.ShopBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 */
@Repository
public interface ShopBundleRepository extends JpaRepository<ShopBundle, Long> {

    List<ShopBundle> findByIsDeletedFalseAndStatusOrderByIdAsc(ShopItemStatus status);

    List<ShopBundle> findByIsDeletedFalseOrderByIdAsc();
}
