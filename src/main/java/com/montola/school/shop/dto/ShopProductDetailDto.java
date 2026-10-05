package com.montola.school.shop.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Product detail, adding the per-viewer access flags.
 *
 * @author avidewan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopProductDetailDto extends ShopProductCardDto {
    private boolean entitled;
    private boolean canDownload;
}
