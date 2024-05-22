package org.example


import kotlin.math.min

//class RectangleDrawerMordant (private val terminal: Screen){
//    private var squareSize: Int = 3
//
//    //val terminal: Screen = DefaultTerminalFactory().createScreen()
//    private fun drawPixel(row: Int, column: Int) {
//        val emptyCell = fromCharacter(' ', TextColor.ANSI.RED, TextColor.ANSI.GREEN)[0]
//        terminal.setCharacter(2 * column, row, emptyCell)
//        terminal.setCharacter(2 * column + 1, row, emptyCell)
//        //terminal.refresh()
//    }
//
//
//    private fun computeMaxSquareSizeHelper(
//        gameBoardRows: Int,
//        gameBoardColumns: Int,
//        terminalRows: Int,
//        terminalColumns: Int
//    ): Int {
//        val maxSquareHeightPixels = terminalRows / gameBoardRows
//        val maxSquareWidthPixels = terminalColumns / (2 * gameBoardColumns)
//        return min(maxSquareHeightPixels, maxSquareWidthPixels)
//    }
//
//    private fun computeMaxSquareSize(gameBoardRows:Int, gameBoardColumns: Int, screenSize: TerminalSize): Int {
//        return (computeMaxSquareSizeHelper(gameBoardRows, gameBoardColumns, screenSize.rows, screenSize.columns))
//    }
//
//    fun setInitialSquareSize(gameBoardRows:Int, gameBoardColumns: Int) {
//        squareSize = computeMaxSquareSize(gameBoardRows, gameBoardColumns, terminal.terminalSize)
//    }
//
//    fun updateSquareSize(gameBoardRows:Int, gameBoardColumns:Int) {
//        val newSize: TerminalSize? = terminal.doResizeIfNecessary()
//        if (newSize != null) {
//            squareSize = computeMaxSquareSize(gameBoardRows, gameBoardColumns, newSize)
//        }
//    }
//
//    private fun drawSquareHelper(startRow: Int, startColumn: Int, size: Int) {
//        for (i in 0..<size) {
//            for (j in 0..<size) {
//                drawPixel(startRow + i, startColumn + j)
//            }
//        }
//        //terminal.refresh()
//    }
//
//    fun drawSquare(i: Int, j: Int, squareSize: Int) {
//        drawSquareHelper(i * squareSize, j * squareSize, squareSize)
//    }
//
//    fun clear() {
//        terminal.clear()
//    }
//
//    fun refresh() {
//        terminal.refresh()
//    }
//
//    fun getSquareSize(): Int {
//        return squareSize
//    }
//}