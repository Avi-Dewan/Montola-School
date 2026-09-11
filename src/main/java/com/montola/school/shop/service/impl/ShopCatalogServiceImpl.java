package com.montola.school.shop.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.ClassEntity;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.repository.LevelRepository;
import com.montola.school.shop.dto.ShopBundleDto;
import com.montola.school.shop.dto.ShopClassDto;
import com.montola.school.shop.dto.ShopLevelDto;
import com.montola.school.shop.dto.ShopProductCardDto;
import com.montola.school.shop.dto.ShopProductDetailDto;
import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;
import com.montola.school.shop.model.ShopProduct;
import com.montola.school.shop.repository.ShopBundleRepository;
import com.montola.school.shop.repository.ShopProductRepository;
import com.montola.school.shop.service.ShopAccessService;
import com.montola.school.shop.service.ShopCatalogService;
import com.montola.school.shop.service.ShopDtoAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author avidewan
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShopCatalogServiceImpl implements ShopCatalogService {

    private final ShopProductRepository productRepository;
    private final ShopBundleRepository bundleRepository;
    private final LevelRepository levelRepository;
    private final ClassRepository classRepository;
    private final ShopDtoAssembler assembler;
    private final ShopAccessService accessService;

    @Override
    public List<ShopLevelDto> getLevels() {
        return levelRepository.findAllByOrderByOrderIndexAsc().stream()
                .map(assembler::toLevelDto)
                .toList();
    }

    @Override
    public List<ShopClassDto> getClasses(Long levelId) {
        List<ClassEntity> classes = levelId != null
                ? classRepository.findByLevel_IdAndLevel_SplitIntoClassesTrueOrderByIdAsc(levelId)
                : classRepository.findByLevel_SplitIntoClassesTrueOrderByIdAsc();

        return IntStream.range(0, classes.size())
                .mapToObj(i -> assembler.toClassDto(classes.get(i), i))
                .toList();
    }

    @Override
    public List<ShopProductCardDto> getProducts(ShopProductType type,
                                                ShopProductFormat format,
                                                Long levelId,
                                                Long classId,
                                                Long subjectId,
                                                Long chapterId) {

        return productRepository.findByIsDeletedFalseAndStatusOrderByIdAsc(ShopItemStatus.PUBLISHED).stream()
                .filter(p -> type == null || p.getType() == type)
                .filter(p -> format == null || p.getFormat() == format)
                .filter(p -> levelId == null || (p.getLevel() != null && levelId.equals(p.getLevel().getId())))
                .filter(p -> classId == null || (p.getClassEntity() != null && classId.equals(p.getClassEntity().getId())))
                .filter(p -> subjectId == null || (p.getSubject() != null && subjectId.equals(p.getSubject().getId())))
                .filter(p -> chapterId == null || (p.getChapter() != null && chapterId.equals(p.getChapter().getId())))
                .map(assembler::toCard)
                .toList();
    }

    @Override
    public List<ShopProductCardDto> getFeaturedProducts() {
        return productRepository.findByIsDeletedFalseAndFeaturedTrueAndStatusOrderByIdAsc(ShopItemStatus.PUBLISHED).stream()
                .map(assembler::toCard)
                .toList();
    }

    @Override
    public ShopProductDetailDto getProductDetail(Long productId, Long currentUserId) {
        ShopProduct product = findProduct(productId);

        ShopProductDetailDto detail = new ShopProductDetailDto();
        assembler.fillCard(detail, product);
        detail.setEntitled(accessService.isEntitled(currentUserId, productId));
        detail.setCanDownload(accessService.canDownload(currentUserId, productId));

        return detail;
    }

    @Override
    public List<ShopBundleDto> getBundles() {
        return bundleRepository.findByIsDeletedFalseAndStatusOrderByIdAsc(ShopItemStatus.PUBLISHED).stream()
                .map(assembler::toBundleDto)
                .toList();
    }

    @Override
    public ShopBundleDto getBundle(Long bundleId) {
        return bundleRepository.findById(bundleId)
                .map(assembler::toBundleDto)
                .orElseThrow(() -> new ResourceNotFoundException("shop.bundle.notfound"));
    }

    private ShopProduct findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.product.notfound"));
    }
}
