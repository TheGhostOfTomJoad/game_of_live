package org.example

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextCharacter.fromCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.Screen
import kotlin.math.min


class RectangleDrawer (private val terminal: Screen){
    private var squareSize: Int = 3

    //val terminal: Screen = DefaultTerminalFactory().createScreen()
    private fun drawPixel(row: Int, column: Int,color: TextColor) {
        val emptyCell = fromCharacter(' ', TextColor.ANSI.RED, color)[0]
        terminal.setCharacter(2 * column, row, emptyCell)
        terminal.setCharacter(2 * column + 1, row, emptyCell)
        //terminal.refresh()
    }
    

    private fun computeMaxSquareSizeHelper(
        gameBoardRows: Int,
        gameBoardColumns: Int,
        terminalRows: Int,
        terminalColumns: Int
    ): Int {
        val maxSquareHeightPixels = terminalRows / gameBoardRows
        val maxSquareWidthPixels = terminalColumns / (2 * gameBoardColumns)
        return min(maxSquareHeightPixels, maxSquareWidthPixels)
    }

    private fun computeMaxSquareSize(gameBoardRows:Int, gameBoardColumns: Int, screenSize: TerminalSize): Int {
        return (computeMaxSquareSizeHelper(gameBoardRows, gameBoardColumns, screenSize.rows, screenSize.columns))
    }

    private fun setSquareSize(gameBoardRows:Int, gameBoardColumns: Int, terminalSize: TerminalSize) {
        squareSize = computeMaxSquareSize(gameBoardRows, gameBoardColumns,terminalSize)
    }

    fun setInitialSquareSize(gameBoardRows:Int, gameBoardColumns: Int) {
        setSquareSize(gameBoardRows, gameBoardColumns, terminal.terminalSize)
    }

    fun updateSquareSize(gameBoardRows:Int, gameBoardColumns:Int) {
        val newSize: TerminalSize? = terminal.doResizeIfNecessary()
        if (newSize != null) {
            setSquareSize(gameBoardRows, gameBoardColumns, newSize)
        }
    }

    private fun drawSquareHelper(startRow: Int, startColumn: Int, size: Int,color: TextColor) {
        for (i in 0..<size) {
            for (j in 0..<size) {
                drawPixel(startRow + i, startColumn + j,color)
            }
        }
        //terminal.refresh()
    }

    fun drawSquare(i: Int, j: Int,color: TextColor) {
        drawSquareHelper(i * squareSize, j * squareSize, squareSize, color)
    }

    fun drawSquareBorder(i: Int, j: Int,color: TextColor) {
        drawSquareHelper(i * squareSize +1, j * squareSize + 1, squareSize -1, color)
    }


    fun clear() {
        terminal.clear()
    }

    fun refresh() {
        terminal.refresh()
    }

    fun getSquareSize(): Int {
        return squareSize
    }
}