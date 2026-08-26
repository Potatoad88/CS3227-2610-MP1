package com.whatshouldieat.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/** Tests identity and immutable collection behavior of food places. */
class FoodPlaceTest {
    /** Verifies generated IDs are valid and independent of duplicate names. */
    @Test
    void idsAreGeneratedIndependentlyOfNamesAndSuppliedIdsArePreserved() {
        FoodPlace first = place(null, "Same Name");
        FoodPlace second = place("", "Same Name");
        FoodPlace supplied = place("known-id", "Same Name");

        UUID.fromString(first.getId());
        UUID.fromString(second.getId());
        assertNotEquals(first.getId(), second.getId());
        assertEquals("known-id", supplied.getId());
    }

    /** Verifies callers cannot mutate tags through input or returned lists. */
    @Test
    void tagsAreDefensivelyCopiedOnInputAndOutput() {
        List<String> suppliedTags = new ArrayList<>(List.of("Quiet"));
        FoodPlace place = new FoodPlace("Cafe", "Other", 1, PriceRange.TWO, 3, suppliedTags, "");

        suppliedTags.add("Late Night");
        List<String> returnedTags = place.getTags();
        returnedTags.add("Outdoor");

        assertEquals(List.of("Quiet"), place.getTags());
    }

    private FoodPlace place(String id, String name) {
        return new FoodPlace(id, name, "Other", 1, PriceRange.TWO, 3, List.of(), "");
    }
}
