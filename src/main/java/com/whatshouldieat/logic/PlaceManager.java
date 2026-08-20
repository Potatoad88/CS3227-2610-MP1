package com.whatshouldieat.logic;

import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.storage.JsonPlaceStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates food-place validation, queries, and persistent CRUD operations.
 */
public class PlaceManager {
    private final JsonPlaceStorage storage;
    private final List<FoodPlace> places;

    /**
     * Creates a manager and loads its initial places from storage.
     *
     * @param storage storage used to load and save places
     * @throws IOException if the saved places cannot be loaded
     */
    public PlaceManager(JsonPlaceStorage storage) throws IOException {
        this.storage = storage;
        this.places = new ArrayList<>(storage.load());
    }

    /**
     * Returns all places sorted by name.
     *
     * @return sorted snapshot of the managed places
     */
    public List<FoodPlace> getPlaces() {
        return places.stream()
                .sorted(Comparator.comparing(FoodPlace::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Returns places matching the supplied criteria, sorted by name.
     *
     * @param criteria filters to apply
     * @return matching places sorted without regard to letter case
     */
    public List<FoodPlace> search(FilterCriteria criteria) {
        return getPlaces().stream().filter(criteria::matches).toList();
    }

    /**
     * Finds a place by its stable identifier.
     *
     * @param id identifier to locate
     * @return matching place, or an empty optional
     */
    public Optional<FoodPlace> findById(String id) {
        return places.stream().filter(place -> place.getId().equals(id)).findFirst();
    }

    /**
     * Validates, adds, and persists a food place.
     *
     * @param place place to add
     * @throws IllegalArgumentException if the place contains invalid details
     * @throws IOException if the updated list cannot be saved
     */
    public void add(FoodPlace place) throws IOException {
        validate(place);
        places.add(place);
        save();
    }

    /**
     * Replaces the editable details of an existing place and persists them.
     * The existing place identifier is preserved.
     *
     * @param id identifier of the place to update
     * @param replacement replacement details
     * @throws IllegalArgumentException if the replacement is invalid or the
     *         identifier does not exist
     * @throws IOException if the updated list cannot be saved
     */
    public void update(String id, FoodPlace replacement) throws IOException {
        validate(replacement);
        FoodPlace existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place not found."));
        existing.updateFrom(replacement);
        save();
    }

    /**
     * Deletes the place with the specified identifier and persists the list.
     *
     * @param id identifier of the place to delete
     * @throws IOException if the updated list cannot be saved
     */
    public void delete(String id) throws IOException {
        places.removeIf(place -> place.getId().equals(id));
        save();
    }

    private void validate(FoodPlace place) {
        if (place.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (place.getCuisine().trim().isEmpty()) {
            throw new IllegalArgumentException("Cuisine type is required.");
        }
        if (!Double.isFinite(place.getDistanceKm()) || place.getDistanceKm() < 0) {
            throw new IllegalArgumentException("Distance must be a finite number that is zero or greater.");
        }
        if (place.getRating() < 1 || place.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }

    private void save() throws IOException {
        storage.save(places);
    }
}
