package com.whatshouldieat.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests formatting shared by the saved-list, details, form, and home views. */
class PlaceFormatterTest {
    /** Verifies that distances retain their value without integer overflow or trailing zeroes. */
    @Test
    void distanceFormatsWithoutOverflow() {
        assertEquals("1", PlaceFormatter.distance(1.0));
        assertEquals("1.25", PlaceFormatter.distance(1.25));
        assertEquals("3000000000", PlaceFormatter.distance(3_000_000_000.0));
    }

    /** Verifies the filled and empty stars used to display a rating. */
    @Test
    void starsFormatsRatingOutOfFive() {
        assertEquals("★★★☆☆", PlaceFormatter.stars(3));
    }
}
