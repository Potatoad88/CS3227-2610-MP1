package com.whatshouldieat.logic;

import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.model.PriceRange;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests random candidate selection independently of filtering. */
class RandomPickerTest {
    /** Verifies the injected random index determines the selected candidate. */
    @Test
    void injectedRandomIndexSelectsCorrespondingPlace() {
        List<FoodPlace> places = List.of(place("First"), place("Second"), place("Third"));
        RandomPicker picker = new RandomPicker(new Random() {
            @Override
            public int nextInt(int bound) {
                assertEquals(places.size(), bound);
                return 1;
            }
        });

        assertEquals(places.get(1), picker.pick(places).orElseThrow());
    }

    /** Verifies an empty candidate list produces no selection. */
    @Test
    void emptyCandidateListProducesNoSelection() {
        assertTrue(new RandomPicker().pick(List.of()).isEmpty());
    }

    private FoodPlace place(String name) {
        return new FoodPlace(name, "Other", 1, PriceRange.TWO, 3, List.of(), "");
    }
}
