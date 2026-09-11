package com.montola.school.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopClassDto {
    private Long id;
    private String name;
    private Long levelId;
    private Integer orderIndex;
}
