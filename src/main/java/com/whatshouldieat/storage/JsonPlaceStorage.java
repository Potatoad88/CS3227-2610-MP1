package com.whatshouldieat.storage;

import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.model.PriceRange;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Loads and saves food places in a local JSON file.
 *
 * <p>A missing or empty storage file represents an empty saved-place list.</p>
 */
public class JsonPlaceStorage {
    private static final DateTimeFormatter BACKUP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final Path file;
    private Path recoveredFile;

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
     * @return food places loaded from storage, or an empty list if the file
     *         does not exist
     * @throws IOException if the storage file cannot be read
     */
    public List<FoodPlace> load() throws IOException {
        recoveredFile = null;
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        String content = Files.readString(file).trim();
        if (content.isEmpty() || content.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return parsePlaces(content);
        } catch (IllegalArgumentException | IndexOutOfBoundsException exception) {
            recoverMalformedFile();
            return new ArrayList<>();
        }
    }

    /**
     * Returns the backup created when malformed data was recovered during the
     * most recent load.
     *
     * @return backup path, or empty when no recovery occurred
     */
    public Optional<Path> getRecoveredFile() {
        return Optional.ofNullable(recoveredFile);
    }

    /**
     * Replaces the storage file contents with the supplied places.
     * Missing parent directories are created automatically.
     *
     * @param places food places to persist
     * @throws IOException if the storage file cannot be created or written
     */
    public void save(List<FoodPlace> places) throws IOException {
        Path target = file.toAbsolutePath();
        Path parent = target.getParent();
        Files.createDirectories(parent);
        String json = places.stream()
                .map(this::toJson)
                .collect(Collectors.joining(",\n", "[\n", "\n]\n"));
        Path temporary = Files.createTempFile(parent, target.getFileName().toString(), ".tmp");
        try {
            Files.writeString(temporary, json);
            replace(temporary, target);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private void replace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private List<FoodPlace> parsePlaces(String content) {
        if (!content.startsWith("[") || !content.endsWith("]")) {
            throw new IllegalArgumentException("Expected a JSON array.");
        }
        List<String> objects = splitObjects(content);
        String body = content.substring(1, content.length() - 1).trim();
        if (!body.isEmpty() && objects.isEmpty()) {
            throw new IllegalArgumentException("Expected JSON objects.");
        }
        List<FoodPlace> places = new ArrayList<>();
        for (String object : objects) {
            Map<String, String> values = parseObject(object);
            places.add(new FoodPlace(
                    values.get("id"),
                    values.getOrDefault("name", ""),
                    values.getOrDefault("cuisine", ""),
                    Double.parseDouble(values.getOrDefault("distanceKm", "0")),
                    PriceRange.fromLabel(values.getOrDefault("priceRange", "$$")),
                    Integer.parseInt(values.getOrDefault("rating", "3")),
                    parseTags(values.getOrDefault("tags", "")),
                    values.getOrDefault("notes", "")
            ));
        }
        return places;
    }

    private void recoverMalformedFile() throws IOException {
        String name = file.getFileName().toString();
        int extension = name.lastIndexOf('.');
        String stem = extension < 0 ? name : name.substring(0, extension);
        String suffix = extension < 0 ? "" : name.substring(extension);
        Path backup = file.resolveSibling(stem + "-corrupted-"
                + BACKUP_TIMESTAMP.format(LocalDateTime.now()) + suffix);
        Files.move(file, backup);
        save(List.of());
        recoveredFile = backup;
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
        return value == null ? "" : value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (char character : value.toCharArray()) {
            if (escaped) {
                result.append(switch (character) {
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 't' -> '\t';
                default -> character;
                });
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else {
                result.append(character);
            }
        }
        if (escaped) {
            result.append('\\');
        }
        return result.toString();
    }

    private List<String> splitObjects(String content) {
        String trimmed = content.substring(1, content.length() - 1).trim();
        if (trimmed.isEmpty()) {
            return List.of();
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
        if (depth != 0 || inString) {
            throw new IllegalArgumentException("Unterminated JSON value.");
        }
        return objects;
    }

    private Map<String, String> parseObject(String object) {
        String body = object.substring(1, object.length() - 1).trim();
        Map<String, String> values = new LinkedHashMap<>();
        for (String pair : splitPairs(body)) {
            int colon = pair.indexOf(':');
            if (colon < 0) {
                throw new IllegalArgumentException("Expected a JSON field.");
            }
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

}
