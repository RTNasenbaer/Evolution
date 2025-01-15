import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Cell(x: Int, y: Int, size: Int, var terrainType: TerrainType) : Rectangle() {

    private var food: Int = 0

    init {
        this.width = size.toDouble()
        this.height = size.toDouble()
        this.x = (x * size).toDouble()
        this.y = (y * size).toDouble()
        stroke = Color.BLACK
        strokeWidth = 0.1
        updateTerrainType(terrainType)
    }

    fun updateTerrainType(terrainType: TerrainType) {
        this.terrainType = terrainType
        val colorOverride: Color = Color.hsb(
            terrainType.ground.hue,
            terrainType.ground.saturation,
            terrainType.ground.brightness - (Math.random() * 0.1)
        )
        fill = colorOverride
        food = 0
        if (Math.random() < terrainType.foodProbability) {
            food = (Math.random() * terrainType.foodMax).toInt()
            fill = Color.hsb(270.0, 0.50, 1.0 - (food.toDouble() / 100)) // Replaced Color.rgb(153, 102, 204)
        }
    }

}

enum class TerrainType(val ground: Color, val foodProbability: Double, val foodMax: Int) {
    FOREST(Color.hsb(150.0, 1.0, 0.26), 0.8, 100),
    TUNDRA(Color.hsb(150.0, 0.17, 0.36), 0.3, 20),
    PLAINS(Color.hsb(150.0, 0.80, 0.45), 0.6, 50),
    DESERT(Color.hsb(50.0, 0.45, 0.93), 0.1, 10),
    EMPTY(Color.hsb(0.0, 0.0, 1.0), 0.0, 0);

}