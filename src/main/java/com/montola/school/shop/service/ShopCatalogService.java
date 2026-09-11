package com.montola.school.shop.service;

import com.montola.school.shop.dto.ShopBundleDto;
import com.montola.school.shop.dto.ShopClassDto;
import com.montola.school.shop.dto.ShopLevelDto;
import com.montola.school.shop.dto.ShopProductCardDto;
import com.montola.school.shop.dto.ShopProductDetailDto;
import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;

import java.util.List;

/**
 * Public shop catalog.
 *
 * @author avidewan
 */
public interface ShopCatalogService {

    List<ShopLevelDto> getLevels();

    /**
     * Classes available for the class tier. Only levels that split into classes
     * (JSC) return rows; SSC/HSC return an empty list and are browsed as whole levels.
     */
    List<ShopClassDto> getClasses(Long levelId);

    List<ShopProductCardDto> getProducts(ShopProductType type,
                                         ShopProductFormat format,
                                         Long levelId,
                                         Long classId,
                                         Long subjectId,
                                         Long chapterId);

    List<ShopProductCardDto> getFeaturedProducts();

    /**
     * @param currentUserId the viewer, or {@code null} for an anonymous visitor
     */
    ShopProductDetailDto getProductDetail(Long productId, Long currentUserId);

    List<ShopBundleDto> getBundles();

    ShopBundleDto getBundle(Long bundleId);
}
