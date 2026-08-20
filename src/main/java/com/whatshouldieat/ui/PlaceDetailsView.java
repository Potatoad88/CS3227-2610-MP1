package com.whatshouldieat.ui;

import com.whatshouldieat.model.FoodPlace;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Displays all saved information for one food place. */
class PlaceDetailsView {
    private final AppView app;
    private final FoodPlace place;

    PlaceDetailsView(AppView app, FoodPlace place) {
        this.app = app;
        this.place = place;
    }

    Parent getRoot() {
        VBox page = new VBox(20);
        page.getStyleClass().add("form-page");
        page.setPadding(new Insets(22, 0, 0, 0));

        Button back = new Button("< Back");
        back.getStyleClass().add("nav-button");
        back.setOnAction(event -> app.showSavedPlaces());
        Label routeTitle = new Label("Place Details");
        routeTitle.getStyleClass().add("form-route-title");
        HBox backRow = new HBox(10, back, routeTitle);
        backRow.setPadding(new Insets(0, 30, 0, 30));

        VBox card = new VBox(18);
        card.getStyleClass().addAll("form-card", "details-card");
        Label heading = new Label(place.getName());
        heading.getStyleClass().add("form-heading");
        card.getChildren().addAll(
                heading,
                field("Cuisine Type", place.getCuisine()),
                field("Price Range", place.getPriceRange().getLabel()),
                field("Rating", stars(place.getRating())),
                field("Distance", formatDistance(place.getDistanceKm()) + " km"),
                field("Tags", place.getTags().isEmpty() ? "No tags." : String.join(", ", place.getTags())),
                field("Personal Notes", place.getNotes().isBlank() ? "No personal notes." : place.getNotes()),
                actions());

        VBox cardWrap = new VBox(card);
        cardWrap.getStyleClass().add("form-card-wrap");
        ScrollPane scroll = new ScrollPane(cardWrap);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().addAll("scroll", "form-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        page.getChildren().addAll(backRow, scroll);
        return page;
    }

    private VBox field(String title, String value) {
        Label label = new Label(title);
        label.getStyleClass().add("field-label");
        Label content = new Label(value);
        content.getStyleClass().add("detail-value");
        content.setWrapText(true);
        content.setMaxWidth(Double.MAX_VALUE);
        return new VBox(7, label, content);
    }

    private Node actions() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button edit = new Button("Edit Place");
        edit.getStyleClass().add("primary-button");
        edit.setOnAction(event -> app.showEditPlace(place));
        return new HBox(spacer, edit);
    }

    private String stars(int rating) {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }

    private String formatDistance(double distance) {
        return distance == Math.rint(distance) ? String.valueOf((int) distance) : String.valueOf(distance);
    }
}
