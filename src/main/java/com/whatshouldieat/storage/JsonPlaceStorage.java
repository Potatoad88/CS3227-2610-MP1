package com.whatshouldieat.storage;

import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.model.PriceRange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loads and saves food places in a local JSON file.
 *
 * <p>If the storage file does not exist, {@link #load()} returns the
 * application's initial sample places. An existing empty file represents an
 * empty saved-place list.</p>
 */
public class JsonPlaceStorage {
    private final Path file;

    /**
     * Creates a storage handler for the specified JSON file.
     *
     * @param file path of the file used to store food places
     */
    public JsonPlaceStorage(Path file) {
        this.file = file;
    }

    /**
     * Loads all food places from the storage file.
     *
     * @return food places loaded from storage, or sample places if the file
     *         does not exist
     * @throws IOException if the storage file cannot be read
     */
    public List<FoodPlace> load() throws IOException {
        if (!Files.exists(file)) {
            return seedPlaces();
        }
        String content = Files.readString(file).trim();
        if (content.isEmpty() || content.equals("[]")) {
            return new ArrayList<>();
        }
        List<FoodPlace> places = new ArrayList<>();
        for (String object : splitObjects(content)) {
            Map<String, String> values = parseObject(object);
            places.add(new FoodPlace(
                    values.get("id"),
                    values.getOrDefault("name", ""),
                    values.getOrDefault("cuisine", ""),
                    parseDistance(values),
                    PriceRange.fromLabel(values.getOrDefault("priceRange", "$$")),
                    Integer.parseInt(values.getOrDefault("rating", "3")),
                    parseTags(values.getOrDefault("tags", "")),
                    values.getOrDefault("notes", "")
            ));
        }
        return places;
    }

    /**
     * Replaces the storage file contents with the supplied places.
     * Missing parent directories are created automatically.
     *
     * @param places food places to persist
     * @throws IOException if the storage file cannot be created or written
     */
    public void save(List<FoodPlace> places) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        String json = places.stream()
                .map(this::toJson)
                .collect(Collectors.joining(",\n", "[\n", "\n]\n"));
        Files.writeString(file, json);
    }

    private String toJson(FoodPlace place) {
        return "  {"
                + field("id", place.getId()) + ", "
                + field("name", place.getName()) + ", "
                + field("cuisine", place.getCuisine()) + ", "
                + "\"distanceKm\": " + place.getDistanceKm() + ", "
                + field("priceRange", place.getPriceRange().getLabel()) + ", "
                + "\"rating\": " + place.getRating() + ", "
                + field("tags", String.join(",", place.getTags())) + ", "
                + field("notes", place.getNotes())
                + "}";
    }

    private String field(String name, String value) {
        return "\"" + name + "\": \"" + escape(value) + "\"";
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String unescape(String value) {
        return value.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private List<String> splitObjects(String content) {
        String trimmed = content.substring(1, content.length() - 1).trim();
        if (trimmed.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                inString = !inString;
            } else if (!inString && c == '{') {
                depth++;
            } else if (!inString && c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(trimmed.substring(start, i + 1));
                }
            } else if (!inString && c == ',' && depth == 0) {
                start = i + 1;
            }
        }
        return objects;
    }

    private Map<String, String> parseObject(String object) {
        String body = object.substring(1, object.length() - 1).trim();
        Map<String, String> values = new LinkedHashMap<>();
        for (String pair : splitPairs(body)) {
            int colon = pair.indexOf(':');
            String key = stripQuotes(pair.substring(0, colon).trim());
            String rawValue = pair.substring(colon + 1).trim();
            values.put(key, stripQuotes(rawValue));
        }
        return values;
    }

    private List<String> splitPairs(String body) {
        List<String> pairs = new ArrayList<>();
        int start = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                inString = !inString;
            } else if (!inString && c == ',') {
                pairs.add(body.substring(start, i).trim());
                start = i + 1;
            }
        }
        pairs.add(body.substring(start).trim());
        return pairs;
    }

    private String stripQuotes(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return unescape(value.substring(1, value.length() - 1));
        }
        return value;
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
    }

    private double parseDistance(Map<String, String> values) {
        return Double.parseDouble(values.getOrDefault("distanceKm", "0"));
    }

    private List<FoodPlace> seedPlaces() {
        List<FoodPlace> seed = new ArrayList<>();
        seed.add(new FoodPlace("O-Ku Sushi", "Japanese", 4.8,
                PriceRange.THREE, 5, List.of("Date Night", "Omakase"), "Good for slow dinners."));
        seed.add(new FoodPlace("Pasta Bella", "Italian", 2.4,
                PriceRange.TWO, 4, List.of("Cozy", "Carbs"), "Reliable pasta and warm lighting."));
        seed.add(new FoodPlace("El Super Taco", "Mexican", 6.1,
                PriceRange.ONE, 5, List.of("Quick Bite", "Spicy"), "Best when craving something loud."));
        return seed;
    }
}
