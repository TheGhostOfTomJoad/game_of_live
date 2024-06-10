import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import org.example.V2
import java.lang.Thread.sleep
import kotlin.random.Random


fun main() {
    val snakeController = SnakeController()
    snakeController.playGame()
}


enum class Direction {
    North, East, South, West;

    fun toV2(): V2 {
        return when (this) {
            North -> V2(0, -1)
            East -> V2(1, 0)
            South -> V2(0, 1)
            West -> V2(-1, 0)
        }
    }

    fun orthogonal(other: Direction): Boolean {
        return this.toV2().orthogonal(other.toV2())
    }

    companion object {
        fun fromChar(char: Char): Direction? {
            return when (char) {
                'w' -> North
                's' -> South
                'a' -> West
                'd' -> East
                else -> {
                    null
                }
            }
        }
    }


}


class SnakeController {
    private val snakeUI = SnakeUI()
    private val snakeModel = SnakeModel(20, 20)

    fun playGame() {
        var onGoing = true
        snakeUI.setInitialSquareSize(snakeModel.rows, snakeModel.cols)
        var gameIsWon = false
        var gameIsLost = false
        while (!gameIsWon && !gameIsLost) {
            onGoing = processUserInput(onGoing)
            if (!onGoing) {
                continue
            }
            snakeModel.playRound()
            snakeUI.drawGame(snakeModel)
            sleep(200)
            gameIsLost = snakeModel.gameLost()
            gameIsWon = snakeModel.gameIsWon()


        }
        snakeUI.showEndScreen(gameIsLost,snakeModel.snakeLen())
    }



