package ui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import world.Type;

/**
 * Component responsible for displaying population charts and graphs.
 * Handles the visualization of entity counts and population trends over time.
 */
public class GraphDisplay {
    private VBox container;

    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series1;
    private XYChart.Series<Number, Number> series2;
    private XYChart.Series<Number, Number> series3;
    private XYChart.Series<Number, Number> series4;

    public GraphDisplay() {
        initializeComponents();
    }

    private void initializeComponents() {
        container = new VBox(AppStyles.SPACING_MEDIUM);
        container.setStyle(AppStyles.getCardStyle());

        createChart();
    }

    private void createChart() {
        series1 = createSeries("Grass Biome");
        series2 = createSeries("Forest Biome");
        series3 = createSeries("Mountain Biome");
        series4 = createSeries("Desert Biome");

        NumberAxis xAxis = new NumberAxis("Steps", 0, 100, 10);
        NumberAxis yAxis = new NumberAxis("Entity Count", 0, 100, 10);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Entity Population by Biome");
        lineChart.setStyle(AppStyles.getCardStyle());
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        lineChart.getData().add(series3);
        lineChart.getData().add(series4);
        lineChart.setLegendVisible(true);

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
        // Update each series with new data points
        if (entityCounts.containsKey(Type.GRASS)) {
            series1.getData().add(new XYChart.Data<>(step, entityCounts.get(Type.GRASS)));
        }
        if (entityCounts.containsKey(Type.FOREST)) {
            series2.getData().add(new XYChart.Data<>(step, entityCounts.get(Type.FOREST)));
        }
        if (entityCounts.containsKey(Type.MOUNTAIN)) {
            series3.getData().add(new XYChart.Data<>(step, entityCounts.get(Type.MOUNTAIN)));
        }
        if (entityCounts.containsKey(Type.DESERT)) {
            series4.getData().add(new XYChart.Data<>(step, entityCounts.get(Type.DESERT)));
        }

        // Keep only last 100 data points for performance
        trimSeriesData(series1);
        trimSeriesData(series2);
        trimSeriesData(series3);
        trimSeriesData(series4);
    }

    private void trimSeriesData(XYChart.Series<Number, Number> series) {
        if (series.getData().size() > 100) {
            series.getData().remove(0);
        }
    }

    public void setPreferredSize(double width, double height) {
        lineChart.setPrefSize(width, height);
    }

    public void clearData() {
        series1.getData().clear();
        series2.getData().clear();
        series3.getData().clear();
        series4.getData().clear();
    }
}