import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.math.pow

class Floof(x: Int, y: Int, scale: Int, gene : Double, cells: MutableMap<Pair<Int, Int>, Cell>) : Rectangle() {

    //    var color : Color = Color.hsb(0.0, 0.0, 0.89) // Replaced Color.rgb(229, 228, 226)
    private var color: Color = Color.hsb(0.0, 0.0, gene + (Math.random() * (gene/2) * if (Math.random() < 0.5) 1 else -1)) // Replaced Color.rgb(100, 100, 100)
    private var maxSaturation : Int = ((1.0+color.brightness)*((3.0).pow((1.0-color.brightness)/0.2)*(12.5/scale).pow(2.0))).toInt()
    private var minSaturation : Int = ((1.0-color.brightness)*((3.0).pow((1.0-color.brightness)/0.2)*(12.5/scale).pow(2.0))).toInt()
    private var environment : MutableMap<Pair<Int, Int>, Cell> = cells

    init {
        this.width = (scale*20).toDouble()
        this.height = (scale*20).toDouble()
        this.x = (x * (scale*20)).toDouble()
        this.y = (y * (scale*20)).toDouble()
        fill = color
        stroke = Color.BLACK
        strokeWidth = 0.1
        println("maxSaturation: $maxSaturation and minSaturation: $minSaturation | ${color.brightness}")
    }

}