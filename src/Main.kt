import javafx.application.Application
import javafx.scene.*
import javafx.stage.Stage


class Main : Application() {
    val HEIGHT = 50
    val WIDTH = 50
    val CELL_SIZE = 10

    override fun start(stage: Stage) {
        var root : Group = Group()
        for (i in 0..HEIGHT) {
            for (j in 0..WIDTH) {
                val cell : Cell = Cell(i, j, CELL_SIZE)
                var cells : Array<Cell> = arrayOf(cell)
                root.children.add(cell)
            }
        }
        val scene = Scene(root, (WIDTH * CELL_SIZE).toDouble(), (HEIGHT * CELL_SIZE).toDouble())
        scene.setOnKeyTyped { e -> println("Something is done") }
        stage.scene = scene
        stage.title = "Hello World!"
        stage.show()
    }

}