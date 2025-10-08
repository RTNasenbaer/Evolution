package ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Factory utility for creating consistent UI components across all displays.
 * Eliminates duplicate widget creation code in ConfigDisplay and WorldBuilderControls.
 */
public class WidgetFactory {

    /**
     * Creates a styled button with specified text and color
     */
    public static Button createButton(String text, String backgroundColor) {
        Button button = new Button(text);
        button.setStyle(AppStyles.getButtonStyle(backgroundColor));
        return button;
    }

    /**
     * Creates a styled button with specified text, color, and minimum width
     */
    public static Button createButton(String text, String backgroundColor, double minWidth) {
        Button button = createButton(text, backgroundColor);
        button.setStyle(button.getStyle() + " -fx-min-width: " + minWidth + ";");
        return button;
    }

    /**
     * Creates a primary button (info color)
     */
    public static Button createPrimaryButton(String text) {
        return createButton(text, AppStyles.INFO_COLOR);
    }

    /**
     * Creates a success button (green color)
     */
    public static Button createSuccessButton(String text) {
        return createButton(text, AppStyles.SUCCESS_COLOR);
    }

    /**
     * Creates a warning button (orange color)
     */
    public static Button createWarningButton(String text) {
        return createButton(text, AppStyles.WARNING_COLOR);
    }

    /**
     * Creates a danger button (red color)
     */
    public static Button createDangerButton(String text) {
        return createButton(text, AppStyles.DANGER_COLOR);
    }

    /**
     * Creates a secondary button (dark gray color)
     */
    public static Button createSecondaryButton(String text) {
        return createButton(text, AppStyles.SECONDARY_COLOR);
    }

    /**
     * Creates a styled text field with placeholder and width
     */
    public static TextField createTextField(String placeholder, double width) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setPrefWidth(width);
        textField.setStyle(AppStyles.getTextFieldStyle());
        return textField;
    }

    /**
     * Creates a styled text field with placeholder (default width)
     */
    public static TextField createTextField(String placeholder) {
        return createTextField(placeholder, 100);
    }

    /**
     * Creates a section header label
     */
    public static Label createSectionHeader(String text) {
        Label label = new Label(text);
        label.setStyle(AppStyles.getSectionHeaderStyle());
        return label;
    }

    /**
     * Creates a regular label
     */
    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle(AppStyles.getLabelStyle());
        return label;
    }

    /**
     * Creates a title label
     */
    public static Label createTitle(String text) {
        Label label = new Label(text);
        label.setStyle(AppStyles.getTitleStyle());
        return label;
    }

    /**
     * Creates a VBox section container with standard spacing
     */
    public static VBox createSection() {
        VBox section = new VBox(AppStyles.SPACING_SMALL);
        return section;
    }

    /**
     * Creates a VBox section with header label
     */
    public static VBox createSection(String headerText) {
        VBox section = createSection();
        section.getChildren().add(createSectionHeader(headerText));
        return section;
    }

    /**
     * Creates a horizontal box for controls with standard spacing
     */
    public static HBox createControlRow() {
        return new HBox(8);
    }

    /**
     * Creates a labeled horizontal control row
     */
    public static HBox createLabeledControlRow(String labelText) {
        HBox row = createControlRow();
        row.getChildren().add(createLabel(labelText));
        return row;
    }

    /**
     * Creates a styled card container
     */
    public static VBox createCard() {
        VBox card = new VBox(AppStyles.SPACING_MEDIUM);
        card.setStyle(AppStyles.getCardStyle());
        return card;
    }

    /**
     * Creates a styled card with title
     */
    public static VBox createCard(String title) {
        VBox card = createCard();
        card.getChildren().add(createTitle(title));
        return card;
    }

    /**
     * Creates a styled card with title and preferred width
     */
    public static VBox createCard(String title, double width) {
        VBox card = createCard(title);
        card.setPrefWidth(width);
        return card;
    }
}
