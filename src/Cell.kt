import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Cell(x: Int, y: Int, CELL_SIZE: Int) : Rectangle() {

    init {
        this.width = CELL_SIZE.toDouble()
        this.height = CELL_SIZE.toDouble()
        this.x = (x * CELL_SIZE).toDouble()
        this.y = (y * CELL_SIZE).toDouble()
        fill = Color.TRANSPARENT
        stroke = Color.BLACK
        strokeWidth = 0.1
    }

}
