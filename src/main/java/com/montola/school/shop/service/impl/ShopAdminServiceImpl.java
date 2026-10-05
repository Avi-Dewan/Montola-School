package com.montola.school.shop.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.storage.FileStorageService;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.repository.LevelRepository;
import com.montola.school.course.repository.SubjectRepository;
import com.montola.school.shop.dto.ShopBundleDto;
import com.montola.school.shop.dto.ShopBundleRequestDto;
import com.montola.school.shop.dto.ShopProductCardDto;
import com.montola.school.shop.dto.ShopProductRequestDto;
import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.model.ShopBundle;
import com.montola.school.shop.model.ShopProduct;
import com.montola.school.shop.repository.ShopBundleRepository;
import com.montola.school.shop.repository.ShopProductRepository;
import com.montola.school.shop.service.ShopAdminService;
import com.montola.school.shop.service.ShopDtoAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author avidewan
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopAdminServiceImpl implements ShopAdminService {

    private final ShopProductRepository productRepository;
    private final ShopBundleRepository bundleRepository;
    private final LevelRepository levelRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;
    private final FileStorageService fileStorageService;
    private final ShopDtoAssembler assembler;

    // ---------------------------------------------------------------- products

    @Override
    @Transactional(readOnly = true)
    public List<ShopProductCardDto> getAllProducts() {
        return productRepository.findByIsDeletedFalseOrderByIdAsc().stream()
                .map(assembler::toCard)
                .toList();
    }

    @Override
    @Transactional
    public ShopProductCardDto createProduct(ShopProductRequestDto request) {
        ShopProduct product = new ShopProduct();
        product.setStatus(ShopItemStatus.DRAFT);
        applyProduct(product, request);

        return assembler.toCard(productRepository.save(product));
    }

    @Override
    @Transactional
    public ShopProductCardDto updateProduct(Long productId, ShopProductRequestDto request) {
        ShopProduct product = findProduct(productId);
        applyProduct(product, request);

        return assembler.toCard(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        ShopProduct product = findProduct(productId);

        // Soft delete: payments and entitlements may reference this product, and
        // purchase history must stay intact.
        product.setDeleted(true);
        productRepository.save(product);

        log.info("Soft-deleted shop product {}", productId);
    }

    @Override
    @Transactional
    public ShopProductCardDto uploadProductFile(Long productId, MultipartFile file) {
        ShopProduct product = findProduct(productId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A file is required.");
        }

        try {
            String key = fileStorageService.store(
                    file.getBytes(), file.getOriginalFilename(), file.getContentType());

            product.setFileKey(key);
            product.setStorageProvider(fileStorageService.provider());

        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the uploaded file.");
        }

        return assembler.toCard(productRepository.save(product));
    }

    // ----------------------------------------------------------------- bundles

    @Override
    @Transactional(readOnly = true)
    public List<ShopBundleDto> getAllBundles() {
        return bundleRepository.findByIsDeletedFalseOrderByIdAsc().stream()
                .map(assembler::toBundleDto)
                .toList();
    }

    @Override
    @Transactional
    public ShopBundleDto createBundle(ShopBundleRequestDto request) {
        ShopBundle bundle = new ShopBundle();
        bundle.setStatus(ShopItemStatus.DRAFT);
        applyBundle(bundle, request);

        return assembler.toBundleDto(bundleRepository.save(bundle));
    }

    @Override
    @Transactional
    public ShopBundleDto updateBundle(Long bundleId, ShopBundleRequestDto request) {
        ShopBundle bundle = findBundle(bundleId);
        applyBundle(bundle, request);

        return assembler.toBundleDto(bundleRepository.save(bundle));
    }

    @Override
    @Transactional
    public void deleteBundle(Long bundleId) {
        ShopBundle bundle = findBundle(bundleId);
        bundle.setDeleted(true);
        bundleRepository.save(bundle);

        log.info("Soft-deleted shop bundle {}", bundleId);
    }

    // ------------------------------------------------------------------ shared

    private void applyProduct(ShopProduct product, ShopProductRequestDto request) {
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setType(request.getType());

        if (request.getFormat() != null) {
            product.setFormat(request.getFormat());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }
        product.setPreview(request.getPreview());

        product.setLevel(request.getLevelId() != null
                ? levelRepository.findById(request.getLevelId())
                        .orElseThrow(() -> new ResourceNotFoundException("course.level.notfound"))
                : null);
        product.setClassEntity(request.getClassId() != null
                ? classRepository.findById(request.getClassId())
                        .orElseThrow(() -> new ResourceNotFoundException("class.notfound"))
                : null);
        product.setSubject(request.getSubjectId() != null
                ? subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("subject.notfound"))
                : null);
        product.setChapter(request.getChapterId() != null
                ? chapterRepository.findById(request.getChapterId())
                        .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"))
                : null);

        // Content is only touched when the caller actually supplied some, so
        // editing metadata never wipes a product's stored content.
        ShopProductRequestDto.ContentDto content = request.getContent();
        if (content != null) {
            if (content.getHtml() != null) {
                product.setContentHtml(content.getHtml());
            }
            if (content.getFileId() != null) {
                product.setFileKey(content.getFileId());
                product.setStorageProvider(fileStorageService.provider());
            }
            if (content.getPageCount() != null) {
                product.setPageCount(content.getPageCount());
            }
        }
    }

    private void applyBundle(ShopBundle bundle, ShopBundleRequestDto request) {
        bundle.setTitle(request.getTitle());
        bundle.setDescription(request.getDescription());

        if (request.getAudience() != null) {
            bundle.setAudience(request.getAudience());
        }
        if (request.getAccessMode() != null) {
            bundle.setAccessMode(request.getAccessMode());
        }
        if (request.getPrice() != null) {
            bundle.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            bundle.setStatus(request.getStatus());
        }

        bundle.setLevel(request.getLevelId() != null
                ? levelRepository.findById(request.getLevelId())
                        .orElseThrow(() -> new ResourceNotFoundException("course.level.notfound"))
                : null);
        bundle.setSubject(request.getSubjectId() != null
                ? subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("subject.notfound"))
                : null);

        if (request.getProductIds() != null) {
            List<ShopProduct> products = new ArrayList<>(
                    productRepository.findAllById(request.getProductIds()));
            bundle.setProducts(products);
        }
    }

    private ShopProduct findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.product.notfound"));
    }

    private ShopBundle findBundle(Long bundleId) {
        return bundleRepository.findById(bundleId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.bundle.notfound"));
    }
}
