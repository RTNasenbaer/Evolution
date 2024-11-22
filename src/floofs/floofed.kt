package floofs

abstract class floofed(x : Int, y : Int, gender : Boolean, genes : MutableMap<String, Double>) {

    var x : Int = x
    var y : Int = y
    val gender : Boolean = gender
    val genes : MutableMap<String, Double> = genes

    fun nextStep() {
        val zufall : Double = Math.random();
        if (zufall < 0.25) {
            x += 1
        } else if (zufall < 0.5) {
            x -= 1
        } else if (zufall < 0.75) {
            y += 1
        } else {
            y -= 1
        }
    }

}