package com.whatshouldieat.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a saved food place and its dining details.
 *
 * <p>Each place has a stable identifier used for updates and deletion. Tags
 * are copied when supplied or returned so callers cannot modify the internal
 * list directly.</p>
 */
public class FoodPlace {
    private final String id;
    private final String name;
    private final String cuisine;
    private final double distanceKm;
    private final PriceRange priceRange;
    private final int rating;
    private final List<String> tags;
    private final String notes;

    /**
     * Creates a food place with a generated identifier.
     *
     * @param name name of the place
     * @param cuisine cuisine served by the place
     * @param distanceKm distance to the place in kilometres
     * @param priceRange price category of the place
     * @param rating rating from 1 to 5
     * @param tags descriptive tags for the place
     * @param notes personal notes about the place
     */
    public FoodPlace(String name, String cuisine, double distanceKm, PriceRange priceRange,
                     int rating, List<String> tags, String notes) {
        this(UUID.randomUUID().toString(), name, cuisine, distanceKm, priceRange, rating, tags, notes);
    }

    /**
     * Creates a food place with a supplied identifier, generating one when it
     * is null or blank.
     *
     * @param id stable identifier, or null to generate one
     * @param name name of the place
     * @param cuisine cuisine served by the place
     * @param distanceKm distance to the place in kilometres
     * @param priceRange price category of the place
     * @param rating rating from 1 to 5
     * @param tags descriptive tags for the place
     * @param notes personal notes about the place
     */
    public FoodPlace(String id, String name, String cuisine, double distanceKm, PriceRange priceRange,
                     int rating, List<String> tags, String notes) {
        this.id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.cuisine = cuisine;
        this.distanceKm = distanceKm;
        this.priceRange = priceRange;
        this.rating = rating;
        this.tags = new ArrayList<>(tags);
        this.notes = notes;
    }

    /** @return stable identifier */
    public String getId() {
        return id;
    }

    /** @return place name */
    public String getName() {
        return name;
    }

    /** @return cuisine name */
    public String getCuisine() {
        return cuisine;
    }

    /** @return distance in kilometres */
    public double getDistanceKm() {
        return distanceKm;
    }

    /** @return price category */
    public PriceRange getPriceRange() {
        return priceRange;
    }

    /** @return rating from 1 to 5 */
    public int getRating() {
        return rating;
    }

    /** @return a copy of the place's tags */
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    /** @return personal notes */
    public String getNotes() {
        return notes;
    }

}
