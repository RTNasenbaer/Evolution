import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Floof(
    x: Int,
    y: Int,
    scale: Int,
    gene: Double,
    maps: Pair<MutableMap<Pair<Int, Int>, Cell>, MutableMap<Pair<Int, Int>, Floof>>
) : Rectangle() {

    //    var color : Color = Color.hsb(0.0, 0.0, 0.89) // Replaced Color.rgb(229, 228, 226)
    var color: Color = Color.hsb(
        0.0,
        0.0,
        gene + (Math.random() * (gene / 2) * if (Math.random() < 0.5) 1 else -1)
    ) // Replaced Color.rgb(100, 100, 100)

    //    var maxSaturation : Int = ((1.0+color.brightness)*((3.0).pow((1.0-color.brightness)/0.2)*(12.5/scale).pow(2.0))).toInt()
//    var minSaturation : Int = ((1.0-color.brightness)*((3.0).pow((1.0-color.brightness)/0.2)*(12.5/scale).pow(2.0))).toInt()
    var environment: MutableMap<Pair<Int, Int>, Cell> = maps.first
    var floofs: MutableMap<Pair<Int, Int>, Floof> = maps.second
    val scale = scale
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

    fun eat() {
        val fod = environment[Pair((x / (scale * 20)).toInt(), (y / (scale * 20)).toInt())]?.food
        food += environment[Pair((x / (scale * 20)).toInt(), (y / (scale * 20)).toInt())]?.eaten()!!
        println("Eating | $food | $fod")
    }

    fun move() {
        when (direction()) {
            0 -> if (y - scale * 20 >= 0) y -= scale * 20 else move()
            1 -> if (x + scale * 20 < environment.keys.maxOf { it.first } * scale * 20) x += scale * 20 else move()
            2 -> if (y + scale * 20 < environment.keys.maxOf { it.second } * scale * 20) y += scale * 20 else move()
            3 -> if (x - scale * 20 >= 0) x -= scale * 20 else move()
        }
    }

    fun direction(): Int {
        val directions: Int = when (Math.random()) {
            in 0.0..0.25 -> 0
            in 0.25..0.5 -> 1
            in 0.5..0.75 -> 2
            else -> 3
        }
        return directions
    }

    fun death() {
        if (food < 0) {
            dead = true; fill = Color.LIME
        }
        if (food > 100) {
            dead = true; fill = Color.RED
        }
    }

    fun reproduce() {
        if (/*food > 50 && */!dead) {
            var mx = (x / (scale * 20)).toInt()
            var my = (y / (scale * 20)).toInt()
            val possiblePositions = listOf(
                Pair(mx + 1, my),
                Pair(mx - 1, my),
                Pair(mx, my + 1),
                Pair(mx, my - 1)
            )
            val newPosition = possiblePositions.firstOrNull {
                environment.keys.contains(it) && !floofs.keys.contains(it)
            }
            if (newPosition != null) {
                val newFloof = Floof(
                    newPosition.first,
                    newPosition.second,
                    scale,
                    gene,
                    Pair(environment, floofs)
                )
                floofs[newPosition] = newFloof
                food -= 50
            }
        }
    }

    fun step() {
        death()
        reproduce()
        if (!dead) {
            food -= 25
            when (environment[Pair((x / (scale * 20)).toInt(), (y / (scale * 20)).toInt())]?.food!!) {
                0 -> move()
                else -> eat()
            }
        }
    }

}