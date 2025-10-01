package ui;

/**
 * Centralized styling constants and methods for the Evolution application.
 * Provides consistent colors, fonts, spacing, and component styles.
 */
public class AppStyles {

    // Color palette
    public static final String PRIMARY_COLOR = "#2c3e50";
    public static final String SECONDARY_COLOR = "#34495e";
    public static final String SUCCESS_COLOR = "#27ae60";
    public static final String WARNING_COLOR = "#f39c12";
    public static final String DANGER_COLOR = "#e74c3c";
    public static final String INFO_COLOR = "#3498db";
    public static final String LIGHT_COLOR = "#ecf0f1";
    public static final String MUTED_COLOR = "#7f8c8d";
    public static final String BORDER_COLOR = "#bdc3c7";
    public static final String BACKGROUND_COLOR = "#f8f9fa";
    public static final String CARD_COLOR = "#ffffff";

    // Typography
    public static final String FONT_SIZE_LARGE = "18px";
    public static final String FONT_SIZE_MEDIUM = "14px";
    public static final String FONT_SIZE_SMALL = "12px";
    public static final String FONT_SIZE_TINY = "10px";

    // Spacing
    public static final int SPACING_LARGE = 20;
    public static final int SPACING_MEDIUM = 15;
    public static final int SPACING_SMALL = 10;
    public static final int SPACING_TINY = 5;
    public static final int PADDING_LARGE = 20;
    public static final int PADDING_MEDIUM = 15;
    public static final int PADDING_SMALL = 10;

    // Dimensions
    public static final int BORDER_RADIUS = 8;
    public static final int BUTTON_RADIUS = 5;
    public static final double SHADOW_RADIUS = 5.0;

    // Component styles
    public static String getCardStyle() {
        return String.format(
                "-fx-background-color: %s; -fx-padding: %d; " +
                        "-fx-background-radius: %d; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), %.1f, 0, 0, 2);",
                CARD_COLOR, PADDING_LARGE, BORDER_RADIUS, SHADOW_RADIUS);
    }

    public static String getTitleStyle() {
        return String.format(
                "-fx-font-size: %s; -fx-font-weight: bold; -fx-text-fill: %s;",
                FONT_SIZE_LARGE, PRIMARY_COLOR);
    }

    public static String getSectionHeaderStyle() {
        return String.format(
                "-fx-font-size: %s; -fx-font-weight: bold; -fx-text-fill: %s;",
                FONT_SIZE_MEDIUM, SECONDARY_COLOR);
    }

    public static String getLabelStyle() {
        return String.format(
                "-fx-font-size: %s; -fx-text-fill: %s;",
                FONT_SIZE_SMALL, MUTED_COLOR);
    }

    public static String getButtonStyle(String backgroundColor) {
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: %s; " +
                        "-fx-background-radius: %d; -fx-border-radius: %d; -fx-padding: 8 12; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 2, 0, 0, 1);",
                backgroundColor, FONT_SIZE_SMALL, BUTTON_RADIUS, BUTTON_RADIUS);
    }

    public static String getTextFieldStyle() {
        return String.format(
                "-fx-background-radius: %d; -fx-border-radius: %d; -fx-padding: 5; -fx-font-size: %s; " +
                        "-fx-border-color: %s; -fx-border-width: 1;",
                BUTTON_RADIUS, BUTTON_RADIUS, FONT_SIZE_SMALL, BORDER_COLOR);
    }

    public static String getStatusBarStyle() {
        return String.format(
                "-fx-background-color: %s; -fx-padding: %d %d; " +
                        "-fx-background-radius: %d; -fx-alignment: center-left;",
                SECONDARY_COLOR, SPACING_SMALL, PADDING_MEDIUM, BUTTON_RADIUS);
    }

    public static String getStatusTextStyle() {
        return String.format(
                "-fx-text-fill: %s; -fx-font-size: %s;",
                LIGHT_COLOR, FONT_SIZE_SMALL);
    }

    public static String getBackgroundStyle() {
        return String.format("-fx-background-color: %s;", BACKGROUND_COLOR);
    }

    // Button color variants
    public static String getPrimaryButtonStyle() {
        return getButtonStyle(INFO_COLOR);
    }

    public static String getSuccessButtonStyle() {
        return getButtonStyle(SUCCESS_COLOR);
    }

    public static String getWarningButtonStyle() {
        return getButtonStyle(WARNING_COLOR);
    }

    public static String getDangerButtonStyle() {
        return getButtonStyle(DANGER_COLOR);
    }

    public static String getSecondaryButtonStyle() {
        return getButtonStyle(SECONDARY_COLOR);
    }
}