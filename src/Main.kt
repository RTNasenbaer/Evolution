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
    private var selectedTerrainType: TerrainType = TerrainType.FOREST
    private var selectedBrush: BrushType = BrushType.TERRAIN
    private val height = 50
    private val width = 50
    private val cellSize = 10
    private val cells: MutableMap<Pair<Int, Int>, Cell> = mutableMapOf()
    private val floofs: MutableMap<Pair<Int, Int>, Floof> = mutableMapOf()

    override fun start(stage: Stage) {
        val root = HBox()
        val field = Group()
        val startBtn = Button("Start")
        val clearBtn = Button("Clear")
        val switch = Button(selectedBrush.name)
        switch.setOnAction {
            selectedBrush = when (selectedBrush) {
                BrushType.TERRAIN -> BrushType.FLOOF
                BrushType.FLOOF -> BrushType.ERASER
                BrushType.ERASER -> BrushType.TERRAIN
            }
            switch.text = selectedBrush.name
        }
        val choiceBox: ChoiceBox<String> = ChoiceBox<String>()
        choiceBox.items.addAll("Forest", "Tundra", "Plains", "Desert")
        choiceBox.value = "Forest"
        choiceBox.setOnAction {
            selectedTerrainType = when (choiceBox.value) {
                "Forest" -> TerrainType.FOREST
                "Tundra" -> TerrainType.TUNDRA
                "Plains" -> TerrainType.PLAINS
                "Desert" -> TerrainType.DESERT
                else -> TerrainType.EMPTY
            }
        }
        val brushSize = Slider(1.0, ((height + width) / 10).toDouble(), 1.0)
        val controls = VBox(startBtn, clearBtn, switch, choiceBox, brushSize)
        for (i in 0..width) {
            for (j in 0..height) {
                val cell = Cell(i, j, cellSize, TerrainType.EMPTY)
                cells[Pair(i, j)] = cell
                field.children.add(cell)
            }
        }
        field.setOnMouseClicked { e ->
            handleMouseClick(e.x, e.y, brushSize.value.toInt(), field)
        }
        field.setOnMouseDragged { e ->
            handleMouseClick(e.x, e.y, brushSize.value.toInt(), field)
        }
        root.children.add(field)
        root.children.add(controls)
        val scene = Scene(root, (width * cellSize).toDouble() * 1.2, (height * cellSize + cellSize).toDouble())
        scene.setOnKeyTyped { println("Something is done") }
        stage.scene = scene
        stage.title = "Hello World!"
        stage.isResizable = false
        stage.isMaximized = false
        stage.show()
    }

    private fun handleMouseClick(mouseX: Double, mouseY: Double, brushSize: Int, group: Group) {
        val x = (mouseX / cellSize).toInt()
        val y = (mouseY / cellSize).toInt()
        val radius = brushSize - 1 // Set the radius of the circle
        when (selectedBrush) {
            BrushType.TERRAIN -> updateTerrain(x, y, radius, selectedTerrainType)
            BrushType.FLOOF -> updateFloof(x, y, group)
            BrushType.ERASER -> updateTerrain(x, y, radius, TerrainType.EMPTY)
        }
    }

    private fun updateFloof(x: Int, y: Int, group: Group) {
        val floof = Floof(x, y, cellSize)
        floofs[Pair(x, y)] = floof
        group.children.add(floof)
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