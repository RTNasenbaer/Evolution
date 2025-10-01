package ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Component responsible for displaying configuration controls and settings.
 * Handles all user interface controls for simulation parameters and actions.
 */
public class ConfigDisplay {
    private VBox container;
    private Label titleLabel;

    // Control sections
    private VBox simulationSection;
    private VBox entitySection;
    private VBox analysisSection;
    private Label seedLabel;

    // Control elements - made public for external access
    public Button runStepBtn;
    public Button runManyBtn;
    public Button stopBtn;
    public Button setTickspeedBtn;
    public Button spawnBtn;
    public Button moveBtn; // Legacy - will be replaced
    public Button selectEntityBtn;
    public Button moveSelectedBtn;
    public Button inspectBtn;
    public Button statsBtn;
    public Button batchBtn;

    public TextField stepsField;
    public TextField tickspeedField;
    public TextField spawnX;
    public TextField spawnY;
    public TextField moveX;
    public TextField moveY;
    public TextField batchRunsField;
    public TextField batchStepsField;

    // Entity selection
    public Label selectedEntityLabel;
    private entities.Entity selectedEntity = null;

    public ConfigDisplay() {
        initializeComponents();
    }

    private void initializeComponents() {
        container = new VBox(AppStyles.SPACING_MEDIUM);
        container.setStyle(AppStyles.getCardStyle());
        container.setPrefWidth(350);

        titleLabel = new Label("Simulation Controls");
        titleLabel.setStyle(AppStyles.getTitleStyle());

        createSimulationSection();
        createEntitySection();
        createAnalysisSection();
        createSeedSection();

        container.getChildren().addAll(titleLabel, simulationSection, entitySection, analysisSection, seedLabel);
    }

    private void createSimulationSection() {
        simulationSection = new VBox(AppStyles.SPACING_SMALL);

        Label simLabel = new Label("Simulation");
        simLabel.setStyle(AppStyles.getSectionHeaderStyle());

        VBox simControls = new VBox(8);

        // Create controls
        runStepBtn = createButton("Run Step", AppStyles.INFO_COLOR);
        runManyBtn = createButton("Run N Steps", AppStyles.INFO_COLOR);
        stepsField = createTextField("Steps", 80);
        stopBtn = createButton("Stop", AppStyles.DANGER_COLOR);
        setTickspeedBtn = createButton("Set Tickspeed", AppStyles.SUCCESS_COLOR);
        tickspeedField = createTextField("Tickspeed (ms)", 120);

        HBox simRow1 = new HBox(8, runStepBtn, runManyBtn, stepsField);
        HBox simRow2 = new HBox(8, stopBtn, setTickspeedBtn, tickspeedField);

        simControls.getChildren().addAll(simRow1, simRow2);
        simulationSection.getChildren().addAll(simLabel, simControls);
    }

    private void createEntitySection() {
        entitySection = new VBox(AppStyles.SPACING_SMALL);

        Label entityLabel = new Label("Entity Management");
        entityLabel.setStyle(AppStyles.getSectionHeaderStyle());

        VBox entityControls = new VBox(8);

        // Spawn controls
        Label spawnLabel = new Label("Spawn Entity:");
        spawnLabel.setStyle(AppStyles.getLabelStyle());
        spawnBtn = createButton("Spawn Entity", AppStyles.SUCCESS_COLOR);
        spawnX = createTextField("X", 50);
        spawnY = createTextField("Y", 50);
        HBox spawnRow = new HBox(8, spawnBtn, spawnX, spawnY);

        // Move controls with improved interface
        Label moveLabel = new Label("Move Entity:");
        moveLabel.setStyle(AppStyles.getLabelStyle());

        selectEntityBtn = createButton("Select Entity", AppStyles.INFO_COLOR);
        moveSelectedBtn = createButton("Move Selected", AppStyles.WARNING_COLOR);
        moveSelectedBtn.setDisable(true); // Disabled until entity is selected

        selectedEntityLabel = new Label("No entity selected");
        selectedEntityLabel.setStyle(AppStyles.getLabelStyle() + " -fx-text-fill: #7f8c8d;");

        moveX = createTextField("Target X", 60);
        moveY = createTextField("Target Y", 60);

        HBox moveRow1 = new HBox(8, selectEntityBtn, selectedEntityLabel);
        HBox moveRow2 = new HBox(8, moveSelectedBtn, new Label("To:"), moveX, moveY);

        entityControls.getChildren().addAll(spawnLabel, spawnRow, moveLabel, moveRow1, moveRow2);
        entitySection.getChildren().addAll(entityLabel, entityControls);
    }

    private void createAnalysisSection() {
        analysisSection = new VBox(AppStyles.SPACING_SMALL);

        Label analysisLabel = new Label("Analysis Tools");
        analysisLabel.setStyle(AppStyles.getSectionHeaderStyle());

        VBox analysisControls = new VBox(8);

        inspectBtn = createButton("Inspect Entity", AppStyles.SECONDARY_COLOR);
        statsBtn = createButton("Show Stats", AppStyles.SECONDARY_COLOR);
        batchBtn = createButton("Run Batch", AppStyles.PRIMARY_COLOR);
        batchRunsField = createTextField("Runs", 70);
        batchStepsField = createTextField("Steps", 70);

        HBox analysisRow1 = new HBox(8, inspectBtn, statsBtn);
        HBox analysisRow2 = new HBox(8, batchBtn, batchRunsField, batchStepsField);

        analysisControls.getChildren().addAll(analysisRow1, analysisRow2);
        analysisSection.getChildren().addAll(analysisLabel, analysisControls);
    }

    private void createSeedSection() {
        seedLabel = new Label("Seed: calculating...");
        seedLabel.setStyle(AppStyles.getLabelStyle() + " -fx-font-family: 'Consolas', monospace;");
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(AppStyles.getButtonStyle(color));
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(
                e -> button.setStyle(button.getStyle().replace("-fx-scale-x: 1.05; -fx-scale-y: 1.05;", "")));
        return button;
    }

    private TextField createTextField(String prompt, int width) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        field.setStyle(AppStyles.getTextFieldStyle());
        return field;
    }

    public VBox getContainer() {
        return container;
    }

    public void updateSeedDisplay(String seedString) {
        String displaySeed = seedString.length() > 25 ? seedString.substring(0, 22) + "..." : seedString;
        seedLabel.setText("World Seed: " + displaySeed);
    }

    public void setPreferredWidth(double width) {
        container.setPrefWidth(width);
    }

    public void setSelectedEntity(entities.Entity entity) {
        this.selectedEntity = entity;
        if (entity != null) {
            selectedEntityLabel.setText(String.format("Entity at (%d, %d)", entity.getX(), entity.getY()));
            moveSelectedBtn.setDisable(false);
        } else {
            selectedEntityLabel.setText("No entity selected");
            moveSelectedBtn.setDisable(true);
        }
    }

    public entities.Entity getSelectedEntity() {
        return selectedEntity;
    }
}