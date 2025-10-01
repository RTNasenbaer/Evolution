package ui;

import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;

/**
 * Handles zoom and pan functionality for world displays.
 * Provides:
 * - Ctrl+Scroll for zooming
 * - Space+Drag for panning
 */
public class ZoomPanHandler {
    private final Region targetNode;
    private final ScrollPane scrollPane;
    private double zoomFactor = 1.0;
    private final double minZoom = 0.1;
    private final double maxZoom = 10.0;
    private final double zoomStep = 0.1;

    // Pan variables
    private boolean isPanning = false;
    private double lastPanX = 0;
    private double lastPanY = 0;

    // Transform
    private Scale scaleTransform;
    private Group zoomGroup;

    public ZoomPanHandler(Region targetNode, ScrollPane scrollPane) {
        this.targetNode = targetNode;
        this.scrollPane = scrollPane;
        this.scaleTransform = new Scale(1.0, 1.0);

        setupZoomGroup();
        setupEventHandlers();
    }

    private void setupZoomGroup() {
        // Create a group to wrap the target node for zoom functionality
        zoomGroup = new Group();
        zoomGroup.getChildren().add(targetNode);
        zoomGroup.getTransforms().add(scaleTransform);

        // Set the zoom group as the scroll pane content
        scrollPane.setContent(zoomGroup);

        // Configure scroll pane for better zoom/pan experience
        scrollPane.setPannable(false); // We handle panning ourselves
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
    }

    private void setupEventHandlers() {
        // Zoom with Ctrl+Scroll
        scrollPane.setOnScroll(this::handleScroll);

        // Pan with Space+Drag
        scrollPane.setOnMousePressed(this::handleMousePressed);
        scrollPane.setOnMouseDragged(this::handleMouseDragged);
        scrollPane.setOnMouseReleased(this::handleMouseReleased);

        // Focus for key events
        scrollPane.setFocusTraversable(true);
    }

    private void handleScroll(ScrollEvent event) {
        // Only zoom if Ctrl is pressed
        if (event.isControlDown()) {
            event.consume();

            double deltaY = event.getDeltaY();
            double scaleFactor = (deltaY > 0) ? (1 + zoomStep) : (1 - zoomStep);

            double newZoom = zoomFactor * scaleFactor;
            newZoom = Math.max(minZoom, Math.min(maxZoom, newZoom));

            if (newZoom != zoomFactor) {
                // Calculate zoom center point
                double pivotX = event.getX();
                double pivotY = event.getY();

                // Apply zoom
                setZoom(newZoom, pivotX, pivotY);
            }
        }
    }

    private void handleMousePressed(MouseEvent event) {
        // Start panning only if Space is pressed
        if (event.getSource() == scrollPane && isSpacePressed()) {
            isPanning = true;
            lastPanX = event.getScreenX();
            lastPanY = event.getScreenY();
            scrollPane.setCursor(javafx.scene.Cursor.MOVE);
            event.consume();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isPanning && isSpacePressed()) {
            double deltaX = event.getScreenX() - lastPanX;
            double deltaY = event.getScreenY() - lastPanY;

            // Update scroll pane position
            double newHValue = scrollPane.getHvalue() - (deltaX / scrollPane.getWidth()) * 2;
            double newVValue = scrollPane.getVvalue() - (deltaY / scrollPane.getHeight()) * 2;

            scrollPane.setHvalue(Math.max(0, Math.min(1, newHValue)));
            scrollPane.setVvalue(Math.max(0, Math.min(1, newVValue)));

            lastPanX = event.getScreenX();
            lastPanY = event.getScreenY();
            event.consume();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (isPanning) {
            isPanning = false;
            scrollPane.setCursor(javafx.scene.Cursor.DEFAULT);
            event.consume();
        }
    }

    private boolean spacePressed = false;

    private boolean isSpacePressed() {
        return spacePressed;
    }

    public void setSpacePressed(boolean pressed) {
        this.spacePressed = pressed;
    }

    private void setZoom(double newZoom, double pivotX, double pivotY) {
        double oldZoom = zoomFactor;
        zoomFactor = newZoom;

        // Update scale transform
        scaleTransform.setX(zoomFactor);
        scaleTransform.setY(zoomFactor);

        // Adjust scroll position to keep zoom centered on cursor
        if (pivotX > 0 && pivotY > 0) {
            double zoomRatio = newZoom / oldZoom;

            // Calculate new scroll positions
            double currentHValue = scrollPane.getHvalue();
            double currentVValue = scrollPane.getVvalue();

            // Adjust for zoom center
            double adjustH = (pivotX / scrollPane.getWidth() - 0.5) * (zoomRatio - 1) * 0.5;
            double adjustV = (pivotY / scrollPane.getHeight() - 0.5) * (zoomRatio - 1) * 0.5;

            scrollPane.setHvalue(Math.max(0, Math.min(1, currentHValue + adjustH)));
            scrollPane.setVvalue(Math.max(0, Math.min(1, currentVValue + adjustV)));
        }
    }

    public void resetZoom() {
        setZoom(1.0, scrollPane.getWidth() / 2, scrollPane.getHeight() / 2);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
    }

    public void zoomIn() {
        double newZoom = Math.min(maxZoom, zoomFactor * (1 + zoomStep));
        setZoom(newZoom, scrollPane.getWidth() / 2, scrollPane.getHeight() / 2);
    }

    public void zoomOut() {
        double newZoom = Math.max(minZoom, zoomFactor * (1 - zoomStep));
        setZoom(newZoom, scrollPane.getWidth() / 2, scrollPane.getHeight() / 2);
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

}