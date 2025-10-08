package ui;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import world.Type;

/**
 * Component responsible for displaying population charts and graphs.
 * Dynamically creates series only for biomes that exist in the world.
 */
public class GraphDisplay {
    private VBox container;
    private LineChart<Number, Number> lineChart;
    private Map<Type, XYChart.Series<Number, Number>> seriesMap;
    private Map<Type, Boolean> biomeDetected;

    public GraphDisplay() {
        seriesMap = new HashMap<>();
        biomeDetected = new HashMap<>();
        initializeComponents();
    }

    private void initializeComponents() {
        container = new VBox(AppStyles.SPACING_MEDIUM);
        container.setStyle(AppStyles.getCardStyle());

        createChart();
    }

    private void createChart() {
        NumberAxis xAxis = new NumberAxis("Steps", 0, 100, 10);
        NumberAxis yAxis = new NumberAxis("Entity Count", 0, 100, 10);
        xAxis.setAutoRanging(false); // Manually control x-axis range
        yAxis.setAutoRanging(true);

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Entity Population by Biome");
        lineChart.setStyle(AppStyles.getCardStyle());
        lineChart.setLegendVisible(true);
        lineChart.setCreateSymbols(false); // Performance optimization
        lineChart.setAnimated(false); // Disable animation for faster updates

        container.getChildren().add(lineChart);
    }

    private XYChart.Series<Number, Number> createSeries(String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        return series;
    }

    public VBox getContainer() {
        return container;
    }

    public LineChart<Number, Number> getChart() {
        return lineChart;
    }

    public void updateChart(int step, java.util.Map<Type, Integer> entityCounts) {
        // Dynamically add series for newly detected biomes
        for (Type type : entityCounts.keySet()) {
            if (!biomeDetected.getOrDefault(type, false)) {
                // First time seeing entities in this biome
                XYChart.Series<Number, Number> newSeries = createSeries(type.name());
                seriesMap.put(type, newSeries);
                lineChart.getData().add(newSeries);
                biomeDetected.put(type, true);

                // Apply biome color to the series line
                applySeriesColor(newSeries, type);
            }
        }

        // Update all detected series with new data points
        for (Map.Entry<Type, XYChart.Series<Number, Number>> entry : seriesMap.entrySet()) {
            Type type = entry.getKey();
            XYChart.Series<Number, Number> series = entry.getValue();
            int count = entityCounts.getOrDefault(type, 0);
            series.getData().add(new XYChart.Data<>(step, count));

            // Keep only last 20 data points for performance
            trimSeriesData(series);

            // Reapply color after data changes
            applyStyling(series, type);
        }

        // Update x-axis to show only the visible range
        updateXAxisRange();
    }

    private void trimSeriesData(XYChart.Series<Number, Number> series) {
        if (series.getData().size() > 20) {
            series.getData().remove(0);
        }
    }

    private void applySeriesColor(XYChart.Series<Number, Number> series, Type type) {
        // Apply biome color to the series line
        // JavaFX LineChart uses CSS classes for coloring, we need to override them
        series.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                applyStyling(series, type);
            }
        });
    }

    private void applyStyling(XYChart.Series<Number, Number> series, Type type) {
        // Find and style the actual line path elements
        javafx.application.Platform.runLater(() -> {
            if (series.getNode() != null) {
                // Style the series line
                series.getNode().lookupAll(".chart-series-line").forEach(node -> {
                    node.setStyle("-fx-stroke: " + type.getHex() + "; -fx-stroke-width: 2px;");
                });

                // Style the legend symbol
                series.getNode().lookupAll(".chart-line-symbol").forEach(node -> {
                    node.setStyle("-fx-background-color: " + type.getHex() + ", white;");
                });
            }
        });
    }

    private void updateXAxisRange() {
        // Find the min and max step values from all series
        double minStep = Double.MAX_VALUE;
        double maxStep = Double.MIN_VALUE;

        for (XYChart.Series<Number, Number> series : seriesMap.values()) {
            if (!series.getData().isEmpty()) {
                double firstStep = series.getData().get(0).getXValue().doubleValue();
                double lastStep = series.getData().get(series.getData().size() - 1).getXValue().doubleValue();
                minStep = Math.min(minStep, firstStep);
                maxStep = Math.max(maxStep, lastStep);
            }
        }

        // Update x-axis bounds if we have valid data
        if (minStep != Double.MAX_VALUE && maxStep != Double.MIN_VALUE) {
            NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
            xAxis.setLowerBound(minStep);
            xAxis.setUpperBound(maxStep);
            // Set tick unit to be approximately 1/5 of the range for nice spacing
            double range = maxStep - minStep;
            xAxis.setTickUnit(Math.max(1, range / 5));
        }
    }

    public void setPreferredSize(double width, double height) {
        lineChart.setPrefSize(width, height);
    }

    public void clearData() {
        // Clear all dynamic series
        for (XYChart.Series<Number, Number> series : seriesMap.values()) {
            series.getData().clear();
        }
        // Remove all series from chart
        lineChart.getData().clear();
        seriesMap.clear();
        biomeDetected.clear();
    }
}