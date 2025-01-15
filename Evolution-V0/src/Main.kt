import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Slider
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

class Main : Application() {
    // Variables
    private val scale = 5
    private var selectedTerrainType: TerrainType = TerrainType.FOREST
    private var selectedBrush: BrushType = BrushType.TERRAIN
    private val height = 50 / scale
    private val width = 50 / scale
    private val cellSize = 20 * scale
    private val cells: MutableMap<Pair<Int, Int>, Cell> = mutableMapOf()
    private val floofs: MutableMap<Pair<Int, Int>, Floof> = mutableMapOf()

    override fun start(stage: Stage) {
        // UI Setup
        val root = HBox()
        val field = Group()
        val startBtn = Button("Start")
        val clearBtn = Button("Clear")
        val switch = Button(selectedBrush.name)
        val choiceBox: ChoiceBox<TerrainType> = ChoiceBox<TerrainType>()
        val brushSize = Slider(1.0, ((height + width) / 10).toDouble(), 1.0)
        val controls = VBox(startBtn, clearBtn, switch, choiceBox, brushSize)

        initializeCells(field)
        setupFieldHandlers(field, brushSize)
        setupControls(switch, choiceBox)

        root.children.addAll(field, controls)
        val scene = Scene(root, (width * cellSize).toDouble() * 1.2, (height * cellSize + cellSize).toDouble())
        setupSceneHandlers(scene)

        stage.scene = scene
        stage.title = "Hello World!"
        stage.isResizable = false
        stage.isMaximized = false
        stage.show()
    }

    private fun initializeCells(field: Group) {
        for (i in 0..width) {
            for (j in 0..height) {
                val cell = Cell(i, j, cellSize, TerrainType.EMPTY)
                cells[Pair(i, j)] = cell
            }
        }
        field.children.addAll(cells.values)
    }

    private fun setupFieldHandlers(field: Group, brushSize: Slider) {
        field.setOnMouseClicked { e ->
            handleMouseClick(e.x, e.y, brushSize.value.toInt(), field)
        }
        field.setOnMouseDragged { e ->
            handleMouseClick(e.x, e.y, brushSize.value.toInt(), field)
        }
    }

    private fun setupControls(switch: Button, choiceBox: ChoiceBox<TerrainType>) {
        switch.setOnAction {
            selectedBrush = when (selectedBrush) {
                BrushType.TERRAIN -> BrushType.FLOOF
                BrushType.FLOOF -> BrushType.ERASER
                BrushType.ERASER -> BrushType.TERRAIN
            }
            switch.text = selectedBrush.name
        }
        choiceBox.items.addAll(TerrainType.FOREST, TerrainType.TUNDRA, TerrainType.PLAINS, TerrainType.DESERT)
        choiceBox.value = selectedTerrainType
        choiceBox.setOnAction { selectedTerrainType = choiceBox.value }
    }

    private fun setupSceneHandlers(scene: Scene) {
        scene.setOnKeyTyped { e ->
            when (e.character) {
                "i" -> step(1)
                "x" -> step(10)
                "c" -> step(100)
                "m" -> step(1000)
            }
        }
    }

    private fun step(i: Int) {
        for (ii in 1..i) {
            for (floof in floofs.values) println("not done") // TODO: Implement this
            for (cell in cells.values) println("not done") // TODO: Implement this
        }
    }

    private fun handleMouseClick(mouseX: Double, mouseY: Double, brushSize: Int, group: Group) {
        val x = (mouseX / cellSize).toInt()
        val y = (mouseY / cellSize).toInt()
        val radius = brushSize - 1
        when (selectedBrush) {
            BrushType.TERRAIN -> updateTerrain(x, y, radius, selectedTerrainType)
            BrushType.FLOOF -> updateFloof(x, y, group)
            BrushType.ERASER -> updateTerrain(x, y, radius, TerrainType.EMPTY)
        }
    }

    private fun updateFloof(x: Int, y: Int, group: Group) {
        if (!floofs.containsKey(Pair(x, y))) {
            val floof = Floof(x, y, scale, 0.5, Pair(cells, floofs))
            floofs[Pair(x, y)] = floof
            group.children.add(floof)
        }
    }

    private fun updateTerrain(x: Int, y: Int, radius: Int, type: TerrainType) {
        for (i in -radius..radius) {
            for (j in -radius..radius) {
                if (i * i + j * j <= radius * radius) {
                    val cellX = x + i
                    val cellY = y + j
                    val cell = cells[Pair(cellX, cellY)]
                    cell?.updateTerrainType(type)
                }
            }
        }
    }
}

enum class BrushType {
    TERRAIN,
    FLOOF,
    ERASER
}