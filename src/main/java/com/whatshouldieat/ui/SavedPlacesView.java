package com.whatshouldieat.ui;

import com.whatshouldieat.logic.FilterCriteria;
import com.whatshouldieat.logic.PlaceManager;
import com.whatshouldieat.logic.RandomPicker;
import com.whatshouldieat.model.FoodPlace;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Displays saved places and provides searching, filtering, random selection,
 * editing, and deletion controls.
 */
class SavedPlacesView {
    private static final PseudoClass OPEN = PseudoClass.getPseudoClass("open");

    private final PlaceManager manager;
    private final AppView app;
    private final RandomPicker picker = new RandomPicker();
    private final TextField search = new TextField();
    private final ChoiceBox<String> cuisineFilter = new ChoiceBox<>();
    private final ChoiceBox<String> priceFilter = new ChoiceBox<>();
    private final TextField distanceFilter = new TextField();
    private final VBox placeList = new VBox(12);
    private final FlowPane filterPanel = new FlowPane(12, 12);
    private final Button filterButton = new Button();

    private String appliedCuisine = "Any Cuisine";
    private String appliedPrice = "Any Price";
    private String appliedDistance = "";
    private String appliedQuery = "";

    SavedPlacesView(PlaceManager manager, AppView app) {
        this.manager = manager;
        this.app = app;
    }

    Parent getRoot() {
        VBox page = new VBox(22);
        page.getStyleClass().add("saved-page");
        page.setPadding(new Insets(30, 36, 76, 36));

        Label title = new Label("Saved Places");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Manage and organize your saved culinary discoveries.");
        subtitle.getStyleClass().add("subtitle");
        VBox heading = new VBox(5, title, subtitle);

        FlowPane toolbar = buildToolbar();
        configureFilterPanel();
        placeList.getStyleClass().add("place-list");
        page.getChildren().addAll(heading, toolbar, filterPanel, placeList);
        refresh();

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().addAll("scroll", "page-scroll");
        return scroll;
    }

