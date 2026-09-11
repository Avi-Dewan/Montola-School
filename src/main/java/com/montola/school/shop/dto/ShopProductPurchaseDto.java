package com.montola.school.shop.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * A purchased product, as returned by {@code /my-purchases}.
 *
 * @author avidewan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopProductPurchaseDto extends ShopProductCardDto {
    private String kind = "PRODUCT";
    private LocalDateTime grantedAt;
}
