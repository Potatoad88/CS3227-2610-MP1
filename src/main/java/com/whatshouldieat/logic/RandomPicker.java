package com.whatshouldieat.logic;

import com.whatshouldieat.model.FoodPlace;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Selects a random food place from those matching the active filters.
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
     * Randomly selects one place that satisfies the supplied criteria.
     *
     * @param places places available for selection
     * @param criteria filters that determine which places are eligible
     * @return the selected place, or an empty optional when none are eligible
     */
    public Optional<FoodPlace> pick(List<FoodPlace> places, FilterCriteria criteria) {
        List<FoodPlace> eligible = places.stream()
                .filter(criteria::matches)
                .toList();
        if (eligible.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(eligible.get(random.nextInt(eligible.size())));
    }
}
