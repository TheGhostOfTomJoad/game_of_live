package org.example

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextCharacter.fromCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlin.math.min


class RectangleDrawer(private val terminal: Screen) {
    private var squareSize: Int = 3
    private fun drawPixel(column: Int,row: Int,  color: TextColor) {
        val emptyCell = fromCharacter(' ', TextColor.ANSI.RED, color)[0]
        terminal.setCharacter(2 * column, row, emptyCell)
        terminal.setCharacter(2 * column + 1, row, emptyCell)
    }

    private fun computeMaxSquareSizeHelper(
        squaresInColumn: Int, squaresInRow: Int, terminalColumns: Int, terminalRows: Int
    ): Int {
        val maxSquareHeightPixels = terminalRows / squaresInRow
        val maxSquareWidthPixels = terminalColumns / (2 * squaresInColumn)
        return min(maxSquareHeightPixels, maxSquareWidthPixels)
    }

    private fun computeMaxSquareSize(squaresInColumn: Int,squaresInRow: Int, screenSize: TerminalSize): Int {
        return computeMaxSquareSizeHelper(squaresInColumn, squaresInRow,screenSize.columns, screenSize.rows)
    }

    private fun setSquareSize(squaresInColumn: Int, squaresInRow: Int, terminalSize: TerminalSize) {
        squareSize = computeMaxSquareSize(squaresInColumn, squaresInRow, terminalSize)
    }

    fun setInitialSquareSize(squaresInColumn: Int, squaresInRow: Int) {
        setSquareSize(squaresInColumn, squaresInRow,  terminal.terminalSize)
    }

    fun updateSquareSize(squaresInColumn: Int, squaresInRow: Int) {
        val newSize: TerminalSize? = terminal.doResizeIfNecessary()
        if (newSize != null) {
            setSquareSize(squaresInColumn, squaresInRow,  newSize)
        }
    }

    private fun drawSquareHelper(startPixelX: Int, startPixelY: Int, size: Int, color: TextColor) {
        for (i in 0..<size) {
            for (j in 0..<size) {
                drawPixel(startPixelX + j,startPixelY + i, color)
            }
        }
    }

    fun drawSquare(indexX: Int, indexY: Int, color: TextColor) {
        drawSquareHelper(indexX * squareSize, indexY * squareSize,  squareSize, color)
    }

    fun drawSquareInner(indexX: Int, indexY: Int, color: TextColor) {
        drawSquareHelper(indexX * squareSize + 1,indexY * squareSize + 1, squareSize - 2, color)
    }

    fun clear() {
        terminal.clear()
    }

    fun refresh() {
        terminal.refresh()
    }

    fun startScreen(){
        terminal.startScreen()
    }
}

fun main(){
    val t = DefaultTerminalFactory().createScreen()
    val x = RectangleDrawer(t)
    x.startScreen()
    x.drawSquare(10,2,TextColor.ANSI.BLUE)
    x.refresh()
}