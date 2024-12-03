import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Cell(x: Int, y: Int, size: Int, terrainType : TerrainType) : Rectangle() {

    var food: Int = 0

    init {
        this.width = size.toDouble()
        this.height = size.toDouble()
        this.x = (x * size).toDouble()
        this.y = (y * size).toDouble()
        fill = terrainType.ground
        stroke = Color.BLACK
        strokeWidth = 0.1
        if (Math.random() < terrainType.foodProbability) {
            food = (Math.random() * terrainType.foodMax).toInt()
        }
    }

    fun updateTerrainType(terrainType: TerrainType) {
        fill = terrainType.ground
        if (Math.random() < terrainType.foodProbability) {
            food = (Math.random() * terrainType.foodMax).toInt()
            fill = Color.hsb(270.0, 0.50, 0.80) // Replaced Color.rgb(153, 102, 204)
        }
    }

}

enum class TerrainType(val ground : Color, val foodProbability: Double, val foodMax: Int) {
    FOREST(Color.hsb(150.0, 1.0, 0.26), 0.8, 100), // Replaced Color.rgb(0, 66, 37)
    TUNDRA(Color.hsb(150.0, 0.17, 0.36), 0.3, 20), // Replaced Color.rgb(77, 93, 83)
    PLAINS(Color.hsb(150.0, 0.80, 0.45), 0.6, 50), // Replaced Color.rgb(23, 114, 69)
    DESERT(Color.hsb(50.0, 0.45, 0.93), 0.1, 10), // Replaced Color.rgb(238, 220, 130)
    EMPTY(Color.hsb(0.0, 0.0, 1.0), 0.0, 0) // Replaced Color.rgb(255, 255, 255)
}