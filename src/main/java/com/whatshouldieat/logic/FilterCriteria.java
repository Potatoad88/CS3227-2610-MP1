package com.whatshouldieat.logic;

import com.whatshouldieat.model.FoodPlace;

import java.util.Locale;

/**
 * Describes the name, cuisine, price, and distance filters applied to places.
 * Blank values and values beginning with {@code Any} do not restrict results.
 */
public class FilterCriteria {
    private final String query;
    private final String cuisine;
    private final String priceRange;
    private final Double maxDistanceKm;

    /**
     * Creates criteria containing only a text query.
     *
     * @param query text to search for
     */
    public FilterCriteria(String query) {
        this(query, "Any Cuisine", "Any Price", "");
    }

    /**
     * Creates criteria from the saved-list search and filter controls.
     *
     * @param query text to search for
     * @param cuisine cuisine label, or an Any/blank value
     * @param priceRange price label, or an Any/blank value
     * @param maxDistanceKm maximum distance text, or blank for no maximum
     */
    public FilterCriteria(String query, String cuisine, String priceRange, String maxDistanceKm) {
        this.query = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        this.cuisine = cuisine == null ? "" : cuisine.trim();
        this.priceRange = priceRange == null ? "" : priceRange.trim();
        this.maxDistanceKm = parseMaxDistance(maxDistanceKm);
    }

    /**
     * Tests whether a food place satisfies every active criterion.
     * Text searches match only the place name, without regard to letter case.
     *
     * @param place place to test
     * @return {@code true} when the place matches all active filters
     */
    public boolean matches(FoodPlace place) {
        if (!isAny(cuisine) && !place.getCuisine().equalsIgnoreCase(cuisine)) {
            return false;
        }
        if (!isAny(priceRange) && !place.getPriceRange().getLabel().equals(priceRange)) {
            return false;
        }
        if (maxDistanceKm != null && place.getDistanceKm() > maxDistanceKm) {
            return false;
        }
        if (query.isEmpty()) {
            return true;
        }
        return contains(place.getName());
    }

    private boolean contains(String value) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private boolean isAny(String value) {
        return value.isEmpty() || value.startsWith("Any");
    }

    private Double parseMaxDistance(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            double parsed = Double.parseDouble(value.trim());
            return Double.isFinite(parsed) && parsed >= 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
