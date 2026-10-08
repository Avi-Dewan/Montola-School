package com.montola.school.shop.service;

import com.montola.school.course.model.ClassEntity;
import com.montola.school.course.model.Level;
import com.montola.school.shop.dto.ShopAdminProductDto;
import com.montola.school.shop.dto.ShopBundleDto;
import com.montola.school.shop.dto.ShopClassDto;
import com.montola.school.shop.dto.ShopLevelDto;
import com.montola.school.shop.dto.ShopPaymentDto;
import com.montola.school.shop.dto.ShopProductCardDto;
import com.montola.school.shop.dto.ShopProductCardView;
import com.montola.school.shop.enums.ShopProductType;
import com.montola.school.shop.model.ShopBundle;
import com.montola.school.shop.model.ShopPayment;
import com.montola.school.shop.model.ShopProduct;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Builds shop DTOs from entities.
 * <p>
 * A hand-written assembler (rather than MapStruct) because most fields are derived
 * from lazy associations and the boolean flags are computed rather than mapped.
 * </p>
 *
 * @author avidewan
 */
@Component
public class ShopDtoAssembler {

    public ShopLevelDto toLevelDto(Level level) {
        return ShopLevelDto.builder()
                .id(level.getId())
                .name(level.getName())
                .orderIndex(level.getOrderIndex())
                .build();
    }

    public ShopClassDto toClassDto(ClassEntity classEntity, int orderIndex) {
        return ShopClassDto.builder()
                .id(classEntity.getId())
                .name(classEntity.getName())
                .levelId(classEntity.getLevel() != null ? classEntity.getLevel().getId() : null)
                .orderIndex(orderIndex)
                .build();
    }

    /**
     * Fills the shared "card" fields on any DTO that extends {@link ShopProductCardDto}.
     * Accepting the base type lets the same code populate cards, detail and purchase DTOs.
     */
    public void fillCard(ShopProductCardDto dto, ShopProduct product) {
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setType(product.getType());
        dto.setFormat(product.getFormat());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus() != null ? product.getStatus().name() : null);
        dto.setFeatured(product.isFeatured());
        dto.setPreview(product.getPreview());
        dto.setDownloadable(ShopProductType.isDownloadable(product.getType()));
        dto.setFileAttached(product.getFileKey() != null && !product.getFileKey().isBlank());

        dto.setLevelId(product.getLevel() != null ? product.getLevel().getId() : null);
        dto.setLevelName(product.getLevel() != null ? product.getLevel().getName() : null);

        dto.setClassId(product.getClassEntity() != null ? product.getClassEntity().getId() : null);
        dto.setClassName(product.getClassEntity() != null ? product.getClassEntity().getName() : null);

        dto.setSubjectId(product.getSubject() != null ? product.getSubject().getId() : null);
        dto.setSubjectName(product.getSubject() != null ? product.getSubject().getName() : null);

        dto.setChapterId(product.getChapter() != null ? product.getChapter().getId() : null);
        dto.setChapterTitle(product.getChapter() != null ? product.getChapter().getTitle() : null);
    }

    public ShopProductCardDto toCard(ShopProduct product) {
        ShopProductCardDto dto = new ShopProductCardDto();
        fillCard(dto, product);

        return dto;
    }

    /**
     * Card fields from the list projection. Mirrors the entity overload — the
     * projection simply never carries the large content columns.
     */
    public void fillCard(ShopProductCardDto dto, ShopProductCardView view) {
        dto.setId(view.id());
        dto.setTitle(view.title());
        dto.setDescription(view.description());
        dto.setType(view.type());
        dto.setFormat(view.format());
        dto.setPrice(view.price());
        dto.setStatus(view.status() != null ? view.status().name() : null);
        dto.setFeatured(view.featured());
        dto.setPreview(view.preview());
        dto.setDownloadable(ShopProductType.isDownloadable(view.type()));
        dto.setFileAttached(view.fileKey() != null && !view.fileKey().isBlank());

        dto.setLevelId(view.levelId());
        dto.setLevelName(view.levelName());
        dto.setClassId(view.classId());
        dto.setClassName(view.className());
        dto.setSubjectId(view.subjectId());
        dto.setSubjectName(view.subjectName());
        dto.setChapterId(view.chapterId());
        dto.setChapterTitle(view.chapterTitle());
    }

    public ShopProductCardDto toCard(ShopProductCardView view) {
        ShopProductCardDto dto = new ShopProductCardDto();
        fillCard(dto, view);

        return dto;
    }

    /**
     * Admin card: the plain card plus the file reference, for display in the edit
     * form. Never returned by a public endpoint.
     */
    public ShopAdminProductDto toAdminCard(ShopProductCardView view) {
        ShopAdminProductDto dto = new ShopAdminProductDto();
        fillCard(dto, view);
        dto.setFileId(view.fileKey());

        return dto;
    }

    public void fillBundle(ShopBundleDto dto, ShopBundle bundle) {
        dto.setId(bundle.getId());
        dto.setTitle(bundle.getTitle());
        dto.setDescription(bundle.getDescription());
        dto.setAudience(bundle.getAudience());
        dto.setAccessMode(bundle.getAccessMode());
        dto.setPrice(bundle.getPrice());
        dto.setStatus(bundle.getStatus() != null ? bundle.getStatus().name() : null);

        dto.setLevelId(bundle.getLevel() != null ? bundle.getLevel().getId() : null);
        dto.setLevelName(bundle.getLevel() != null ? bundle.getLevel().getName() : null);

        dto.setSubjectId(bundle.getSubject() != null ? bundle.getSubject().getId() : null);
        dto.setSubjectName(bundle.getSubject() != null ? bundle.getSubject().getName() : null);

        var products = bundle.getProducts().stream()
                .filter(product -> !product.isDeleted())
                .map(this::toCard)
                .collect(Collectors.toList());
        dto.setProducts(products);
        dto.setProductCount(products.size());
    }

    public ShopBundleDto toBundleDto(ShopBundle bundle) {
        ShopBundleDto dto = new ShopBundleDto();
        fillBundle(dto, bundle);

        return dto;
    }

    public ShopPaymentDto toPaymentDto(ShopPayment payment, boolean includeUser) {
        String productTitle = payment.getProduct() != null ? payment.getProduct().getTitle() : null;
        String bundleTitle = payment.getBundle() != null ? payment.getBundle().getTitle() : null;

        return ShopPaymentDto.builder()
                .id(payment.getId())
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
                .userName(includeUser && payment.getUser() != null ? payment.getUser().getFullName() : null)
                .productId(payment.getProduct() != null ? payment.getProduct().getId() : null)
                .productTitle(productTitle)
                .bundleId(payment.getBundle() != null ? payment.getBundle().getId() : null)
                .bundleTitle(bundleTitle)
                .itemTitle(productTitle != null ? productTitle : bundleTitle)
                .amount(payment.getAmount())
                .senderNumber(payment.getSenderNumber())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .verifiedAt(payment.getVerifiedAt())
                .build();
    }
}
