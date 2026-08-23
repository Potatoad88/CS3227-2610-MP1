package com.whatshouldieat.logic;

import com.whatshouldieat.model.FoodPlace;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Selects a random food place from a supplied list.
 */
public class RandomPicker {
    private final Random random;

    /** Creates a picker using a non-deterministic random source. */
    public RandomPicker() {
        this(new Random());
    }

    /**
     * Creates a picker with an injectable random source.
     *
     * @param random random source used for selection
     */
    public RandomPicker(Random random) {
        this.random = random;
    }

    /**
     * Randomly selects one place from the supplied list.
     *
     * @param places places available for selection
     * @return the selected place, or an empty optional when the list is empty
     */
    public Optional<FoodPlace> pick(List<FoodPlace> places) {
        if (places.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(places.get(random.nextInt(places.size())));
    }
}
