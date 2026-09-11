package com.montola.school.shop.dto;

import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create/update payload for a shop product.
 * <p>
 * {@code content} is only applied when present, so editing a product's metadata
 * never wipes its stored content.
 * </p>
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Type is required")
    private ShopProductType type;

    private ShopProductFormat format;

    private Double price;

    private Long levelId;

    private Long classId;

    private Long subjectId;

    private Long chapterId;

    private ShopItemStatus status;

    private Boolean featured;

    private String preview;

    private ContentDto content;

    /**
     * Interactive products carry {@code html}; PDF products carry {@code fileId} (+ pageCount).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentDto {
        private String html;
        private String fileId;
        private Integer pageCount;
    }
}
