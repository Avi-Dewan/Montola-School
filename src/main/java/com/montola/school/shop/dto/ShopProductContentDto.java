package com.montola.school.shop.dto;

import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gated product payload returned by {@code /products/{id}/content}.
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductContentDto {
    private Long id;
    private String title;
    private ShopProductType type;
    private ShopProductFormat format;
    private String watermark;
    private String html;
    private String fileId;
    private Integer pageCount;

    /**
     * Short-lived URL for reading an attached file in place. Entitlement-gated
     * like the rest of this payload — viewing is not the same as downloading,
     * which stays restricted to staff and DOWNLOAD-mode bundles.
     */
    private String viewUrl;
}
