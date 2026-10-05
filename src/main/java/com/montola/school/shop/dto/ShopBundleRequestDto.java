package com.montola.school.shop.dto;

import com.montola.school.shop.enums.BundleAccessMode;
import com.montola.school.shop.enums.BundleAudience;
import com.montola.school.shop.enums.ShopItemStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Create/update payload for a shop bundle.
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopBundleRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private BundleAudience audience;

    private BundleAccessMode accessMode;

    private Long levelId;

    private Long subjectId;

    private Double price;

    private ShopItemStatus status;

    @Builder.Default
    private List<Long> productIds = new ArrayList<>();
}
