package ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import world.Type;

/**
 * Component responsible for world building controls.
 * Handles biome selection, brush settings, and world manipulation tools.
 */
public class WorldBuilderControls {
    private VBox container;
    private Label titleLabel;

    // Control sections
    private VBox biomeSection;
    private VBox brushSection;
    private VBox toolsSection;
    private Label seedLabel;

    // Biome selection
    private ToggleGroup biomeGroup;
    private Type selectedBiome = Type.GRASS;

    // Brush settings
    private int brushSize = 1;
    private BrushShape brushShape = BrushShape.SQUARE;
    private Spinner<Integer> sizeSpinner;
    private ToggleGroup shapeGroup;

    // Tool buttons - made public for external access
    public Button clearBtn;
    public Button randomBtn;
    public Button saveBtn;
    public Button loadBtn;
    public Button exportSeedBtn;
    public Button importSeedBtn;

    public enum BrushShape {
        SQUARE, CIRCLE
    }

    public WorldBuilderControls() {
        initializeComponents();
    }

    private void initializeComponents() {
        container = new VBox(AppStyles.SPACING_MEDIUM);
        container.setStyle(AppStyles.getCardStyle());
        container.setPrefWidth(350);

        titleLabel = new Label("World Builder Tools");
        titleLabel.setStyle(AppStyles.getTitleStyle());

        createBiomeSection();
        createBrushSection();
        createToolsSection();
        createSeedSection();

        container.getChildren().addAll(titleLabel, biomeSection, brushSection, toolsSection, seedLabel);
    }

    private void createBiomeSection() {
        biomeSection = new VBox(AppStyles.SPACING_SMALL);

        Label biomeLabel = new Label("Select Biome:");
        biomeLabel.setStyle(AppStyles.getSectionHeaderStyle());

        biomeGroup = new ToggleGroup();

        // Create 2x4 grid layout for biome buttons
        VBox biomeGrid = new VBox(5);
        Type[] biomeTypes = Type.values();

        for (int row = 0; row < 2; row++) {
            HBox biomeRow = new HBox(5);
            for (int col = 0; col < 4; col++) {
                int index = row * 4 + col;
                if (index < biomeTypes.length) {
                    Type type = biomeTypes[index];
                    ToggleButton btn = new ToggleButton(type.name());
                    btn.setToggleGroup(biomeGroup);
                    btn.setStyle(String.format(
                            AppStyles.getButtonStyle(type.getHex()) + " -fx-min-width: 75; -fx-max-width: 75;"));
                    btn.setOnAction(e -> selectedBiome = type);
                    biomeRow.getChildren().add(btn);

                    if (type == Type.GRASS) {
                        btn.setSelected(true);
                    }
                }
            }
            biomeGrid.getChildren().add(biomeRow);
        }

        biomeSection.getChildren().addAll(biomeLabel, biomeGrid);
    }

    private void createBrushSection() {
        brushSection = new VBox(AppStyles.SPACING_SMALL);

        Label brushLabel = new Label("Brush Settings:");
        brushLabel.setStyle(AppStyles.getSectionHeaderStyle());

        HBox brushControls = new HBox(15);

        // Size control
        VBox sizeControl = new VBox(8);
        Label sizeLabel = new Label("Size:");
        sizeLabel.setStyle(AppStyles.getLabelStyle());
        sizeSpinner = new Spinner<>(1, 10, brushSize);
        sizeSpinner.setMaxWidth(70);
        sizeSpinner.setStyle(AppStyles.getTextFieldStyle());
        sizeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> brushSize = newVal);
        sizeControl.getChildren().addAll(sizeLabel, sizeSpinner);

        // Shape control
        VBox shapeControl = new VBox(8);
        Label shapeLabel = new Label("Shape:");
        shapeLabel.setStyle(AppStyles.getLabelStyle());
        HBox shapeButtons = new HBox(5);
        shapeGroup = new ToggleGroup();

        ToggleButton squareBtn = new ToggleButton("□");
        squareBtn.setToggleGroup(shapeGroup);
        squareBtn.setSelected(true);
        squareBtn.setStyle(AppStyles.getPrimaryButtonStyle());
        squareBtn.setOnAction(e -> brushShape = BrushShape.SQUARE);
        squareBtn.setTooltip(new Tooltip("Square brush"));

        ToggleButton circleBtn = new ToggleButton("○");
        circleBtn.setToggleGroup(shapeGroup);
        circleBtn.setStyle(AppStyles.getSecondaryButtonStyle());
        circleBtn.setOnAction(e -> brushShape = BrushShape.CIRCLE);
        circleBtn.setTooltip(new Tooltip("Circle brush"));

        shapeButtons.getChildren().addAll(squareBtn, circleBtn);
        shapeControl.getChildren().addAll(shapeLabel, shapeButtons);

        brushControls.getChildren().addAll(sizeControl, shapeControl);
        brushSection.getChildren().addAll(brushLabel, brushControls);
    }

    private void createToolsSection() {
        toolsSection = new VBox(AppStyles.SPACING_SMALL);

        Label toolsLabel = new Label("World Tools:");
        toolsLabel.setStyle(AppStyles.getSectionHeaderStyle());

        HBox toolButtonsRow1 = new HBox(10);
        HBox toolButtonsRow2 = new HBox(10);

        clearBtn = createButton("Clear All", AppStyles.DANGER_COLOR);
        randomBtn = createButton("Random Fill", AppStyles.WARNING_COLOR);
        saveBtn = createButton("Save", AppStyles.SUCCESS_COLOR);
        loadBtn = createButton("Load", AppStyles.INFO_COLOR);
        exportSeedBtn = createButton("Export Seed", AppStyles.SECONDARY_COLOR);
        importSeedBtn = createButton("Import Seed", AppStyles.PRIMARY_COLOR);

        exportSeedBtn.setTooltip(new Tooltip("Generate a seed that will recreate this exact terrain"));
        importSeedBtn.setTooltip(new Tooltip("Import a world from a seed string"));

        toolButtonsRow1.getChildren().addAll(clearBtn, randomBtn, saveBtn, loadBtn);
        toolButtonsRow2.getChildren().addAll(exportSeedBtn, importSeedBtn);
        toolsSection.getChildren().addAll(toolsLabel, toolButtonsRow1, toolButtonsRow2);
    }

    private void createSeedSection() {
        seedLabel = new Label("Seed: calculating...");
        seedLabel.setStyle(AppStyles.getLabelStyle() + " -fx-font-family: 'Consolas', monospace;");
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(AppStyles.getButtonStyle(color) + " -fx-min-width: 90;");
        return button;
    }

    public VBox getContainer() {
        return container;
    }

    public Type getSelectedBiome() {
        return selectedBiome;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public BrushShape getBrushShape() {
        return brushShape;
    }

    public void updateSeedDisplay(String seedString, String description) {
        String displaySeed = seedString.length() > 25 ? seedString.substring(0, 22) + "..." : seedString;
        seedLabel.setText("Seed: " + displaySeed);
        if (description != null && !description.isEmpty()) {
            seedLabel.setTooltip(new Tooltip(description));
        }
    }

    public void setPreferredWidth(double width) {
        container.setPrefWidth(width);
    }
}