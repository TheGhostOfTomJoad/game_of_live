package org.example


import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.lang.Thread.sleep
import kotlin.math.max
import kotlin.math.min

enum class Control {
    Left,
    Right,
    Up,
    Down,
    Tab
}

fun main() {


    //val model = Model(
    //    " x x x x \n" + "x x x x x\n" + "         "
    //)
//    //println(model.getBoard().countNeighbours(1,0))
    //val gui = GUI()
    val controller = Controller()
    controller.playGame()
//    gui.initTerminalConfig()
//    gui.setInitialSquareSize(model.getBoard())
//    gui.printBoardLaterna(0,model.getBoard())

    //gui.drawPixel(3,0)
    //gui.drawPixel(4,0)
    //gui.drawSquare(1,4,5)
//    val t = Terminal()
//    t.println((red on blue)("You can use any of the standard ANSI colors"))
//    t.cursor.move {
//        up(3)
//        startOfLine()
//        clearScreenAfterCursor()
//    }
//    t.cursor.hide(showOnExit = true)
//    t.println((red on blue)("You can use any of the standard ANSI colors"))
}


class Controller {
    private val gui = GUI()
    private val model = Model(
        "          \n" +
                "          \n" +
                "   x      \n" +
                "  xx x    \n" +
                "      x   \n" +
                "   x      \n" +
                "    x xx  \n" +
                "      x   \n" +
                "          \n" +
                "          "
    )

    fun playGame() {
        gui.initTerminalConfig()
        gui.setInitialSquareSize(model.getBoard())

        var ongoing = true
//    var mousePos: Pair<Int, Int>?
        while (true) {
            if (ongoing) {
                //gui.printGame(model.getRound(), model.getBoard())
                gui.printBoardLaterna(model.getRound(), model.getBoard())
                sleep(500)
                model.playRound()
            }
            val pressedKey = gui.getPressedKey()
            if (pressedKey != null) {
                if (pressedKey.character == ' ') {
                    ongoing = !ongoing
                }

                model.processInput(pressedKey.keyType)


            }
//        mousePos = gui.mouseDown()
//        println(mousePos)
//        if (mousePos != null) {
//            println("mousePos: $mousePos")
//        }

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

        //terminal.setCursorVisible(false)
    }

    fun setInitialSquareSize(board: Board) {
        tileDrawer.setInitialSquareSize(board.height, board.width)
    }

    fun printBoardLaterna(round: Int, gameBoard: Board) {
        tileDrawer.clear()
        tileDrawer.updateSquareSize(gameBoard.height, gameBoard.width)
        for (i in 0 until gameBoard.height) {
            for (j in 0 until gameBoard.width) {
                if (gameBoard.getAt(i, j)) tileDrawer.drawSquare(i, j, tileDrawer.getSquareSize())
            }
        }
        tileDrawer.refresh()
    }

    fun spaceIsPressed(): Boolean {
        val input = terminal.pollInput()
        return if (input != null) {
            input.character == ' '
        } else {
            false
        }
    }

    fun getPressedKey(): KeyStroke? {
        return terminal.pollInput()

    }


}


//    fun mouseDown(): Pair<Int, Int>? {
//        val input = terminal.pollInput()
//        println(input != null)
//        println(input is MouseAction)
//        return if (input != null && input is MouseAction &&input.isMouseDown) {
//            Pair   (input.position.column / (squareSize * 2),input.position.column / squareSize)
//        } else {
//            null
//        }
//    }


class Model(boardString: String) {
    //    var firstGameBoard = Board(height, width)
//    var secondGameBoard = Board(height, width)
    private val board = Board.fromString(boardString)
    private var round = 0
    private var selectedX = 0
    private var selectedY = 0

    fun getRound(): Int {
        return round
    }


    fun getBoard(): Board {
        return board
    }
//
//    fun getCurrentGameBoard(): Board {
//        if (round % 2 == 0) {
//            return firstGameBoard
//        } else {
//            return secondGameBoard
//        }
//    }
//
//    fun getNextGameBoard(): Board {
//        if (round % 2 == 1) {
//            return firstGameBoard
//        } else {
//            return secondGameBoard
//        }
//    }
//
//
//    fun computeNextBoard(){
//        val currentBoard = getCurrentGameBoard()
//        var nextBoard = getNextGameBoard()
//    for  (i in 0 until height)  {
//        for  (j in 0 until width)  {
//            nextBoard.setCell(i, j, currentBoard.livesNextRound(i,j))
//        }
//    }
//    }

    fun playRound() {
        board.updateBoard()
        round += 1
    }

    fun processInput(keyType: KeyType) {
        when (keyType) {
            KeyType.ArrowLeft -> selectedX = (selectedX - 1) % board.width
            KeyType.ArrowRight -> selectedX = (selectedX + 1) % board.width
            KeyType.ArrowUp -> selectedY = (selectedX - 1) % board.height
            KeyType.ArrowDown -> selectedY = (selectedX + 1) % board.height
            KeyType.MouseEvent -> println("mouse clicked")
            else -> {}
        }
    }


}


class Board(val height: Int, val width: Int) {
    private var gameBoard: Array<Array<Boolean>> = Array(height) { Array(width) { false } }
    // var secondGameBoard:Array<Array<Boolean>> =  Array(height) { Array(width) { false } }
    // var round = 0

    //    fun getCurrentGameBoard(): Array<Array<Boolean>> {
//        if (round % 2 == 0) {return fistGameBoard}
//
//        else{return secondGameBoard}
//    }
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
                //print("line${row + i} column ${column + j} ")
                if ((i != row || j != column) && gameBoard[i][j]) {
                    acc += 1
                    //  print("alive")
                }
                //println()
            }
        }
        return acc
    }


//    private fun isOnBoard(row: Int, column: Int): Boolean {
//        return row >= 0 && column >= 0 && row < width && column < height
//    }

//    private fun isLivingCell(row: Int, column: Int): Boolean {
//        return if (isOnBoard(row, column)) {
//            gameBoard[row][column]
//        } else {
//            false
//        }
//    }


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

}