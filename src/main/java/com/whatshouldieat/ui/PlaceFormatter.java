package com.whatshouldieat.ui;

import java.math.BigDecimal;

/** Formats food-place values consistently across views. */
final class PlaceFormatter {
    private PlaceFormatter() {
    }

    static String distance(double distance) {
        return BigDecimal.valueOf(distance).stripTrailingZeros().toPlainString();
    }

    static String stars(int rating) {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }
}
