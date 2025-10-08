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
        xAxis.setAutoRanging(true);
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
            }
        }

        // Update all detected series with new data points
        for (Map.Entry<Type, XYChart.Series<Number, Number>> entry : seriesMap.entrySet()) {
            Type type = entry.getKey();
            XYChart.Series<Number, Number> series = entry.getValue();
            int count = entityCounts.getOrDefault(type, 0);
            series.getData().add(new XYChart.Data<>(step, count));

            // Keep only last 100 data points for performance
            trimSeriesData(series);
        }
    }

    private void trimSeriesData(XYChart.Series<Number, Number> series) {
        if (series.getData().size() > 20) {
            series.getData().remove(0);
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