    private fun processUserInput(onGoing: Boolean): Boolean {
        val pressedKey = snakeUI.getPressedKey()
        if (pressedKey == null) {
            return onGoing
        }
        if (pressedKey == ' ') {
            return !onGoing
        }
        val newDirection = Direction.fromChar(pressedKey)
        if (newDirection != null) {
            snakeModel.setSnakeDirection(newDirection)
        }
        return onGoing
    }
}
    class SnakeUI {
        private val terminal: TerminalScreen = DefaultTerminalFactory().createScreen()
        private val rectangleDrawer = RectangleDrawer(terminal)
        private val borderSize = 1
        init {
            initTerminalConfig()
        }
        fun printGame(model: SnakeModel) {
            println(model)
        }

        private fun initTerminalConfig() {
            rectangleDrawer.startScreen()
        }

        fun setInitialSquareSize(height: Int, width: Int) {
            rectangleDrawer.setInitialSquareSize(height, width)
        }

        fun drawGame(model: SnakeModel) {
            rectangleDrawer.clear()
            rectangleDrawer.updateSquareSize(model.rows + 2, model.cols + 2)
            drawSnake(model.getSnakeCoordinates())
            drawBorder(model.rows , model.cols)
            drawApple(model.getAppleCoordinates())
            rectangleDrawer.refresh()
        }

        private fun drawSnake(snakeCoordinates: List<V2>) {
            for (tailCoord in snakeCoordinates) {

                rectangleDrawer.drawSquare(tailCoord.x + borderSize, tailCoord.y + borderSize, TextColor.ANSI.GREEN)
            }
        }

        private fun drawApple(appleCoord:V2) {

            rectangleDrawer.drawSquare(appleCoord.x + borderSize, appleCoord.y + borderSize, TextColor.ANSI.RED)
        }

        private fun drawBorder(rows: Int, cols: Int) {
            for (i in 0..cols) {
                rectangleDrawer.drawSquare(i, 0, TextColor.ANSI.CYAN)
                rectangleDrawer.drawSquare(i, rows, TextColor.ANSI.CYAN)
            }

            for (i in 0..rows) {
                rectangleDrawer.drawSquare(0, i, TextColor.ANSI.CYAN)
                rectangleDrawer.drawSquare(cols + 1, i, TextColor.ANSI.CYAN)
            }
        }


        fun getPressedKey(): Char? {
            val input = terminal.pollInput()
            if (input != null) {
                return input.character
            }
            return null
        }

        private fun showString(str: String) {
            terminal.clear()
            val textGraphics: TextGraphics = terminal.newTextGraphics()
            textGraphics.setForegroundColor(TextColor.ANSI.RED)
            textGraphics.setBackgroundColor(TextColor.ANSI.GREEN)
            textGraphics.putString(5, 5, str)
            terminal.refresh()
        }

        private fun showPoints(points: Int): String {
            return "You have $points ${if (points == 1) "point" else "points"}" + "!"
        }

        private fun showWon(points: Int) {
            showString("You won!" + showPoints(points))
        }

        private fun showLost(points: Int) {
            showString("Game Over!" + showPoints(points))
        }


        fun showEndScreen(gameIsLost: Boolean, points: Int) {
            if (gameIsLost) {
                showLost(points)
            } else {
                showWon(points)
            }
        }


    }


    class Snake {
        private var direction = Direction.North
        private var head: V2 = V2(5, 3)
        private var tail: MutableList<V2> = mutableListOf()
        fun getHead(): V2 {
            return head
        }

        fun getTail(): MutableList<V2> {
            return tail
        }

        fun move(appleEaten: Boolean) {
            tail.add(0, head)
            if (!appleEaten) {
                tail.removeLast()
            }
            head += direction.toV2()
        }

        fun tailBitten(): Boolean {
            return tail.contains(head)
        }

        fun snakeLen(): Int {
            return tail.size + 1
        }

        fun contains(v: V2): Boolean {
            return v == head || tail.contains(v)
        }

        fun setDirection2(newDirection: Direction) {
            if (newDirection.orthogonal(direction)) {
                direction = newDirection
            }
        }

        fun toList(): List<V2> {
            return listOf(head) + tail
        }


    }

    data class SnakeModel(val rows: Int, val cols: Int) {
        private val snake: Snake = Snake()
        private var points = 0
        private var appleEatenLastRound = false
        private var appleCoordinates: V2 = V2(4, 4)

        fun getAppleCoordinates(): V2 {
            return appleCoordinates
        }

        private fun headIsOnBoard(): Boolean {
            val snakeHeadX = snake.getHead().x
            val snakeHeadY = snake.getHead().y
            return snakeHeadX <= cols - 1 && snakeHeadX >= 0 && snakeHeadY < cols - 1 && snakeHeadY >= 0
        }

        private fun tailBitten(): Boolean {
            return snake.tailBitten()
        }

        fun gameIsWon(): Boolean {
            return snake.snakeLen() == rows * cols
        }

        fun gameLost(): Boolean {
            return tailBitten() || !headIsOnBoard()
        }

        private fun setNewApple() {
            var appleSet = false
            while (!appleSet) {
                val newAppleX = Random.nextInt(0, cols - 1)
                val newAppleY = Random.nextInt(0, rows - 1)
                val newAppleCoordinates = V2(newAppleX, newAppleY)
                if (!snake.contains(newAppleCoordinates)) {
                    appleCoordinates = newAppleCoordinates
                    appleSet = true
                }
            }
        }

        private fun appleEatenThisRound(): Boolean {
            return snake.getHead() == appleCoordinates
        }

        fun playRound() {
            snake.move(appleEatenLastRound)
            appleEatenLastRound = false
            if (appleEatenThisRound()) {
                appleEatenLastRound = true; points += 1;setNewApple()
            }
        }


        override fun toString(): String {
            var acc = ""
            for (i in -1..rows) {
                for (j in -1..cols) {
                    val v = V2(j, i)
                    val c: Char = when {
                        snake.getHead() == v -> '@'
                        snake.getTail().contains(v) -> 'o'
                        v == appleCoordinates -> '$'
                        i == -1 || i == rows || j == cols || j == -1 -> 'x'
                        else -> {
                            ' '
                        }
                    }
                    acc += c
                }
                acc += '\n'
            }
            return acc
        }

        fun setSnakeDirection(direction: Direction) {
            snake.setDirection2(direction)
        }

        fun getSnakeCoordinates(): List<V2> {
            return snake.toList()
        }

        fun snakeLen(): Int {
            return snake.snakeLen()
        }

    }

