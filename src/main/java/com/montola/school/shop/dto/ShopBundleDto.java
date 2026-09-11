package com.montola.school.shop.dto;

import com.montola.school.shop.enums.BundleAccessMode;
import com.montola.school.shop.enums.BundleAudience;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopBundleDto {
    private Long id;
    private String title;
    private String description;
    private BundleAudience audience;
    private BundleAccessMode accessMode;
    private Double price;
    private String status;
    private Long levelId;
    private String levelName;
    private Long subjectId;
    private String subjectName;
    private int productCount;
    @Builder.Default
    private List<ShopProductCardDto> products = new ArrayList<>();
}
