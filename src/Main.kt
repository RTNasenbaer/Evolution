import javafx.application.Application
import javafx.scene.*
import javafx.stage.Stage


class Main : Application() {
    val HEIGHT = 50
    val WIDTH = 50
    val CELL_SIZE = 10

    override fun start(stage: Stage) {
        var root : Group = Group();
        for (i in 0..HEIGHT) {
            for (j in 0..WIDTH) {
                var cells : Array<Cell> = Array
            }
        }
        stage.title = "Hello World!"
        stage.show()
    }

}