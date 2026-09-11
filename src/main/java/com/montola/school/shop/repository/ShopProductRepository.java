package com.montola.school.shop.repository;

import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.model.ShopProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 */
@Repository
public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {

    /**
     * Catalog query. Filtering by type/format/scope is done in the service: the
     * shop catalog is small and in-memory filtering keeps the query free of the
     * nullable-enum casting pitfalls that optional JPQL filters run into.
     * Soft-deleted rows are always excluded.
     */
    List<ShopProduct> findByIsDeletedFalseAndStatusOrderByIdAsc(ShopItemStatus status);

    List<ShopProduct> findByIsDeletedFalseAndFeaturedTrueAndStatusOrderByIdAsc(ShopItemStatus status);

    List<ShopProduct> findByIsDeletedFalseOrderByIdAsc();
}
