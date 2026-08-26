package com.whatshouldieat.ui;

import com.whatshouldieat.logic.PlaceManager;
import com.whatshouldieat.storage.JsonPlaceStorage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Paths;

/**
 * JavaFX entry point for the What Should I Eat desktop application.
 */
public class WhatShouldIEatApp extends Application {
    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        JsonPlaceStorage storage = new JsonPlaceStorage(Paths.get("data", "places.json"));
        PlaceManager manager = new PlaceManager(storage);
        AppView appView = new AppView(manager);
        Scene scene = new Scene(appView.getRoot(), 1120, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        stage.setTitle("What Should I Eat?");
        stage.setMinWidth(720);
        stage.setMinHeight(480);
        stage.setScene(scene);
        stage.show();
        storage.getRecoveredFile().ifPresent(backup -> AppDialog.showInfo(appView.getRoot(), "!",
                "Saved Data Could Not Be Loaded",
                "The saved-places file was invalid, so the app started with an empty list. "
                        + "The original file was preserved at " + backup.toAbsolutePath() + "."));
    }
}
