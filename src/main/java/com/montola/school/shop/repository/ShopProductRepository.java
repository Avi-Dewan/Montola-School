package com.montola.school.shop.repository;

import com.montola.school.shop.dto.ShopProductCardView;
import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.model.ShopProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 */
@Repository
public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {

    /*
     * Card lists use the ShopProductCardView projection rather than the entity so the
     * large content_html and file_key columns are never read. Reading them for a list
     * pulled every product's full markup out of the database only to throw it away.
     *
     * Filtering by type/format/scope stays in the service: the catalog is small and
     * in-memory filtering keeps the query free of the nullable-enum casting pitfalls
     * that optional JPQL filters run into. Soft-deleted rows are always excluded.
     */

    @Query("""
            select new com.montola.school.shop.dto.ShopProductCardView(
                p.id, p.title, p.description, p.type, p.format, p.price, p.status,
                p.featured, p.preview,
                l.id, l.name, c.id, c.name, s.id, s.name, ch.id, ch.title)
            from ShopProduct p
            left join p.level l
            left join p.classEntity c
            left join p.subject s
            left join p.chapter ch
            where p.isDeleted = false and p.status = :status
            order by p.id asc
            """)
    List<ShopProductCardView> findCardsByStatus(@Param("status") ShopItemStatus status);

    @Query("""
            select new com.montola.school.shop.dto.ShopProductCardView(
                p.id, p.title, p.description, p.type, p.format, p.price, p.status,
                p.featured, p.preview,
                l.id, l.name, c.id, c.name, s.id, s.name, ch.id, ch.title)
            from ShopProduct p
            left join p.level l
            left join p.classEntity c
            left join p.subject s
            left join p.chapter ch
            where p.isDeleted = false and p.status = :status and p.featured = true
            order by p.id asc
            """)
    List<ShopProductCardView> findFeaturedCards(@Param("status") ShopItemStatus status);

    @Query("""
            select new com.montola.school.shop.dto.ShopProductCardView(
                p.id, p.title, p.description, p.type, p.format, p.price, p.status,
                p.featured, p.preview,
                l.id, l.name, c.id, c.name, s.id, s.name, ch.id, ch.title)
            from ShopProduct p
            left join p.level l
            left join p.classEntity c
            left join p.subject s
            left join p.chapter ch
            where p.isDeleted = false
            order by p.id asc
            """)
    List<ShopProductCardView> findAllCards();
}
