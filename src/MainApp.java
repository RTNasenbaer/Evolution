import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
        root.getChildren().addAll(controls, gridPane, statusLabel);

        renderWorld();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Evolution Simulation");
        primaryStage.show();
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

                // Print entity counts in all biomes
                Map<Type, Integer> biomeEntityCounts = world.countEntitiesInAllBiomes();
                System.out.println("Entity counts by biome after step " + (i + 1) + ":");
                for (Map.Entry<Type, Integer> entry : biomeEntityCounts.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

                Platform.runLater(this::renderWorld);
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
        double maxWidth = screenBounds.getWidth() - 100; // leave some margin
        double maxHeight = screenBounds.getHeight() - 150; // leave some margin for controls
        int tileSize = 24;
        double neededWidth = World.SIZE * tileSize;
        double neededHeight = World.SIZE * tileSize;
        // Adjust tile size if needed
        if (neededWidth > maxWidth || neededHeight > maxHeight) {
            double scaleX = maxWidth / World.SIZE;
            double scaleY = maxHeight / World.SIZE;
            tileSize = (int) Math.floor(Math.min(scaleX, scaleY));
            if (tileSize < 8) tileSize = 8; // minimum size for visibility
        }
        for (int y = 0; y < World.SIZE; y++) {
            for (int x = 0; x < World.SIZE; x++) {
                javafx.scene.control.Label label = display.createTileLabel(world, x, y);
                label.setMinSize(tileSize, tileSize);
                label.setMaxSize(tileSize, tileSize);
                label.setFont(javafx.scene.text.Font.font("Monospaced", tileSize - 6));
                gridPane.add(label, x, y);
            }
        }
        gridPane.setPrefSize(tileSize * World.SIZE, tileSize * World.SIZE);
        gridPane.setMaxSize(tileSize * World.SIZE, tileSize * World.SIZE);
        primaryStage.setWidth(Math.min(tileSize * World.SIZE + 40, maxWidth + 40));
        primaryStage.setHeight(Math.min(tileSize * World.SIZE + 120, maxHeight + 120));
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
