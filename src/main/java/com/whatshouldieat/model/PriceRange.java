package com.whatshouldieat.model;

/**
 * Supported restaurant price categories, represented by dollar-sign labels.
 */
public enum PriceRange {
    /** Lowest price category. */
    ONE("$"),
    /** Lower-middle price category. */
    TWO("$$"),
    /** Upper-middle price category. */
    THREE("$$$"),
    /** Highest price category. */
    FOUR("$$$$");

    private final String label;

    PriceRange(String label) {
        this.label = label;
    }

    /**
     * Returns the dollar-sign label displayed by the UI.
     *
     * @return display label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Converts a dollar-sign label or enum name into a price range.
     *
     * @param label label or enum name to convert
     * @return matching price range, or {@link #TWO} when no match exists
     */
    public static PriceRange fromLabel(String label) {
        for (PriceRange range : values()) {
            if (range.label.equals(label) || range.name().equalsIgnoreCase(label)) {
                return range;
            }
        }
        return TWO;
    }
}
