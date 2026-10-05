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
public class ShopLevelDto {
    private Long id;
    private String name;
    private Integer orderIndex;
}
