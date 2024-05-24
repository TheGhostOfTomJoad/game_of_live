package org.example

import com.googlecode.lanterna.TextCharacter.fromCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlin.random.Random


fun main() {

    val terminal: TerminalScreen = DefaultTerminalFactory().createScreen()
    terminal.startScreen()
    terminal.setCharacter(0, 5, fromCharacter(' ', TextColor.ANSI.RED, TextColor.ANSI.GREEN_BRIGHT)[0])
    terminal.refresh()
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
    fun orthogonal(other:Direction):Boolean{
        return this.toV2().orthogonal(other.toV2())
    }

}


class SnakeController {
    val snakeUI = SnakeUI()
    val snakeModel = SnakeModel(10, 10)
    var onGoing = true
    fun playGame() {
        while (onGoing) {
            snakeModel.playRound()
        }
    }


}

class SnakeUI() {
    fun printGame(model: SnakeModel) {
        println(model)
    }
}


class Snake {
    var direction = Direction.North
    var head: V2 = V2(3, 3)
    var tail: MutableList<V2> = mutableListOf()

    fun move(appleEaten: Boolean) {
        tail.addFirst(head)
        tail.removeLast()
        head = (direction.toV2()) + head
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

    fun setDirection(newDirection: Direction) {
        if (newDirection.orthogonal(direction)) {
            direction = newDirection
        }
    }
}

class SnakeModel(val rows: Int, val cols: Int) {
    val snake: Snake = Snake()
    var points = 0
    var appleEatenLastRound = false

    var appleCoordinates: V2 = V2(4, 4)

    fun headIsOnBoard(): Boolean {
        val snakeHeadX = snake.head.x
        val snakeHeadY = snake.head.x
        return snakeHeadX < cols - 1 && snakeHeadX > 0 && snakeHeadY < cols - 1 && snakeHeadY > 0
    }

    fun tailBitten(): Boolean {
        return snake.tailBitten()
    }

    fun gameIsWon(): Boolean {
        return snake.snakeLen() == rows * cols
    }

    fun gameLost(): Boolean {
        return tailBitten() || !headIsOnBoard()
    }

    fun setNewApple() {
        val appleSet = false
        while (!appleSet) {
            val newAppleX = Random.nextInt(0, cols - 1)
            val newAppleY = Random.nextInt(0, rows - 1)
            val newAppleCoordinates = V2(newAppleX, newAppleY)
            if (!snake.contains(newAppleCoordinates)) {
                appleCoordinates = newAppleCoordinates
            }
        }
    }

    fun appleEatenThisRound(): Boolean {
        return snake.head == appleCoordinates
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
                    snake.contains(v) -> 'o'
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
}
