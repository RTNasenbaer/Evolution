import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import world.Type;
import world.World;

import java.util.Map;

import entities.Entity;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class MainApp extends Application {
    private World world;
    private Display display;
    private int tickspeed = 200;
    private Stage primaryStage;
    private GridPane gridPane;
    private Label statusLabel;
    private Thread simulationThread;
    private volatile boolean running = false;
    private int globalStepCounter = 0;

    private XYChart.Series<Number, Number> series1;
    private XYChart.Series<Number, Number> series2;
    private XYChart.Series<Number, Number> series3;
    private XYChart.Series<Number, Number> series4;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        world = new World();
        world.addEntity(Entity.createDefaultEntity(0, 0));
        display = new Display(world, Display.DisplayMode.JAVAFX);
        gridPane = new GridPane();
        statusLabel = new Label("Ready");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        HBox controls = createControls();
        LineChart<Number, Number> lineChart = createLineChart();
        root.getChildren().addAll(controls, gridPane, lineChart, statusLabel);

        renderWorld();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Evolution Simulation");
        primaryStage.show();
    }

    private LineChart<Number, Number> createLineChart() {
        series1 = new XYChart.Series<>();
        series1.setName("Grass Biome");
        series2 = new XYChart.Series<>();
        series2.setName("Forest Biome");
        series3 = new XYChart.Series<>();
        series3.setName("Mountain Biome");
        series4 = new XYChart.Series<>();
        series4.setName("Desert Biome");

        NumberAxis xAxis = new NumberAxis("Steps", 0, 100, 10);
        NumberAxis yAxis = new NumberAxis("Entity Count", 0, 100, 10);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Entity Counts by Biome");
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        lineChart.getData().add(series3);
        lineChart.getData().add(series4);
        lineChart.setPrefSize(800, 400);

        return lineChart;
    }

    private HBox createControls() {
        Button runStepBtn = new Button("Run Step");
        Button runManyBtn = new Button("Run N Steps");
        Button spawnBtn = new Button("Spawn Entity");
        Button moveBtn = new Button("Move Entity");
        Button stopBtn = new Button("Stop");
        TextField stepsField = new TextField();
        stepsField.setPromptText("Steps");
        stepsField.setPrefWidth(60);
        TextField spawnX = new TextField();
        spawnX.setPromptText("X");
        spawnX.setPrefWidth(40);
        TextField spawnY = new TextField();
        spawnY.setPromptText("Y");
        spawnY.setPrefWidth(40);
        TextField moveEx = new TextField();
        moveEx.setPromptText("Ex");
        moveEx.setPrefWidth(40);
        TextField moveEy = new TextField();
        moveEy.setPromptText("Ey");
        moveEy.setPrefWidth(40);
        TextField moveX = new TextField();
        moveX.setPromptText("X");
        moveX.setPrefWidth(40);
        TextField moveY = new TextField();
        moveY.setPromptText("Y");
        moveY.setPrefWidth(40);
        TextField tickspeedField = new TextField();
        tickspeedField.setPromptText("Tickspeed (ms)");
        tickspeedField.setPrefWidth(80);
        Button setTickspeedBtn = new Button("Set Tickspeed");

        runStepBtn.setOnAction(e -> runSteps(1));
        runManyBtn.setOnAction(e -> {
            int steps = parseIntOr(stepsField.getText(), 1);
            runSteps(steps);
        });
        spawnBtn.setOnAction(e -> {
            int x = parseIntOr(spawnX.getText(), 0);
            int y = parseIntOr(spawnY.getText(), 0);
            world.addEntity(Entity.createDefaultEntity(x, y));
            renderWorld();
        });
        moveBtn.setOnAction(e -> {
            int ex = parseIntOr(moveEx.getText(), 0);
            int ey = parseIntOr(moveEy.getText(), 0);
            int x = parseIntOr(moveX.getText(), 0);
            int y = parseIntOr(moveY.getText(), 0);
            Entity entity = world.getEntity(ex, ey);
            if (entity != null) {
                entity.moveTo(x, y);
                entity.eat(world.getTile(entity.getX(), entity.getY()), tickspeed, World.SIZE);
                if (!entity.isAlive()) {
                    world.removeEntity(entity);
                }
                statusLabel.setText("Entity moved.");
            } else {
                statusLabel.setText("Entity not found at (" + ex + "," + ey + ")");
            }
            renderWorld();
        });
        setTickspeedBtn.setOnAction(e -> {
            int newTickspeed = parseIntOr(tickspeedField.getText(), 200);
            if (newTickspeed >= 0) {
                tickspeed = newTickspeed;
                statusLabel.setText("Tickspeed set to " + tickspeed + " ms.");
            } else {
                statusLabel.setText("Tickspeed must be non-negative.");
            }
        });
        stopBtn.setOnAction(e -> stopSimulation());

        HBox hbox = new HBox(5, runStepBtn, runManyBtn, stepsField, spawnBtn, spawnX, spawnY, moveBtn, moveEx, moveEy, moveX, moveY, setTickspeedBtn, tickspeedField, stopBtn);
        hbox.setPadding(new Insets(5));
        return hbox;
    }

    private void runSteps(int steps) {
        if (simulationThread != null && simulationThread.isAlive()) {
            statusLabel.setText("Simulation already running.");
            return;
        }
        running = true;
        simulationThread = new Thread(() -> {
            for (int i = 0; i < steps && running; i++) {
                for (Entity entity : world.getEntities()) { // Use a copy to avoid concurrent modification
                    entity.update(world, tickspeed, World.SIZE);
                }

                // Get entity counts by biome
                Map<Type, Integer> biomeEntityCounts = world.countEntitiesInAllBiomes();

                // Update the chart data only every tenth step
                Platform.runLater(() -> {
                    globalStepCounter++; // Increment the global step counter
                    if (globalStepCounter % 10 == 0) { // Add data only for every tenth step
                        series1.getData().add(new XYChart.Data<>(globalStepCounter, biomeEntityCounts.getOrDefault(Type.GRASS, 0)));
                        series2.getData().add(new XYChart.Data<>(globalStepCounter, biomeEntityCounts.getOrDefault(Type.FOREST, 0)));
                        series3.getData().add(new XYChart.Data<>(globalStepCounter, biomeEntityCounts.getOrDefault(Type.MOUNTAIN, 0)));
                        series4.getData().add(new XYChart.Data<>(globalStepCounter, biomeEntityCounts.getOrDefault(Type.DESERT, 0)));
                    }
                    renderWorld();
                });

                try {
                    Thread.sleep(tickspeed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            running = false;
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    private void stopSimulation() {
        running = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
        statusLabel.setText("Simulation stopped.");
    }

    private void renderWorld() {
        gridPane.getChildren().clear();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxWidth = Math.min(screenBounds.getWidth() / 2, screenBounds.getWidth() - 100); // Limit to half the screen width
        double maxHeight = screenBounds.getHeight() - 350; // Leave some margin for controls

        int tileSize = 24; // Default tile size
        int blockSize = 1; // Default block size

        // Calculate the maximum tile size that fits within the available space
        double scaleX = maxWidth / World.SIZE;
        double scaleY = maxHeight / World.SIZE;
        tileSize = (int) Math.floor(Math.min(scaleX, scaleY));

        // If the tile size is too small, combine tiles into larger blocks
        if (tileSize < 8) { // Minimum size for visibility
            blockSize = (int) Math.ceil(8.0 / tileSize);
            tileSize = (int) Math.floor(Math.min(scaleX * blockSize, scaleY * blockSize));
        }

        for (int y = 0; y < World.SIZE; y += blockSize) {
            for (int x = 0; x < World.SIZE; x += blockSize) {
                javafx.scene.control.Label label = display.createCombinedTileLabel(x, y, blockSize);
                label.setMinSize(tileSize, tileSize);
                label.setMaxSize(tileSize, tileSize);
                label.setFont(javafx.scene.text.Font.font("Monospaced", tileSize - 6));
                gridPane.add(label, x / blockSize, y / blockSize);
            }
        }

        gridPane.setPrefSize(tileSize * (World.SIZE / blockSize), tileSize * (World.SIZE / blockSize));
        gridPane.setMaxSize(tileSize * (World.SIZE / blockSize), tileSize * (World.SIZE / blockSize));
        primaryStage.setWidth(Math.min(tileSize * (World.SIZE / blockSize) + 40, maxWidth + 40));
        primaryStage.setHeight(Math.min(tileSize * (World.SIZE / blockSize) + 300, maxHeight + 300));
    }

    private int parseIntOr(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
