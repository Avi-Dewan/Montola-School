package com.montola.school.shop.dto;

import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product as shown in lists and cards.
 * <p>
 * Field names deliberately mirror the JSON the frontend consumes, and booleans
 * are named without an {@code is} prefix so Jackson emits the expected names.
 * </p>
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductCardDto {
    private Long id;
    private String title;
    private String description;
    private ShopProductType type;
    private ShopProductFormat format;
    private Double price;
    private String status;
    private boolean featured;
    private String preview;
    private boolean downloadable;
    private Long levelId;
    private String levelName;
    private Long classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Long chapterId;
    private String chapterTitle;
}
