package com.montola.school.shop.controller;

import com.montola.school.common.security.SecurityUtils;
import com.montola.school.shop.dto.ShopBundleDto;
import com.montola.school.shop.dto.ShopClassDto;
import com.montola.school.shop.dto.ShopLevelDto;
import com.montola.school.shop.dto.ShopProductCardDto;
import com.montola.school.shop.dto.ShopProductDetailDto;
import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;
import com.montola.school.shop.service.ShopCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public shop catalog. Every endpoint here is whitelisted; the product detail
 * additionally reads an optional token so it can report the viewer's access.
 *
 * @author avidewan
 */
@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
@Tag(name = "Shop Catalog", description = "Public shop browsing endpoints")
@Slf4j
public class ShopCatalogController {

    private final ShopCatalogService catalogService;

    @Operation(summary = "List curriculum levels (JSC/SSC/HSC)")
    @GetMapping("/levels")
    public ResponseEntity<List<ShopLevelDto>> getLevels() {
        return ResponseEntity.ok(catalogService.getLevels());
    }

    @Operation(summary = "List shop classes (only levels that split into classes, i.e. JSC)")
    @GetMapping("/classes")
    public ResponseEntity<List<ShopClassDto>> getClasses(@RequestParam(required = false) Long levelId) {
        return ResponseEntity.ok(catalogService.getClasses(levelId));
    }

    @Operation(summary = "Browse published products with optional filters")
    @GetMapping("/products")
    public ResponseEntity<List<ShopProductCardDto>> getProducts(
            @RequestParam(required = false) ShopProductType type,
            @RequestParam(required = false) ShopProductFormat format,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long chapterId) {

        return ResponseEntity.ok(
                catalogService.getProducts(type, format, levelId, classId, subjectId, chapterId));
    }

    @Operation(summary = "Product detail (includes entitled/canDownload when a token is supplied)")
    @GetMapping("/products/{id}")
    public ResponseEntity<ShopProductDetailDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProductDetail(id, SecurityUtils.currentUserIdOrNull()));
    }

    @Operation(summary = "Featured published products")
    @GetMapping("/featured")
    public ResponseEntity<List<ShopProductCardDto>> getFeatured() {
        return ResponseEntity.ok(catalogService.getFeaturedProducts());
    }

    @Operation(summary = "List published bundles")
    @GetMapping("/bundles")
    public ResponseEntity<List<ShopBundleDto>> getBundles() {
        return ResponseEntity.ok(catalogService.getBundles());
    }

    @Operation(summary = "Bundle detail")
    @GetMapping("/bundles/{id}")
    public ResponseEntity<ShopBundleDto> getBundle(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getBundle(id));
    }
}
