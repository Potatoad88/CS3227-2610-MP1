package com.whatshouldieat;

import com.whatshouldieat.logic.FilterCriteria;
import com.whatshouldieat.logic.PlaceManager;
import com.whatshouldieat.logic.RandomPicker;
import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.model.PriceRange;
import com.whatshouldieat.storage.JsonPlaceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceManagerTest {
    @TempDir
    Path tempDir;

    private Path dataFile;
    private PlaceManager manager;

    @BeforeEach
    void setUp() throws IOException {
        dataFile = tempDir.resolve("places.json");
        Files.writeString(dataFile, "[]");
        manager = new PlaceManager(new JsonPlaceStorage(dataFile));
    }

    @Test
    void addUpdateDeleteArePersisted() throws IOException {
        FoodPlace place = place("Noodle House", "Chinese", 1.8, PriceRange.ONE, 4);

        manager.add(place);
        assertEquals(1, manager.getPlaces().size());

        manager.update(place.getId(), place("Noodle House Plus", "Chinese", 2.2, PriceRange.TWO, 5));
        FoodPlace updated = manager.findById(place.getId()).orElseThrow();
        assertEquals("Noodle House Plus", updated.getName());
        assertEquals(place.getId(), updated.getId());

        PlaceManager reloaded = new PlaceManager(new JsonPlaceStorage(dataFile));
        assertEquals("Noodle House Plus", reloaded.findById(place.getId()).orElseThrow().getName());

        manager.delete(place.getId());
        assertTrue(manager.getPlaces().isEmpty());
        assertTrue(new PlaceManager(new JsonPlaceStorage(dataFile)).getPlaces().isEmpty());
    }

    @Test
    void invalidPlaceDetailsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.add(place(" ", "Chinese", 1, PriceRange.ONE, 3)));
        assertThrows(IllegalArgumentException.class,
                () -> manager.add(place("Cafe", " ", 1, PriceRange.ONE, 3)));
        assertThrows(IllegalArgumentException.class,
                () -> manager.add(place("Cafe", "Other", -1, PriceRange.ONE, 3)));
        assertThrows(IllegalArgumentException.class,
                () -> manager.add(place("Cafe", "Other", Double.NaN, PriceRange.ONE, 3)));
        assertThrows(IllegalArgumentException.class,
                () -> manager.add(place("Cafe", "Other", 1, PriceRange.ONE, 0)));
    }

    @Test
    void missingStorageFileStartsEmpty() throws IOException {
        Path missingFile = tempDir.resolve("missing-places.json");

        PlaceManager emptyManager = new PlaceManager(new JsonPlaceStorage(missingFile));

        assertTrue(emptyManager.getPlaces().isEmpty());
    }

    @Test
    void searchAppliesTextAndFieldFiltersTogether() throws IOException {
        FoodPlace near = new FoodPlace("Noodle House", "Chinese", 1.8,
                PriceRange.ONE, 4, List.of("Soup", "Quick"), "Order dry noodles.");
        FoodPlace far = new FoodPlace("Fine Dining", "Western", 8,
                PriceRange.FOUR, 5, List.of("Date Night"), "");
        manager.add(near);
        manager.add(far);

        List<FoodPlace> matches = manager.search(new FilterCriteria("soup", "Chinese", "$", "2"));

        assertEquals(List.of(near), matches);
        assertTrue(manager.search(new FilterCriteria("NOODLE")).contains(near));
        assertFalse(manager.search(new FilterCriteria("", "Any Cuisine", "$$$$", "")).contains(near));
    }

    @Test
    void randomPickerOnlyUsesMatchingPlaces() {
        FoodPlace included = place("Nearby", "Local", 1, PriceRange.ONE, 3);
        FoodPlace excluded = place("Far Away", "Local", 20, PriceRange.ONE, 3);
        RandomPicker picker = new RandomPicker(new Random(1));

        Optional<FoodPlace> picked = picker.pick(List.of(included, excluded),
                new FilterCriteria("", "Any Cuisine", "Any Price", "5"));

        assertEquals(included, picked.orElseThrow());
        assertTrue(picker.pick(List.of(excluded), new FilterCriteria("nearby")).isEmpty());
    }

    @Test
    void storageRoundTripPreservesSpecialCharactersAndMissingIds() throws IOException {
        FoodPlace original = new FoodPlace(null, "Quote \" Cafe", "Other", 0,
                PriceRange.TWO, 3, List.of("Tea", "Quiet"), "Line one\nLine two\\end");
        assertNotNull(original.getId());

        JsonPlaceStorage storage = new JsonPlaceStorage(dataFile);
        storage.save(List.of(original));
        FoodPlace loaded = storage.load().get(0);

        assertEquals(original.getId(), loaded.getId());
        assertEquals(original.getName(), loaded.getName());
        assertEquals(original.getTags(), loaded.getTags());
        assertEquals(original.getNotes(), loaded.getNotes());
    }

    private FoodPlace place(String name, String cuisine, double distance,
                            PriceRange priceRange, int rating) {
        return new FoodPlace(name, cuisine, distance, priceRange, rating, List.of(), "");
    }
}
