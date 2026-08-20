package com.whatshouldieat.ui;

import com.whatshouldieat.logic.PlaceManager;
import com.whatshouldieat.model.FoodPlace;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.prefs.Preferences;

/**
 * Owns the application's navigation shell and switches between page views.
 * The selected light or dark theme is restored from user preferences.
 */
public class AppView {
    private static final String DARK_MODE = "darkMode";

    private final PlaceManager manager;
    private final BorderPane root = new BorderPane();
    private final Preferences preferences = Preferences.userNodeForPackage(AppView.class);

    /**
     * Creates the application shell backed by the supplied place manager.
     *
     * @param manager manager providing saved-place operations
     */
    public AppView(PlaceManager manager) {
        this.manager = manager;
        root.getStyleClass().add("app-root");
        setDarkMode(preferences.getBoolean(DARK_MODE, false));
        root.setTop(createNav());
        showHome();
    }

    /**
     * Returns the root node displayed in the application scene.
     *
     * @return application root
     */
    public Parent getRoot() {
        return root;
    }

    /** Displays the home page. */
    public void showHome() {
        root.setCenter(new HomeView(manager, this).getRoot());
    }

    /** Displays the saved-places page. */
    public void showSavedPlaces() {
        root.setCenter(new SavedPlacesView(manager, this).getRoot());
    }

    /** Displays an empty form for adding a food place. */
    public void showAddPlace() {
        root.setCenter(new PlaceFormView(manager, this, null).getRoot());
    }

    /**
     * Displays a form populated with an existing food place.
     *
     * @param place place to edit
     */
    public void showEditPlace(FoodPlace place) {
        root.setCenter(new PlaceFormView(manager, this, place).getRoot());
    }

    private HBox createNav() {
        Label brand = new Label("What Should I Eat?");
        brand.getStyleClass().add("brand");
        Button home = navButton("Home");
        Button saved = navButton("Saved Places");
        home.setOnAction(event -> showHome());
        saved.setOnAction(event -> showSavedPlaces());
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        ToggleButton theme = new ToggleButton();
        theme.getStyleClass().add("theme-toggle");
        theme.setSelected(root.getStyleClass().contains("dark"));
        theme.setTooltip(new Tooltip("Toggle dark mode"));
        theme.setAccessibleText("Toggle dark mode");
        updateThemeIcon(theme);
        theme.setOnAction(event -> {
            setDarkMode(theme.isSelected());
            preferences.putBoolean(DARK_MODE, theme.isSelected());
            updateThemeIcon(theme);
        });
        HBox nav = new HBox(28, brand, home, saved, spacer, theme);
        nav.getStyleClass().add("nav");
        return nav;
    }

    private Button navButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        return button;
    }

    private void setDarkMode(boolean dark) {
        root.getStyleClass().remove("dark");
        if (dark) {
            root.getStyleClass().add("dark");
        }
    }

    private void updateThemeIcon(ToggleButton theme) {
        theme.setText(theme.isSelected() ? "☀" : "☾");
    }
}
