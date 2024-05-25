package org.example

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.lang.Thread.sleep
import kotlin.random.Random


fun main() {
    val snakeController = SnakeController()
    snakeController.playGame()
}

enum class SnakeControl {
    Left, Right, Up, Down, Pause;

    fun toDirection(): Direction {
        return (when (this) {
            Left -> Direction.West
            Right -> Direction.East
            Up -> Direction.North
            Down -> Direction.South
            Pause -> throw Error("this should never happen")
        })
    }
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
}


class SnakeController {
    private val snakeUI = SnakeUI()
    private val snakeModel = SnakeModel(10, 10)

    fun playGame() {
        var onGoing = true
        snakeUI.initTerminalConfig()
        snakeUI.setInitialSquareSize(snakeModel.rows, snakeModel.cols)
        var gameIsWon = false
        var gameIsLost = false
        while (!gameIsWon && !gameIsLost) {
            val pressedKey = snakeUI.getPressedKey()
            if (pressedKey != null) {
                if (pressedKey == SnakeControl.Pause) {
                    onGoing = !onGoing
                } else {
                    snakeModel.processInput(pressedKey)
                }
            }
            if (onGoing) {
                snakeModel.playRound()
            }
            snakeUI.drawGame(snakeModel)
            sleep(1000)
            gameIsLost = snakeModel.gameLost()
            gameIsWon = snakeModel.gameIsWon()
        }
    }

}

class SnakeUI {
    private val terminal: TerminalScreen = DefaultTerminalFactory().createScreen()
    private val rectangleDrawer = RectangleDrawer(terminal)
    fun printGame(model: SnakeModel) {
        println(model)
    }

    fun initTerminalConfig() {
        rectangleDrawer.startScreen()
    }

    fun setInitialSquareSize(height: Int, width: Int) {
        rectangleDrawer.setInitialSquareSize(height, width)
    }

    fun drawGame(model: SnakeModel) {
        rectangleDrawer.clear()
        for (tailCoord in model.getSnakeCoordinates()) {
            rectangleDrawer.drawSquare(tailCoord.y, tailCoord.x, TextColor.ANSI.GREEN)
        }
        val appleCoord = model.getAppleCoordinates()
        rectangleDrawer.drawSquare(appleCoord.y, appleCoord.x, TextColor.ANSI.RED)
        rectangleDrawer.refresh()
    }

    private fun keyStrokeToSnakeControl(keystroke: KeyStroke): SnakeControl? {
        val result: SnakeControl? = when (keystroke.keyType) {
            KeyType.ArrowLeft -> SnakeControl.Left
            KeyType.ArrowRight -> SnakeControl.Right
            KeyType.ArrowUp -> SnakeControl.Up
            KeyType.ArrowDown -> SnakeControl.Down
            KeyType.MouseEvent -> {
                println("mouse clicked");null
            }

            else -> {
                when (keystroke.character) {
                    ' ' -> SnakeControl.Pause
                    else -> {
                        null
                    }
                }
            }
        }
        return result
    }

    fun getPressedKey(): SnakeControl? {
        val input = terminal.pollInput()
        if (input != null) {
            return keyStrokeToSnakeControl(input)
        }
        return null
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
        tail.addFirst(head)
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

class SnakeModel(val rows: Int, val cols: Int) {
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
        return snakeHeadX < cols - 1 && snakeHeadX > 0 && snakeHeadY < cols - 1 && snakeHeadY > 0
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
        when {
            appleEatenThisRound() -> {
                appleEatenLastRound = true; points += 1;setNewApple()
            }
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

    fun processInput(snakeControl: SnakeControl) {
        snake.setDirection2(snakeControl.toDirection())
    }

    fun getSnakeCoordinates(): List<V2> {
        return snake.toList()
    }
}
