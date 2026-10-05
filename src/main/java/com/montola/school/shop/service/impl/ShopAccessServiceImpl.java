package com.montola.school.shop.service.impl;

import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.AccessDeniedCustomException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.storage.FileStorageService;
import com.montola.school.common.storage.StorageProperties;
import com.montola.school.shop.dto.ShopBundlePurchaseDto;
import com.montola.school.shop.dto.ShopDownloadDto;
import com.montola.school.shop.dto.ShopProductContentDto;
import com.montola.school.shop.dto.ShopProductPurchaseDto;
import com.montola.school.shop.enums.BundleAccessMode;
import com.montola.school.shop.enums.ShopProductType;
import com.montola.school.shop.model.ShopBundle;
import com.montola.school.shop.model.ShopEntitlement;
import com.montola.school.shop.model.ShopProduct;
import com.montola.school.shop.repository.ShopEntitlementRepository;
import com.montola.school.shop.repository.ShopProductRepository;
import com.montola.school.shop.service.ShopAccessService;
import com.montola.school.shop.service.ShopDtoAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author avidewan
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShopAccessServiceImpl implements ShopAccessService {

    private final ShopProductRepository productRepository;
    private final ShopEntitlementRepository entitlementRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final StorageProperties storageProperties;
    private final ShopDtoAssembler assembler;

    @Override
    public boolean isEntitled(Long userId, Long productId) {
        if (userId == null) {
            return false;
        }

        User user = findUser(userId);

        // Staff may preview everything.
        if (user.isAdminOrManagerOrTeacher()) {
            return true;
        }

        if (entitlementRepository.existsByUserIdAndProductId(userId, productId)) {
            return true;
        }

        return bundleEntitlements(userId).stream()
                .anyMatch(bundle -> bundleContainsProduct(bundle, productId));
    }

    @Override
    public boolean canDownload(Long userId, Long productId) {
        if (userId == null) {
            return false;
        }

        ShopProduct product = findProduct(productId);

        if (!ShopProductType.isDownloadable(product.getType())) {
            return false;
        }

        User user = findUser(userId);
        if (user.isAdminOrManager()) {
            return true;
        }

        // NOTE: the reference implementation also allows a "COACHING" account type;
        // this backend has no such account type, so TEACHER is the teaching account.
        if (!user.isTeacher()) {
            return false;
        }

        return bundleEntitlements(userId).stream()
                .anyMatch(bundle -> bundle.getAccessMode() == BundleAccessMode.DOWNLOAD
                        && bundleContainsProduct(bundle, productId));
    }

    @Override
    public ShopProductContentDto getContent(Long userId, Long productId) {
        ShopProduct product = findProduct(productId);

        if (!isEntitled(userId, productId)) {
            throw new AccessDeniedCustomException("shop.purchase.required");
        }

        User user = findUser(userId);

        return ShopProductContentDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .type(product.getType())
                .format(product.getFormat())
                .watermark("Licensed to " + user.getEmail() + " — Montola School")
                .html(product.getContentHtml())
                .fileId(product.getFileKey())
                .pageCount(product.getPageCount())
                .build();
    }

    @Override
    public ShopDownloadDto getDownload(Long userId, Long productId) {
        ShopProduct product = findProduct(productId);

        if (!canDownload(userId, productId)) {
            throw new AccessDeniedCustomException("shop.download.not.permitted");
        }

        User user = findUser(userId);

        long ttlSeconds = storageProperties.getS3().getUrlTtlSeconds();
        String fileKey = product.getFileKey() != null ? product.getFileKey() : "product-" + product.getId();
        String url = fileStorageService.url(product.getFileKey(), Duration.ofSeconds(ttlSeconds));

        return ShopDownloadDto.builder()
                .fileId(fileKey)
                .url(url != null ? url : fileKey)
                .expiresInSeconds(ttlSeconds)
                .watermark(user.getEmail() + " · " + user.getId())
                .build();
    }

    @Override
    public List<Object> getMyPurchases(Long userId) {
        List<Object> purchases = new ArrayList<>();

        for (ShopEntitlement entitlement : entitlementRepository.findByUserIdOrderByGrantedAtDesc(userId)) {
            if (entitlement.getProduct() != null) {
                ShopProductPurchaseDto dto = new ShopProductPurchaseDto();
                assembler.fillCard(dto, entitlement.getProduct());
                dto.setGrantedAt(entitlement.getGrantedAt());
                purchases.add(dto);

            } else if (entitlement.getBundle() != null) {
                ShopBundlePurchaseDto dto = new ShopBundlePurchaseDto();
                assembler.fillBundle(dto, entitlement.getBundle());
                dto.setGrantedAt(entitlement.getGrantedAt());
                purchases.add(dto);
            }
        }

        return purchases;
    }

    private List<ShopBundle> bundleEntitlements(Long userId) {
        return entitlementRepository.findByUserIdAndBundleIsNotNull(userId).stream()
                .map(ShopEntitlement::getBundle)
                .filter(bundle -> bundle != null)
                .toList();
    }

    private boolean bundleContainsProduct(ShopBundle bundle, Long productId) {
        return bundle.getProducts().stream()
                .anyMatch(product -> productId.equals(product.getId()));
    }

    private ShopProduct findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.product.notfound"));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
    }
}
