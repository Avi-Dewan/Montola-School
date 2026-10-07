package com.montola.school.shop.dto;

import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;

/**
 * Read-only projection of the fields a product card needs.
 * <p>
 * It deliberately omits the large columns — {@code contentHtml} and {@code fileKey}.
 * Both load eagerly with the entity, so listing products through the entity dragged
 * every product's full content out of the database only to discard it: the card DTO
 * does not carry either field. Lists use this projection; a request that genuinely
 * needs the content loads the entity by id.
 * </p>
 *
 * @author avidewan
 */
public record ShopProductCardView(
        Long id,
        String title,
        String description,
        ShopProductType type,
        ShopProductFormat format,
        Double price,
        ShopItemStatus status,
        boolean featured,
        String preview,

        Long levelId,
        String levelName,

        Long classId,
        String className,

        Long subjectId,
        String subjectName,

        Long chapterId,
        String chapterTitle
) {
}
