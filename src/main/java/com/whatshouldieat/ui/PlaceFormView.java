package com.whatshouldieat.ui;

import com.whatshouldieat.logic.PlaceManager;
import com.whatshouldieat.model.FoodPlace;
import com.whatshouldieat.model.PriceRange;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds the shared add/edit form and submits validated place details.
 */
class PlaceFormView {
    private final PlaceManager manager;
    private final AppView app;
    private final FoodPlace editing;
    private final TextField name = new TextField();
    private final ChoiceBox<String> cuisine = new ChoiceBox<>();
    private final ChoiceBox<String> price = new ChoiceBox<>();
    private final HBox ratingStars = new HBox(4);
    private final List<Button> starButtons = new ArrayList<>();
    private int selectedRating = 4;
    private final TextField distanceKm = new TextField();
    private final TextField tags = new TextField();
    private final TextArea notes = new TextArea();

    PlaceFormView(PlaceManager manager, AppView app, FoodPlace editing) {
        this.manager = manager;
        this.app = app;
        this.editing = editing;
    }

    Parent getRoot() {
        VBox page = new VBox(20);
        page.getStyleClass().add("form-page");
        page.setPadding(new Insets(22, 0, 0, 0));

        HBox backRow = new HBox(10);
        backRow.setPadding(new Insets(0, 30, 0, 30));
        Button back = new Button("< Back");
        back.getStyleClass().add("nav-button");
        back.setOnAction(event -> app.showSavedPlaces());
        Label title = new Label(editing == null ? "Add Place" : "Edit Place");
        title.getStyleClass().add("form-route-title");
        backRow.getChildren().addAll(back, title);

        VBox card = new VBox(18);
        card.getStyleClass().add("form-card");
        Label heading = new Label("Restaurant Details");
        heading.getStyleClass().add("form-heading");
        cuisine.getItems().addAll("Japanese", "Italian", "Mexican", "Chinese", "Indian", "Korean", "Thai", "Western", "Local", "Other");
        cuisine.setValue("Japanese");
        price.getItems().addAll("$", "$$", "$$$", "$$$$");
        price.setValue("$$");
        createRatingStars();
        notes.setPrefRowCount(4);
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);
        grid.add(field("Name", name), 0, 0, 2, 1);
        grid.add(field("Cuisine Type", cuisine), 0, 1);
        grid.add(field("Price Range", price), 1, 1);
        grid.add(field("Rating", ratingStars), 0, 2, 2, 1);
        grid.add(field("Distance (km)", distanceKm), 0, 3, 2, 1);
        grid.add(field("Tags (comma separated)", tags), 0, 4, 2, 1);
        grid.add(field("Personal Notes", notes), 0, 5, 2, 1);
        card.getChildren().addAll(heading, grid, actionRow());
        populate();
        VBox cardWrap = new VBox(card);
        cardWrap.getStyleClass().add("form-card-wrap");
        ScrollPane scroll = new ScrollPane(cardWrap);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().addAll("scroll", "form-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        page.getChildren().addAll(backRow, scroll);
        return page;
    }

    private VBox field(String label, javafx.scene.Node control) {
        VBox box = new VBox(7);
        Label fieldLabel = new Label(label);
        fieldLabel.getStyleClass().add("field-label");
        control.getStyleClass().add("field-control");
        box.getChildren().addAll(fieldLabel, control);
        return box;
    }

    private HBox actionRow() {
        HBox row = new HBox(12);
        Button cancel = new Button("Cancel");
        Button save = new Button(editing == null ? "Save Place" : "Update Place");
        cancel.getStyleClass().add("outline-button");
        save.getStyleClass().add("primary-button");
        cancel.setOnAction(event -> app.showSavedPlaces());
        save.setOnAction(event -> save());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(spacer, cancel, save);
        return row;
    }

    private void populate() {
        if (editing == null) {
            return;
        }
        name.setText(editing.getName());
        cuisine.setValue(editing.getCuisine());
        price.setValue(editing.getPriceRange().getLabel());
        selectedRating = editing.getRating();
        updateStars();
        distanceKm.setText(formatDistance(editing.getDistanceKm()));
        tags.setText(String.join(", ", editing.getTags()));
        notes.setText(editing.getNotes());
    }

    private void save() {
        try {
            FoodPlace place = new FoodPlace(name.getText().trim(), cuisine.getValue(), parseDistance(),
                    PriceRange.fromLabel(price.getValue()), selectedRating,
                    parseTags(tags.getText()), notes.getText().trim());
            if (editing == null) {
                manager.add(place);
            } else {
                manager.update(editing.getId(), place);
            }
            app.showSavedPlaces();
        } catch (IllegalArgumentException | IOException exception) {
            AppDialog.showInfo(distanceKm, "!", "Could Not Save", exception.getMessage());
        }
    }

    private List<String> parseTags(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
    }

    private void createRatingStars() {
        ratingStars.getStyleClass().add("star-row");
        for (int i = 1; i <= 5; i++) {
            final int ratingValue = i;
            Button star = new Button();
            star.getStyleClass().add("star-button");
            star.setOnAction(event -> {
                selectedRating = ratingValue;
                updateStars();
            });
            starButtons.add(star);
            ratingStars.getChildren().add(star);
        }
        updateStars();
    }

    private void updateStars() {
        for (int i = 0; i < starButtons.size(); i++) {
            Button star = starButtons.get(i);
            star.setText(i < selectedRating ? "★" : "☆");
            star.getStyleClass().removeAll("star-selected", "star-empty");
            star.getStyleClass().add(i < selectedRating ? "star-selected" : "star-empty");
        }
    }

    private double parseDistance() {
        String value = distanceKm.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Distance is required.");
        }
        try {
            double distance = Double.parseDouble(value);
            if (!Double.isFinite(distance) || distance < 0) {
                throw new IllegalArgumentException("Distance must be a finite number that is zero or greater.");
            }
            return distance;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Distance must be a number.");
        }
    }

    private String formatDistance(double distance) {
        if (distance == Math.rint(distance)) {
            return String.valueOf((int) distance);
        }
        return String.valueOf(distance);
    }
}
