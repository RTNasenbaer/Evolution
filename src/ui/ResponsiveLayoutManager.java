package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Manages responsive layout and dynamic window sizing for the application.
 * Handles screen size detection, window resizing, and component scaling.
 */
public class ResponsiveLayoutManager {

    private Stage stage;
    private double minWidth = 800;
    private double minHeight = 600;
    private double maxWidthRatio = 0.9;
    private double maxHeightRatio = 0.85;

    // Layout breakpoints
    public static final double SMALL_SCREEN_WIDTH = 1024;
    public static final double MEDIUM_SCREEN_WIDTH = 1366;
    public static final double LARGE_SCREEN_WIDTH = 1920;

    public ResponsiveLayoutManager(Stage stage) {
        this.stage = stage;
        setupResponsiveLayout();
    }

    private void setupResponsiveLayout() {
        // Get screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calculate optimal window size
        double optimalWidth = Math.min(screenBounds.getWidth() * maxWidthRatio,
                calculateOptimalWidth(screenBounds.getWidth()));
        double optimalHeight = Math.min(screenBounds.getHeight() * maxHeightRatio,
                calculateOptimalHeight(screenBounds.getHeight()));

        // Set window size constraints
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setWidth(optimalWidth);
        stage.setHeight(optimalHeight);

        // Enable maximizing
        stage.setMaximized(false);

        // Center window on screen
        stage.setX((screenBounds.getWidth() - optimalWidth) / 2);
        stage.setY((screenBounds.getHeight() - optimalHeight) / 2);

        // Add responsive listeners
        addResponsiveListeners();
    }

    private double calculateOptimalWidth(double screenWidth) {
        if (screenWidth <= SMALL_SCREEN_WIDTH) {
            return 1000;
        } else if (screenWidth <= MEDIUM_SCREEN_WIDTH) {
            return 1200;
        } else if (screenWidth <= LARGE_SCREEN_WIDTH) {
            return 1400;
        } else {
            return 1600;
        }
    }

    private double calculateOptimalHeight(double screenHeight) {
        // Maintain 16:10 aspect ratio approximately
        return calculateOptimalWidth(Screen.getPrimary().getVisualBounds().getWidth()) * 0.625 + 100;
    }

    private void addResponsiveListeners() {
        // Listen for window size changes
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                onWidthChanged(newValue.doubleValue());
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                onHeightChanged(newValue.doubleValue());
            }
        });

        // Listen for maximized state changes
        stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                onMaximizedChanged(newValue);
            }
        });
    }

    private void onWidthChanged(double newWidth) {
        // Responsive behavior can be implemented here
        // For example, adjusting component sizes based on window width
    }

    private void onHeightChanged(double newHeight) {
        // Responsive behavior can be implemented here
        // For example, adjusting component sizes based on window height
    }

    private void onMaximizedChanged(boolean isMaximized) {
        // Handle maximized state change
        // Component sizes will be automatically recalculated by width/height listeners
        System.out.println("Window " + (isMaximized ? "maximized" : "restored"));
    }

    public ScreenSize getScreenSize() {
        double width = Screen.getPrimary().getVisualBounds().getWidth();
        if (width <= SMALL_SCREEN_WIDTH) {
            return ScreenSize.SMALL;
        } else if (width <= MEDIUM_SCREEN_WIDTH) {
            return ScreenSize.MEDIUM;
        } else if (width <= LARGE_SCREEN_WIDTH) {
            return ScreenSize.LARGE;
        } else {
            return ScreenSize.EXTRA_LARGE;
        }
    }

    public double getOptimalControlPanelWidth() {
        ScreenSize screenSize = getScreenSize();
        switch (screenSize) {
        case SMALL:
            return 300;
        case MEDIUM:
            return 350;
        case LARGE:
            return 400;
        case EXTRA_LARGE:
            return 450;
        default:
            return 350;
        }
    }

    public double getOptimalWorldDisplayWidth() {
        return stage.getWidth() - getOptimalControlPanelWidth() - (AppStyles.SPACING_LARGE * 3);
    }

    public double getOptimalWorldDisplayHeight() {
        double availableHeight = stage.getHeight() - 150; // Account for padding and status bar
        return availableHeight * (stage.isMaximized() ? 0.70 : 0.60); // More space when maximized
    }

    public double getOptimalChartHeight() {
        double availableHeight = stage.getHeight() - 150;
        return availableHeight * (stage.isMaximized() ? 0.25 : 0.30); // Less space when maximized
    }

    public enum ScreenSize {
        SMALL, MEDIUM, LARGE, EXTRA_LARGE
    }

    // Utility methods for responsive design
    public static int getResponsiveSpacing(ScreenSize screenSize) {
        switch (screenSize) {
        case SMALL:
            return AppStyles.SPACING_SMALL;
        case MEDIUM:
            return AppStyles.SPACING_MEDIUM;
        case LARGE:
        case EXTRA_LARGE:
            return AppStyles.SPACING_LARGE;
        default:
            return AppStyles.SPACING_MEDIUM;
        }
    }

    public static int getResponsivePadding(ScreenSize screenSize) {
        switch (screenSize) {
        case SMALL:
            return AppStyles.PADDING_SMALL;
        case MEDIUM:
            return AppStyles.PADDING_MEDIUM;
        case LARGE:
        case EXTRA_LARGE:
            return AppStyles.PADDING_LARGE;
        default:
            return AppStyles.PADDING_MEDIUM;
        }
    }
}