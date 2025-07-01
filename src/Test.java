import java.util.Random;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Test extends Application {

   private static final int MAX_X = 100;
   private static final int MAX_Y = 50;

    public static void main(String[] args) {
         launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
      ObservableList<XYChart.Data<Number, Number>> data1 = FXCollections.observableArrayList();
      ObservableList<XYChart.Data<Number, Number>> data2 = FXCollections.observableArrayList();

      var xAxis = new NumberAxis("x", 0, MAX_X, 10);
      var yAxis = new NumberAxis("y", 0, MAX_Y, 10);

      var series1 = new XYChart.Series<>(data1);
      series1.setName("Graph 1");
      var series2 = new XYChart.Series<>(data2);
      series2.setName("Graph 2");

      var lineChart = new LineChart<>(xAxis, yAxis);
      lineChart.getData().add(series1);
      lineChart.getData().add(series2);
      lineChart.setTitle("Multiple Graphs Example");

      var button = new Button("Add Data");
      button.setOnAction(e -> {
         int nextX1 = data1.size() * 10;
         int nextX2 = data2.size() * 10;

         Random random = new Random();
         data1.add(new XYChart.Data<>(nextX1, random.nextDouble() * MAX_Y)); // Add data to Graph 1
         data2.add(new XYChart.Data<>(nextX2, random.nextDouble() * MAX_Y)); // Add data to Graph 2

         // Adjust x-axis upper bound if necessary
         if (nextX1 >= xAxis.getUpperBound() || nextX2 >= xAxis.getUpperBound()) {
            xAxis.setUpperBound(Math.max(nextX1, nextX2) + 10);
         }
      });

      var root = new VBox(lineChart, button);
      primaryStage.setScene(new Scene(root, 800, 600));
      primaryStage.setTitle("Test");
      primaryStage.show();
   }
}
