package com.montola.school.shop.service;

import com.montola.school.shop.dto.ShopDownloadDto;
import com.montola.school.shop.dto.ShopProductContentDto;

import java.util.List;

/**
 * Access rules for shop content: who may view and who may download.
 *
 * @author avidewan
 */
public interface ShopAccessService {

    /**
     * True when the user owns the product directly, owns a bundle containing it,
     * or is staff (ADMIN/MANAGER/TEACHER preview). Anonymous users are never entitled.
     */
    boolean isEntitled(Long userId, Long productId);

    /**
     * Downloads are only for downloadable product types, owned through a
     * DOWNLOAD-mode bundle, by a teacher (or admin/manager).
     */
    boolean canDownload(Long userId, Long productId);

    ShopProductContentDto getContent(Long userId, Long productId);

    ShopDownloadDto getDownload(Long userId, Long productId);

    /**
     * Entitlements as purchases. Elements are either a product purchase or a bundle
     * purchase, matching the shape the frontend consumes.
     */
    List<Object> getMyPurchases(Long userId);
}
