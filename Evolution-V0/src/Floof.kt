import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Floof(
    x: Int,
    y: Int,
    val scale: Int,
    gene: Double,
    maps: Pair<MutableMap<Pair<Int, Int>, Cell>, MutableMap<Pair<Int, Int>, Floof>>
) : Rectangle() {

    //    var color : Color = Color.hsb(0.0, 0.0, 0.89) // Replaced Color.rgb(229, 228, 226)
    private var color: Color = Color.hsb(
        0.0,
        0.0,
        gene + (Math.random() * (gene / 2) * if (Math.random() < 0.5) 1 else -1)
    ) // Replaced Color.rgb(100, 100, 100)

    private var environment: MutableMap<Pair<Int, Int>, Cell> = maps.first
    var floofs: MutableMap<Pair<Int, Int>, Floof> = maps.second
    var food: Int = 0
    var dead: Boolean = false
    val gene = color.brightness

    init {
        this.width = (scale * 20).toDouble()
        this.height = (scale * 20).toDouble()
        this.x = (x * (scale * 20)).toDouble()
        this.y = (y * (scale * 20)).toDouble()
        fill = color
        stroke = Color.BLACK
        strokeWidth = 0.1
//        println("maxSaturation: $maxSaturation and minSaturation: $minSaturation | ${color.brightness}")
    }

    private fun move() {
        when (direction()) {
            0 -> if (y - scale * 20 >= 0) y -= scale * 20 else move()
            1 -> if (x + scale * 20 < environment.keys.maxOf { it.first } * scale * 20) x += scale * 20 else move()
            2 -> if (y + scale * 20 < environment.keys.maxOf { it.second } * scale * 20) y += scale * 20 else move()
            3 -> if (x - scale * 20 >= 0) x -= scale * 20 else move()
        }
    }

    private fun direction(): Int {
        return when (Math.random()) {
            in 0.0..0.25 -> 0
            in 0.25..0.5 -> 1
            in 0.5..0.75 -> 2
            else -> 3
        }
    }

}