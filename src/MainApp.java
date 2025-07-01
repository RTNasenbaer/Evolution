import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import world.World;
import entities.Entity;

public class MainApp extends Application {

    private World world;
    private Display display;
    private int tickspeed = 200;
    private GridPane gridPane;
    private TextArea logArea;
    private TextField commandField;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        world = new World();
        world.addEntity(new Entity(0, 0, 100, 1));
        display = new Display(world);

        BorderPane root = new BorderPane();
        gridPane = new GridPane();
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(5);

        // --- Sidebar VBox (now on the right) ---
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #222;");

        // Section: Simulation Controls
        Label simLabel = new Label("Simulation");
        simLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Button runBtn = new Button("Run 1 Step");
        Button run10Btn = new Button("Run 10 Steps");
        Slider tickspeedSlider = new Slider(10, 1000, tickspeed);
        tickspeedSlider.setShowTickLabels(true);
        tickspeedSlider.setShowTickMarks(true);
        tickspeedSlider.setMajorTickUnit(250);
        tickspeedSlider.setMinorTickCount(4);
        tickspeedSlider.setBlockIncrement(10);
        Label tickspeedLabel = new Label("Tickspeed: " + tickspeed + " ms");
        tickspeedLabel.setStyle("-fx-text-fill: white;");
        tickspeedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            tickspeed = newVal.intValue();
            tickspeedLabel.setText("Tickspeed: " + tickspeed + " ms");
        });
        VBox simControls = new VBox(10, runBtn, run10Btn, tickspeedLabel, tickspeedSlider);

        // Section: Entity Controls
        Label entityLabel = new Label("Entities");
        entityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Button spawnBtn = new Button("Spawn");
        Button moveBtn = new Button("Move");
        VBox entityControls = new VBox(10, spawnBtn, moveBtn);

        // Section: Command Input
        Label cmdLabel = new Label("Command");
        cmdLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        commandField = new TextField();
        commandField.setPromptText("Enter command...");
        commandField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleCommand(commandField.getText());
                commandField.clear();
            }
        });
        VBox cmdControls = new VBox(5, cmdLabel, commandField);

        // Add all sections to sidebar
        sidebar.getChildren().addAll(simLabel, simControls, entityLabel, entityControls, cmdControls);

        // Place sidebar on the right side of the main layout
        root.setRight(sidebar);

        // Log area and grid
        root.setCenter(gridPane);
        root.setBottom(logArea);

        // Button actions
        runBtn.setOnAction(e -> runSteps(1));
        run10Btn.setOnAction(e -> runSteps(10));
        spawnBtn.setOnAction(e -> {
            world.addEntity(new Entity(0, 0, 100, 1));
            log("Spawned entity at (0,0)");
            renderGrid();
        });
        moveBtn.setOnAction(e -> {
            if (!world.getEntities().isEmpty()) {
                world.getEntities().get(0).moveRandomly();
                log("Moved entity randomly");
                renderGrid();
            }
        });

        // Fullscreen
        Scene scene = new Scene(root, 1200, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Evolution World");
        primaryStage.setFullScreen(true);
        primaryStage.show();

        renderGrid();
    }

    private void renderGrid() {
        gridPane.getChildren().clear();
        for (int y = 0; y < World.HEIGHT; y++) {
            for (int x = 0; x < World.WIDTH; x++) {
                Label label = display.createTileLabel(world, x, y);
                gridPane.add(label, x, y);
            }
        }
    }

    private void runSteps(int steps) {
        new Thread(() -> {
            for (int i = 0; i < steps; i++) {
                for (Entity entity : world.getEntities()) {
                    entity.moveRandomly();
                }
                Platform.runLater(this::renderGrid);
                try {
                    Thread.sleep(tickspeed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log("Interrupted.");
                    break;
                }
            }
        }).start();
    }

    private void handleCommand(String input) {
        if (input.equals("/exit")) {
            Platform.exit();
        } else if (input.equals("/run")) {
            runSteps(1);
        } else if (input.startsWith("/run ")) {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                try {
                    int steps = Integer.parseInt(parts[1]);
                    runSteps(steps);
                } catch (NumberFormatException e) {
                    log("Invalid number of steps.");
                }
            } else {
                log("Usage: /run <steps>");
            }
        } else if (input.startsWith("/move ")) {
            String[] parts = input.split(" ");
            if (parts.length == 3) {
                try {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    world.getEntities().get(0).moveTo(x, y);
                    log("Moved entity to (" + x + "," + y + ")");
                } catch (NumberFormatException e) {
                    log("Invalid coordinates.");
                }
            } else if (parts.length == 5) {
                try {
                    int ex = Integer.parseInt(parts[1]);
                    int ey = Integer.parseInt(parts[2]);
                    int x = Integer.parseInt(parts[3]);
                    int y = Integer.parseInt(parts[4]);
                    world.getEntity(ex, ey).moveTo(x, y);
                    log("Moved entity at (" + ex + "," + ey + ") to (" + x + "," + y + ")");
                } catch (NumberFormatException e) {
                    log("Invalid coordinates.");
                }
            } else {
                log("Usage: /move x y");
            }
            renderGrid();
        } else if (input.startsWith("/spawn ")) {
            String[] parts = input.split(" ");
            if (parts.length == 3) {
                try {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    world.addEntity(new Entity(x, y, 100, 1));
                    log("Spawned entity at (" + x + "," + y + ")");
                } catch (NumberFormatException e) {
                    log("Invalid coordinates.");
                }
            } else {
                log("Usage: /spawn x y");
            }
            renderGrid();
        } else if (input.startsWith("/tickspeed ")) {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                try {
                    int newTickspeed = Integer.parseInt(parts[1]);
                    if (newTickspeed < 0) {
                        log("Tickspeed must be non-negative.");
                    } else {
                        tickspeed = newTickspeed;
                        log("Tickspeed set to " + tickspeed + " ms.");
                    }
                } catch (NumberFormatException e) {
                    log("Invalid tickspeed.");
                }
            } else {
                log("Usage: /tickspeed <ms>");
            }
        } else {
            log("Unknown command.");
        }
    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}