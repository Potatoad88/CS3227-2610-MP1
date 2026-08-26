package com.whatshouldieat.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests conversion between stored/displayed price labels and price ranges. */
class PriceRangeTest {
    /** Verifies every label and enum name maps to its corresponding range. */
    @Test
    void labelsAndEnumNamesMapToTheirRanges() {
        for (PriceRange range : PriceRange.values()) {
            assertEquals(range, PriceRange.fromLabel(range.getLabel()));
            assertEquals(range, PriceRange.fromLabel(range.name().toLowerCase()));
        }
    }

    /** Verifies unsupported labels use the documented middle-price fallback. */
    @Test
    void unsupportedLabelDefaultsToTwo() {
        assertEquals(PriceRange.TWO, PriceRange.fromLabel("unsupported"));
    }
}
