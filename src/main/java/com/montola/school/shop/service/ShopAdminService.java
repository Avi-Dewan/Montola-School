package com.montola.school.shop.service;

import com.montola.school.shop.dto.ShopBundleDto;
import com.montola.school.shop.dto.ShopBundleRequestDto;
import com.montola.school.shop.dto.ShopProductCardDto;
import com.montola.school.shop.dto.ShopProductRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin management of shop products and bundles.
 *
 * @author avidewan
 */
public interface ShopAdminService {

    List<ShopProductCardDto> getAllProducts();

    ShopProductCardDto createProduct(ShopProductRequestDto request);

    ShopProductCardDto updateProduct(Long productId, ShopProductRequestDto request);

    void deleteProduct(Long productId);

    List<ShopBundleDto> getAllBundles();

    ShopBundleDto createBundle(ShopBundleRequestDto request);

    ShopBundleDto updateBundle(Long bundleId, ShopBundleRequestDto request);

    void deleteBundle(Long bundleId);

    /**
     * Stores an uploaded PDF for a product and points the product at it.
     */
    ShopProductCardDto uploadProductFile(Long productId, MultipartFile file);
}