    private FlowPane buildToolbar() {
        search.setPromptText("Search saved places");
        search.getStyleClass().add("saved-search");
        search.setOnAction(event -> applySearch());
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank() && !appliedQuery.isEmpty()) {
                appliedQuery = "";
                refresh();
            }
        });
        Button searchButton = new Button("⌕");
        searchButton.getStyleClass().add("search-button");
        searchButton.setTooltip(new Tooltip("Search saved places"));
        searchButton.setAccessibleText("Search saved places");
        searchButton.setOnAction(event -> applySearch());
        HBox searchControl = new HBox(search, searchButton);
        searchControl.getStyleClass().add("search-control");

        Button random = new Button("↯  Random");
        random.getStyleClass().add("secondary-button");
        random.setOnAction(event -> pickRandom());

        filterButton.getStyleClass().add("filter-toggle");
        filterButton.setOnAction(event -> toggleFilterPanel());
        updateFilterButton();

        Button add = new Button("+  Add Place");
        add.getStyleClass().add("primary-button");
        add.setOnAction(event -> app.showAddPlace());

        FlowPane toolbar = new FlowPane(10, 10, searchControl, random, filterButton, add);
        toolbar.getStyleClass().add("saved-toolbar");
        return toolbar;
    }

    private void applySearch() {
        appliedQuery = search.getText().trim();
        refresh();
    }

    private void configureFilterPanel() {
        cuisineFilter.getItems().addAll("Any Cuisine", "Japanese", "Italian", "Mexican", "Chinese", "Indian",
                "Korean", "Thai", "Western", "Local", "Other");
        cuisineFilter.setValue(appliedCuisine);
        cuisineFilter.getStyleClass().add("filter-choice");

        priceFilter.getItems().addAll("Any Price", "$", "$$", "$$$", "$$$$");
        priceFilter.setValue(appliedPrice);
        priceFilter.getStyleClass().add("filter-choice");

        distanceFilter.setPromptText("Maximum distance (km)");
        distanceFilter.getStyleClass().add("filter-distance");

        Button clear = new Button("Clear");
        clear.getStyleClass().add("secondary-button");
        clear.setOnAction(event -> clearFilters());
        Button apply = new Button("Apply Filters");
        apply.getStyleClass().add("primary-button");
        apply.setOnAction(event -> applyFilters());
        HBox actions = new HBox(9, clear, apply);
        actions.setAlignment(Pos.BOTTOM_RIGHT);
        actions.getStyleClass().add("filter-actions");

        filterPanel.getStyleClass().add("filter-panel");
        filterPanel.getChildren().addAll(
                filterField("Cuisine", cuisineFilter),
                filterField("Price", priceFilter),
                filterField("Maximum distance", distanceFilter),
                actions);
        setFilterPanelOpen(false);
    }

    private VBox filterField(String labelText, Node control) {
        Label label = new Label(labelText);
        label.getStyleClass().add("filter-label");
        VBox field = new VBox(6, label, control);
        field.getStyleClass().add("filter-field");
        return field;
    }

    private void toggleFilterPanel() {
        boolean show = !filterPanel.isVisible();
        if (show) {
            cuisineFilter.setValue(appliedCuisine);
            priceFilter.setValue(appliedPrice);
            distanceFilter.setText(appliedDistance);
        }
        setFilterPanelOpen(show);
    }

    private void setFilterPanelOpen(boolean open) {
        filterPanel.setManaged(open);
        filterPanel.setVisible(open);
        filterButton.pseudoClassStateChanged(OPEN, open);
    }

    private void applyFilters() {
        String distance = distanceFilter.getText().trim();
        if (!isValidDistance(distance)) {
            AppDialog.showInfo(filterPanel, "!", "Invalid Distance",
                    "Enter a non-negative number for the maximum distance in kilometres.");
            return;
        }
        appliedCuisine = cuisineFilter.getValue();
        appliedPrice = priceFilter.getValue();
        appliedDistance = distance;
        setFilterPanelOpen(false);
        updateFilterButton();
        refresh();
    }

    private boolean isValidDistance(String value) {
        if (value.isEmpty()) {
            return true;
        }
        try {
            double parsed = Double.parseDouble(value);
            return Double.isFinite(parsed) && parsed >= 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private void clearFilters() {
        appliedCuisine = "Any Cuisine";
        appliedPrice = "Any Price";
        appliedDistance = "";
        cuisineFilter.setValue(appliedCuisine);
        priceFilter.setValue(appliedPrice);
        distanceFilter.clear();
        setFilterPanelOpen(false);
        updateFilterButton();
        refresh();
    }

    private void updateFilterButton() {
        int count = 0;
        count += "Any Cuisine".equals(appliedCuisine) ? 0 : 1;
        count += "Any Price".equals(appliedPrice) ? 0 : 1;
        count += appliedDistance.isEmpty() ? 0 : 1;
        filterButton.setText(count == 0 ? "☷  Filter" : "☷  Filter  " + count);
    }

    private void refresh() {
        placeList.getChildren().clear();
        List<FoodPlace> places = manager.search(criteria());
        if (places.isEmpty()) {
            Label icon = new Label("⌕");
            icon.getStyleClass().add("empty-icon");
            Label empty = new Label("No saved places match this view.");
            empty.getStyleClass().add("empty");
            Label hint = new Label("Try another search or clear the current filters.");
            hint.getStyleClass().add("muted");
            VBox emptyState = new VBox(8, icon, empty, hint);
            emptyState.getStyleClass().add("empty-state");
            placeList.getChildren().add(emptyState);
            return;
        }
        places.forEach(place -> placeList.getChildren().add(rowFor(place)));
    }

    private BorderPane rowFor(FoodPlace place) {
        BorderPane row = new BorderPane();
        row.getStyleClass().add("place-row");
        row.setFocusTraversable(true);
        row.setAccessibleRole(AccessibleRole.BUTTON);
        row.setAccessibleText("View details for " + place.getName());
        row.setOnMouseClicked(event -> app.showPlaceDetails(place));
        row.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                app.showPlaceDetails(place);
            }
        });

        Label icon = new Label(iconFor(place.getCuisine()));
        icon.getStyleClass().add("cuisine-icon");
        Label name = new Label(place.getName());
        name.getStyleClass().add("place-name");
        Label details = new Label(place.getCuisine() + "  ·  " + place.getPriceRange().getLabel() + "  ·  "
                + formatDistance(place.getDistanceKm()) + " km  ·  " + stars(place.getRating()));
        details.getStyleClass().add("place-detail");
        details.setWrapText(true);
        VBox identity = new VBox(5, name, details);
        identity.setMinWidth(0);
        HBox summary = new HBox(14, icon, identity);
        summary.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(identity, Priority.ALWAYS);

        VBox content = new VBox(8, summary);
        if (!place.getTags().isEmpty()) {
            Label tags = new Label(String.join("  ·  ", place.getTags()));
            tags.getStyleClass().add("tag-line");
            tags.setWrapText(true);
            content.getChildren().add(tags);
        }

        Button edit = iconButton("✎", "Edit " + place.getName());
        Button delete = iconButton("×", "Delete " + place.getName());
        delete.getStyleClass().add("danger-icon-button");
        edit.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
        delete.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
        edit.setOnAction(event -> app.showEditPlace(place));
        delete.setOnAction(event -> delete(place));
        HBox actions = new HBox(7, edit, delete);
        actions.setAlignment(Pos.CENTER_RIGHT);

        row.setCenter(content);
        row.setRight(actions);
        BorderPane.setMargin(actions, new Insets(0, 0, 0, 18));
        return row;
    }

    private Button iconButton(String icon, String tooltip) {
        Button button = new Button(icon);
        button.getStyleClass().add("row-icon-button");
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }

    private String iconFor(String cuisine) {
        if ("Japanese".equalsIgnoreCase(cuisine)) {
            return "寿";
        }
        if ("Italian".equalsIgnoreCase(cuisine)) {
            return "IT";
        }
        if ("Mexican".equalsIgnoreCase(cuisine)) {
            return "MX";
        }
        if ("Chinese".equalsIgnoreCase(cuisine)) {
            return "中";
        }
        if ("Korean".equalsIgnoreCase(cuisine)) {
            return "KR";
        }
        return "FD";
    }

    private String stars(int rating) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            builder.append(i <= rating ? "★" : "☆");
        }
        return builder.toString();
    }

    private String formatDistance(double distance) {
        if (distance == Math.rint(distance)) {
            return String.valueOf((int) distance);
        }
        return String.format("%.1f", distance);
    }

    private void pickRandom() {
        Optional<FoodPlace> picked = picker.pick(manager.getPlaces(), criteria());
        if (picked.isEmpty()) {
            AppDialog.showInfo(placeList, "↯", "No Match", "No places match the current picker filters.");
            return;
        }
        FoodPlace place = picked.get();
        AppDialog.showInfo(placeList, "★", "Random Pick",
                "Try " + place.getName() + "\n" + place.getPriceRange().getLabel() + " · "
                        + formatDistance(place.getDistanceKm()) + " km · " + stars(place.getRating()));
    }

    private void delete(FoodPlace place) {
        if (!AppDialog.confirm(placeList, "×", "Delete Place", "Delete " + place.getName() + "?", "Delete")) {
            return;
        }
        try {
            manager.delete(place.getId());
            refresh();
        } catch (IOException exception) {
            AppDialog.showInfo(placeList, "!", "Could Not Delete", exception.getMessage());
        }
    }

    private FilterCriteria criteria() {
        return new FilterCriteria(appliedQuery, appliedCuisine, appliedPrice, appliedDistance);
    }
}
