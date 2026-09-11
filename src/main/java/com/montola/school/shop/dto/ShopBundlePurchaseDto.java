package com.montola.school.shop.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * A purchased bundle, as returned by {@code /my-purchases}.
 *
 * @author avidewan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopBundlePurchaseDto extends ShopBundleDto {
    private String kind = "BUNDLE";
    private LocalDateTime grantedAt;
}
