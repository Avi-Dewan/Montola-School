package com.montola.school.shop.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Product as returned to the admin product list.
 * <p>
 * The plain card plus the stored file reference, so the edit form can show what is
 * attached. The reference is deliberately kept off {@link ShopProductCardDto},
 * because the public catalog endpoints serve that same DTO — putting it there would
 * expose every product's storage key to anonymous visitors.
 * </p>
 * <p>
 * This is display-only. The reference is not meant to be submitted back: the update
 * path re-derives {@code storageProvider} from whichever backend is active, so
 * resending it could silently re-label a file as an external reference.
 * </p>
 *
 * @author avidewan
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShopAdminProductDto extends ShopProductCardDto {

    private String fileId;
}
