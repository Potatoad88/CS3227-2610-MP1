package com.whatshouldieat.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Creates application-styled informational and confirmation dialogs.
 */
class AppDialog {
    private AppDialog() {
    }

    static void showInfo(Node owner, String icon, String title, String message) {
        Stage dialog = createStage(owner);
        VBox card = createCard(icon, title, message);
        HBox actions = new HBox();
        actions.getStyleClass().add("dialog-actions");
        Button ok = primaryButton("Got it");
        ok.setOnAction(event -> dialog.close());
        actions.getChildren().add(ok);
        card.getChildren().add(actions);
        show(dialog, card);
    }

    static boolean confirm(Node owner, String icon, String title, String message, String confirmText) {
        Stage dialog = createStage(owner);
        final boolean[] confirmed = {false};
        VBox card = createCard(icon, title, message);
        HBox actions = new HBox(10);
        actions.getStyleClass().add("dialog-actions");
        Button cancel = secondaryButton("Cancel");
        Button confirm = primaryButton(confirmText);
        cancel.setOnAction(event -> dialog.close());
        confirm.setOnAction(event -> {
            confirmed[0] = true;
            dialog.close();
        });
        actions.getChildren().addAll(cancel, confirm);
        card.getChildren().add(actions);
        show(dialog, card);
        return confirmed[0];
    }

    private static Stage createStage(Node owner) {
        Window window = owner.getScene() == null ? null : owner.getScene().getWindow();
        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        if (window != null) {
            dialog.initOwner(window);
        }
        dialog.initModality(Modality.WINDOW_MODAL);
        return dialog;
    }

    private static VBox createCard(String icon, String title, String message) {
        VBox card = new VBox(14);
        card.getStyleClass().add("dialog-card");
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("dialog-icon");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(420);
        messageLabel.setMaxWidth(420);
        messageLabel.setMinHeight(Region.USE_PREF_SIZE);
        messageLabel.setTextOverrun(OverrunStyle.CLIP);
        card.getChildren().addAll(iconLabel, titleLabel, messageLabel);
        return card;
    }

    private static void show(Stage dialog, VBox card) {
        StackPane overlay = new StackPane(card);
        overlay.getStyleClass().add("dialog-overlay");
        if (dialog.getOwner() != null && dialog.getOwner().getScene().getRoot().getStyleClass().contains("dark")) {
            overlay.getStyleClass().add("dark");
        }
        overlay.setPadding(new Insets(28));
        Scene scene = new Scene(overlay, 520, 320);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(AppDialog.class.getResource("/styles/app.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static Button primaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        return button;
    }

    private static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        return button;
    }
}
