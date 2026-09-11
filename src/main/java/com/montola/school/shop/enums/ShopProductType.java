package com.montola.school.shop.enums;

/**
 * The kind of educational product being sold.
 *
 * @author avidewan
 */
public enum ShopProductType {

    NOTES,
    PDF_CHAPTER,
    INTERACTIVE_LEARNING,
    BOARD_ANALYSIS,
    CHAPTER_ANALYSIS,
    BOARD_SOLUTION,
    WORKSHEET,
    DRILLSHEET,
    RECALL_CARD,
    INTERACTIVE_TEST,
    BOOK;

    /**
     * Types that may be downloaded (as opposed to only viewed online).
     */
    public static boolean isDownloadable(ShopProductType type) {
        return type == WORKSHEET || type == DRILLSHEET || type == RECALL_CARD;
    }
}
