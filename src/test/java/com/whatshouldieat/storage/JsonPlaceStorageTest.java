package com.whatshouldieat.storage;

import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.model.PriceRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests JSON persistence using temporary files instead of production data. */
class JsonPlaceStorageTest {
    @TempDir
    Path tempDir;

    /** Verifies that a missing storage file represents an empty saved list. */
    @Test
    void missingStorageFileStartsEmpty() throws IOException {
        JsonPlaceStorage storage = new JsonPlaceStorage(tempDir.resolve("missing-places.json"));

        assertTrue(storage.load().isEmpty());
    }

    /** Verifies that a whitespace-only storage file represents an empty saved list. */
    @Test
    void whitespaceOnlyStorageFileStartsEmpty() throws IOException {
        Path file = tempDir.resolve("places.json");
        Files.writeString(file, " \n\t ");

        assertTrue(new JsonPlaceStorage(file).load().isEmpty());
    }

    /** Verifies that every stored field and escaped character survives a round trip. */
    @Test
    void roundTripPreservesAllFieldsAndSpecialCharacters() throws IOException {
        FoodPlace original = new FoodPlace(null, "Quote \" Cafe", "Other", 0,
                PriceRange.TWO, 3, List.of("Tea", "Quiet"), "Line one\nLine two\\nTab\there\rEnd");
        Path file = tempDir.resolve("places.json");
        JsonPlaceStorage storage = new JsonPlaceStorage(file);

        assertNotNull(original.getId());
        storage.save(List.of(original));
        FoodPlace loaded = storage.load().get(0);

        assertEquals(original.getId(), loaded.getId());
        assertEquals(original.getName(), loaded.getName());
        assertEquals(original.getCuisine(), loaded.getCuisine());
        assertEquals(original.getDistanceKm(), loaded.getDistanceKm());
        assertEquals(original.getPriceRange(), loaded.getPriceRange());
        assertEquals(original.getRating(), loaded.getRating());
        assertEquals(original.getTags(), loaded.getTags());
        assertEquals(original.getNotes(), loaded.getNotes());
    }

    /** Verifies that saving replaces existing data without leaving temporary files. */
    @Test
    void saveReplacesExistingDataAndCleansUpTemporaryFile() throws IOException {
        Path file = tempDir.resolve("places.json");
        JsonPlaceStorage storage = new JsonPlaceStorage(file);
        FoodPlace replacement = place("Replacement");

        storage.save(List.of(place("Original")));
        storage.save(List.of(replacement));

        assertEquals(List.of(replacement.getId()), storage.load().stream().map(FoodPlace::getId).toList());
        try (var files = Files.list(tempDir)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    /** Verifies that saving creates missing parent directories. */
    @Test
    void saveCreatesMissingParentDirectories() throws IOException {
        Path file = tempDir.resolve("nested/data/places.json");
        JsonPlaceStorage storage = new JsonPlaceStorage(file);
        FoodPlace place = place("Nested");

        storage.save(List.of(place));

        assertTrue(Files.exists(file));
        assertEquals(place.getId(), storage.load().get(0).getId());
    }

    private FoodPlace place(String name) {
        return new FoodPlace(name, "Other", 2, PriceRange.TWO, 4, List.of(), "");
    }
}
