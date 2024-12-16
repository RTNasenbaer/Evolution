import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Cell(x: Int, y: Int, size: Int, terrainType: TerrainType) : Rectangle() {

    var food: Int = 0
    val regenAt: Int = 5
    var regen: Int = 0
    var regenEnabled: Boolean = false
    var currentTerrain: TerrainType = terrainType

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
        currentTerrain = terrainType
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

    fun update() {
        if (regenEnabled) if (food == 0) if (regen == regenAt) {
            regen = 0
            if (Math.random() < currentTerrain.foodProbability) {
                food = (Math.random() * currentTerrain.foodMax).toInt()
                fill = Color.hsb(270.0, 0.50, 1.0 - (food.toDouble() / 100)) // Replaced Color.rgb(153, 102, 204)
                regenEnabled = false
            }
        } else regen++
    }

    fun eaten(): Int {
        val eaten = food
        food = 0
        regenEnabled = true
        fill = Color.hsb(
            currentTerrain.ground.hue,
            currentTerrain.ground.saturation,
            currentTerrain.ground.brightness - (Math.random() * 0.1)
        )
        return eaten
    }

}

enum class TerrainType(val ground: Color, val foodProbability: Double, val foodMax: Int) {
    FOREST(Color.hsb(150.0, 1.0, 0.26), 0.9, 90),
    TUNDRA(Color.hsb(150.0, 0.17, 0.36), 0.3, 30),
    PLAINS(Color.hsb(150.0, 0.80, 0.45), 0.6, 45),
    DESERT(Color.hsb(50.0, 0.45, 0.93), 0.1, 30),
    EMPTY(Color.hsb(0.0, 0.0, 1.0), 0.0, 0);

}