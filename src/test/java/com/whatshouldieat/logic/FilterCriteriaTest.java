package com.whatshouldieat.logic;

import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.model.PriceRange;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests filter boundaries and inactive saved-place criteria. */
class FilterCriteriaTest {
    /** Verifies maximum distance is inclusive and excludes greater values. */
    @Test
    void maximumDistanceIncludesItsBoundary() {
        FilterCriteria criteria = new FilterCriteria("", "Any Cuisine", "Any Price", "5");

        assertTrue(criteria.matches(place(5)));
        assertFalse(criteria.matches(place(5.01)));
    }

    /** Verifies null, blank, and Any values leave their filters inactive. */
    @Test
    void nullBlankAndAnyValuesDoNotRestrictResults() {
        FoodPlace place = place(3);

        assertTrue(new FilterCriteria(null, null, null, null).matches(place));
        assertTrue(new FilterCriteria(" ", "Any Cuisine", "Any Price", " ").matches(place));
    }

    private FoodPlace place(double distance) {
        return new FoodPlace("Cafe", "Other", distance, PriceRange.TWO, 3, List.of(), "");
    }
}
