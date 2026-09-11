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
public class ShopDownloadDto {
    private String fileId;
    private String url;
    private long expiresInSeconds;
    private String watermark;
}
