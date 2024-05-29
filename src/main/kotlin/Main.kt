package org.example


import RectangleDrawer
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.lang.Thread.sleep
import kotlin.math.max
import kotlin.math.min

enum class Control {
    Left, Right, Up, Down, Select, Pause
}

fun main() {
    val controller = Controller()
    controller.playGame()
}


class Controller {
    private val gui = GUI()

    //    private val model = Model(
//        "          \n" +
//                "          \n" +
//                "   x      \n" +
//                "  xx x    \n" +
//                "      x   \n" +
//                "   x      \n" +
//                "    x xx  \n" +
//                "      x   \n" +
//                "          \n" +
//                "          "
//    )
    private val model = Model("   \n   \n   ")

    fun playGame() {
        gui.initTerminalConfig()
        gui.setInitialSquareSize(model.getBoard())
        var ongoing = true
        while (true) {
            sleep(500)
            if (ongoing) {

                model.playRound()
            }
            val pressedKey = gui.getPressedKey()
            if (pressedKey != null) {
                if (pressedKey == Control.Pause) {
                    ongoing = !ongoing
                }
                model.processInput(pressedKey)
            }
            gui.printBoardLaterna(model.getRound(), model)
        }
    }
}


class GUI {
    private val terminal: TerminalScreen = DefaultTerminalFactory().createScreen()
    private val tileDrawer = RectangleDrawer(terminal)

    fun printGame(round: Int, gameBoard: Board) {
        println("round: $round")
        println(gameBoard.toString())
    }

    fun initTerminalConfig() {
        terminal.startScreen()
    }

    fun setInitialSquareSize(board: Board) {
        tileDrawer.setInitialSquareSize(board.height, board.width)
    }

    fun printBoardLaterna(round: Int, model: Model) {
        tileDrawer.clear()
        val gameBoard = model.getBoard()
        tileDrawer.updateSquareSize(gameBoard.height, gameBoard.width)
        for (i in 0 until gameBoard.height) {
            for (j in 0 until gameBoard.width) {
                if (gameBoard.getAt(i, j)) tileDrawer.drawSquare(j, i, TextColor.ANSI.GREEN)
            }
        }
        tileDrawer.drawSquareInner(model.getSelectedColumn(),model.getSelectedRow(), TextColor.ANSI.RED)
        tileDrawer.refresh()
    }

    private fun keyStrokeToMovement(keystroke: KeyStroke): Control? {
        val result: Control? = when (keystroke.keyType) {
            KeyType.ArrowLeft -> Control.Left
            KeyType.ArrowRight -> Control.Right
            KeyType.ArrowUp -> Control.Up
            KeyType.ArrowDown -> Control.Down
            KeyType.MouseEvent -> {
                println("mouse clicked");null
            }

            else -> {
                when (keystroke.character) {
                    ' ' -> Control.Pause
                    'x' -> Control.Select

                    else -> {
                        null
                    }
                }
            }
        }
        return result
    }

    fun getPressedKey(): Control? {
        val input = terminal.pollInput()
        if (input != null) {
            return keyStrokeToMovement(input)
        }
        return null
    }
}


class Model(boardString: String) {
    private val board = Board.fromString(boardString)
    private var round = 0
    private var selectedRow = 0
    private var selectedColumn = 0

    fun getRound(): Int {
        return round
    }

    fun getSelectedRow(): Int {
        return selectedRow
    }

    fun getSelectedColumn(): Int {
        return selectedColumn
    }

    fun getBoard(): Board {
        return board
    }

    fun playRound() {
        board.updateBoard()
        round += 1
    }

    fun processInput(control: Control) {
        println("Selected: $selectedRow, $selectedColumn")
        when (control) {
            Control.Left -> selectedColumn = (selectedColumn - 1) % board.width
            Control.Right -> selectedColumn = (selectedColumn + 1) % board.width
            Control.Up -> selectedRow = (selectedRow - 1) % board.height
            Control.Down -> selectedRow = (selectedRow + 1) % board.height
            Control.Select -> board.toggle(selectedRow, selectedColumn)
            else -> {}
        }
    }
}


class Board(val height: Int, val width: Int) {
    private var gameBoard: Array<Array<Boolean>> = Array(height) { Array(width) { false } }

    constructor(gameBoard: Array<Array<Boolean>>) : this(gameBoard.size, gameBoard[0].size) {
        if (gameBoard.map { line -> line.size }.toSet().size != 1) {
            throw Error("Board must be a rectangle")
        }
        this.gameBoard = gameBoard
    }

    companion object {
        fun fromString(gameBoardAsString: String): Board {
            val gbAs2DCharList = gameBoardAsString.split("\n").map { line -> line.toCharArray() }
            println(gbAs2DCharList)
            return Board(gbAs2DCharList.map { line -> (line.map { cell -> cell == 'x' }).toTypedArray() }
                .toTypedArray())
        }
    }

    private fun countNeighbours(row: Int, column: Int): Int {
        var acc = 0
        for (i in max(0, row - 1)..min(row + 1, height - 1)) {
            for (j in max(0, column - 1)..min(column + 1, width - 1)) {
                if ((i != row || j != column) && gameBoard[i][j]) {
                    acc += 1
                }
            }
        }
        return acc
    }

    private fun livesNextRound(row: Int, column: Int): Boolean {
        val neighbours = countNeighbours(row, column)
        return gameBoard[row][column] && (neighbours == 2 || neighbours == 3) || !gameBoard[row][column] && neighbours == 3
    }

    private fun computeNextBoard(): Array<Array<Boolean>> {
        val nextBoard = Array(height) { Array(width) { false } }
        for (i in 0 until height) {
            for (j in 0 until width) {
                nextBoard[i][j] = livesNextRound(i, j)
            }
        }
        return nextBoard
    }

    fun updateBoard() {
        gameBoard = computeNextBoard()
    }

    override fun toString(): String {
        return "-".repeat(2 * width + 1) + "\n" + (gameBoard.map { line -> line.map { cell -> if (cell) "x" else " " } }
            .map { line -> "|" + line.joinToString(" ") + "|" }).joinToString("\n") + "\n" + "-".repeat(2 * width + 1)
    }

    fun getAt(row: Int, column: Int): Boolean {
        return gameBoard[row][column]
    }

    fun toggle(selectedRow: Int, selectedColumn: Int) {
        gameBoard[selectedRow][selectedColumn] = !gameBoard[selectedRow][selectedColumn]
    }
}