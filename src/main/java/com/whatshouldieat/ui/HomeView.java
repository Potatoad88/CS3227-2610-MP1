package com.whatshouldieat.ui;

import com.whatshouldieat.logic.FilterCriteria;
import com.whatshouldieat.logic.PlaceManager;
import com.whatshouldieat.logic.RandomPicker;
import com.whatshouldieat.model.FoodPlace;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Builds the landing page and its unfiltered random-place action.
 */
class HomeView {
    private final PlaceManager manager;
    private final AppView app;
    private final RandomPicker picker = new RandomPicker();

    HomeView(PlaceManager manager, AppView app) {
        this.manager = manager;
        this.app = app;
    }

    Parent getRoot() {
        VBox page = new VBox(56);
        page.getStyleClass().add("home-page");
        page.setPadding(new Insets(72, 32, 40, 32));

        HBox hero = new HBox(52);
        hero.getStyleClass().add("hero");
        VBox copy = new VBox(18);
        HBox.setHgrow(copy, Priority.ALWAYS);
        Label badge = new Label("Culinary Utility System");
        badge.getStyleClass().add("badge");
        Label title = new Label("Your Personal\nCulinary Compass");
        title.getStyleClass().add("hero-title");
        Label body = new Label("A personal desktop tool to manage, curate, and discover saved food places. Stop wondering what's for dinner and start exploring your list.");
        body.getStyleClass().add("hero-body");
        body.setWrapText(true);
        Button pickerButton = new Button("↯ Random Craving Picker");
        pickerButton.getStyleClass().add("primary-button");
        pickerButton.setOnAction(event -> pickRandom());
        copy.getChildren().addAll(badge, title, body, pickerButton);

        VBox visual = new VBox(14);
        visual.getStyleClass().add("hero-visual");
        visual.getChildren().addAll(visualBlock("⌘", "Saved Places", manager.getPlaces().size() + " places ready"),
                visualBlock("★", "Taste Profile", "Price, cuisine, rating, distance"),
                visualBlock("↯", "Random Picker", "Offline and instant"));
        hero.getChildren().addAll(copy, visual);

        GridPane features = new GridPane();
        features.setHgap(14);
        features.setVgap(14);
        features.add(feature("⌘", "Curated Food List", "Add, update, and organize restaurants with cuisine, price, distance, rating, tags, and notes."), 0, 0);
        features.add(feature("↯", "Random Craving Generator", "Let the app select a matching food place from your curated list when you cannot decide."), 1, 0);
        features.add(feature("◎", "Distance Aware", "Keep simple distance values now, with room for future map-powered automation later."), 2, 0);
        for (int i = 0; i < 3; i++) {
            javafx.scene.layout.ColumnConstraints column = new javafx.scene.layout.ColumnConstraints();
            column.setPercentWidth(33.3);
            features.getColumnConstraints().add(column);
        }

        page.getChildren().addAll(hero, features);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().addAll("scroll", "page-scroll");
        return scroll;
    }

    private VBox visualBlock(String icon, String title, String body) {
        VBox card = new VBox(6);
        card.getStyleClass().add("mini-card");
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("mini-icon");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("mini-title");
        Label bodyLabel = new Label(body);
        bodyLabel.getStyleClass().add("muted");
        card.getChildren().addAll(iconLabel, titleLabel, bodyLabel);
        return card;
    }

    private VBox feature(String iconText, String title, String text) {
        VBox card = new VBox(14);
        card.getStyleClass().add("feature-card");
        Label icon = new Label(iconText);
        icon.getStyleClass().add("feature-icon");
        Label heading = new Label(title);
        heading.getStyleClass().add("feature-title");
        heading.setWrapText(true);
        heading.setMaxWidth(Double.MAX_VALUE);
        Label body = new Label(text);
        body.setWrapText(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.getStyleClass().add("feature-body");
        card.getChildren().addAll(icon, heading, body);
        return card;
    }

    private void pickRandom() {
        Optional<FoodPlace> picked = picker.pick(manager.getPlaces(), new FilterCriteria(""));
        if (picked.isEmpty()) {
            AppDialog.showInfo(app.getRoot(), "↯", "Nothing to pick yet", "Add at least one food place first.");
            return;
        }
        FoodPlace place = picked.get();
        AppDialog.showInfo(app.getRoot(), "★", "Tonight's Pick",
                place.getName() + "\n" + place.getCuisine() + " · "
                        + place.getPriceRange().getLabel() + " · " + place.getDistanceKm() + " km away");
    }
}
