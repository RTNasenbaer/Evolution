package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;

/**
 * Utility class for creating standardized dialog windows and pop-ups
 * across the Evolution application.
 */
public class DialogUtils {

    /**
     * Shows a simple information alert with styled content
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        styleDialog(alert);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows an error alert with styled content
     */
    public static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        styleDialog(alert);
        alert.setTitle(title);
        alert.setHeaderText("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a success alert with styled content
     */
    public static void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        styleDialog(alert);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a text input dialog with styled content
     */
    public static String showTextInput(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        styleDialog(dialog);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        java.util.Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Shows a text input dialog without a default value
     */
    public static String showTextInput(String title, String header, String content) {
        return showTextInput(title, header, content, "");
    }

    /**
     * Shows a copyable text dialog with a "Copy to Clipboard" button
     * Perfect for long strings like seed values
     */
    public static void showCopyableText(String title, String header, String labelText, String copyableText) {
        Dialog<Void> dialog = new Dialog<>();
        styleDialog(dialog);
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // Create content area
        VBox content = new VBox(AppStyles.SPACING_MEDIUM);
        content.setPadding(new Insets(AppStyles.PADDING_MEDIUM));

        // Label
        if (labelText != null && !labelText.isEmpty()) {
            Label label = new Label(labelText);
            label.setStyle(AppStyles.getLabelStyle());
            label.setWrapText(true);
            content.getChildren().add(label);
        }

        // Text area with copyable text
        TextArea textArea = new TextArea(copyableText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(8);
        textArea.setPrefColumnCount(50);
        textArea.setStyle(AppStyles.getTextFieldStyle() +
                " -fx-font-family: 'Consolas', 'Courier New', monospace;" +
                " -fx-font-size: 11px;");

        // Select all text by default for easy copying
        textArea.selectAll();

        content.getChildren().add(textArea);

        // Copy button
        Button copyButton = new Button("📋 Copy to Clipboard");
        copyButton.setStyle(AppStyles.getPrimaryButtonStyle());
        copyButton.setOnAction(e -> {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(copyableText);
            Clipboard.getSystemClipboard().setContent(clipboardContent);

            // Visual feedback
            copyButton.setText("✓ Copied!");
            copyButton.setStyle(AppStyles.getSuccessButtonStyle());

            // Reset button after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        copyButton.setText("📋 Copy to Clipboard");
                        copyButton.setStyle(AppStyles.getPrimaryButtonStyle());
                    });
                } catch (InterruptedException ignored) {
                }
            }).start();
        });

        content.getChildren().add(copyButton);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Make close button styled
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        if (closeButton != null) {
            closeButton.setStyle(AppStyles.getSecondaryButtonStyle());
        }

        dialog.showAndWait();
    }

    /**
     * Shows a confirmation dialog
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        styleDialog(alert);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a custom dialog with provided content
     */
    public static <T> Dialog<T> createCustomDialog(String title, String header) {
        Dialog<T> dialog = new Dialog<>();
        styleDialog(dialog);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        return dialog;
    }

    /**
     * Applies consistent styling to any dialog
     * Made public to allow styling custom dialogs from outside this class
     */
    public static void styleDialog(Dialog<? > dialog) {
        DialogPane dialogPane = dialog.getDialogPane();

        // Style the dialog pane
        dialogPane.setStyle(
                "-fx-background-color: " + AppStyles.BACKGROUND_COLOR + ";" +
                        "-fx-border-color: " + AppStyles.INFO_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;");

        // Style header - check if it exists first to avoid NullPointerException
        javafx.scene.Node headerPanel = dialogPane.lookup(".header-panel");
        if (headerPanel != null) {
            headerPanel.setStyle(
                    "-fx-background-color: " + AppStyles.CARD_COLOR + ";" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: " + AppStyles.PRIMARY_COLOR + ";");
        }

        // Style content - check if it exists first to avoid NullPointerException
        javafx.scene.Node contentNode = dialogPane.lookup(".content");
        if (contentNode != null) {
            contentNode.setStyle(
                    "-fx-font-size: 12px;" +
                            "-fx-text-fill: " + AppStyles.MUTED_COLOR + ";");
        }

        // Style buttons
        for (ButtonType buttonType : dialogPane.getButtonTypes()) {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            if (button != null) {
                button.setStyle(AppStyles.getPrimaryButtonStyle());
            }
        }
    }

    /**
     * Shows a progress dialog that doesn't block
     */
    public static ProgressDialog showProgress(String title, String header) {
        return new ProgressDialog(title, header);
    }

    /**
     * Custom progress dialog wrapper
     */
    public static class ProgressDialog {
        private Alert alert;
        private Label messageLabel;
        private ProgressBar progressBar;

        private ProgressDialog(String title, String header) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            styleDialog(alert);
            alert.setTitle(title);
            alert.setHeaderText(header);

            VBox content = new VBox(AppStyles.SPACING_MEDIUM);
            content.setPadding(new Insets(AppStyles.PADDING_MEDIUM));

            messageLabel = new Label("Starting...");
            messageLabel.setStyle(AppStyles.getLabelStyle());

            progressBar = new ProgressBar(0);
            progressBar.setPrefWidth(300);
            progressBar.setStyle(
                    "-fx-accent: " + AppStyles.INFO_COLOR + ";");

            content.getChildren().addAll(messageLabel, progressBar);
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().getButtonTypes().clear();
        }

        public void show() {
            alert.show();
        }

        public void updateProgress(double progress, String message) {
            javafx.application.Platform.runLater(() -> {
                progressBar.setProgress(progress);
                messageLabel.setText(message);
            });
        }

        public void close() {
            javafx.application.Platform.runLater(() -> alert.close());
        }
    }
}
