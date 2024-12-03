import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Floof(x: Int, y: Int, size: Int) : Rectangle() {

    var saturation : Double = 0.5
//    var color : Color = Color.hsb(0.0, 0.0, 0.89) // Replaced Color.rgb(229, 228, 226)
    var color : Color = Color.hsb(0.0, 0.0, saturation) // Replaced Color.rgb(100, 100, 100)

    init {
        this.width = size.toDouble()
        this.height = size.toDouble()
        this.x = (x * size).toDouble()
        this.y = (y * size).toDouble()
        fill = color
        stroke = Color.BLACK
        strokeWidth = 0.1
    }

}