import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.V2
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlinx.coroutines.delay


data class Model(var number: Int)

@Composable
@Preview
fun App() {
    val requester = remember { FocusRequester() }

    val snakeState by remember { mutableStateOf(SnakeModel(20, 20)) }
    var snakeCoords by remember { mutableStateOf(snakeState.getSnakeCoordinates()) }
    var appleCoords by remember { mutableStateOf(snakeState.getAppleCoordinates()) }

    MaterialTheme {

        if (snakeState.gameLost()) {
            Text("Game Over. You have " + snakeState.snakeLen() + " Points!")

        } else {


            LaunchedEffect(Unit) {

                while (!snakeState.gameIsWon() && !snakeState.gameLost()) {
                    delay(1000L)
                    snakeState.playRound()
                    snakeCoords = snakeState.getSnakeCoordinates()
                    appleCoords = snakeState.getAppleCoordinates()
                }
            }

            Row(Modifier.fillMaxSize().focusRequester(focusRequester = requester)
                .focusable().onPreviewKeyEvent {
                    println("1")
                    val maybeDir = convertDirection(it.key)
                    if (maybeDir != null) {
                        println("2")
                        snakeState.setSnakeDirection(maybeDir)
                    }
                    true
                }.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // To disable the ripple effect
                ) {
                    requester.requestFocus()
                }) {

                Column {
                    makeDirectionButton(snakeState, Direction.North)
                    makeDirectionButton(snakeState, Direction.East)
                    makeDirectionButton(snakeState, Direction.West)
                    makeDirectionButton(snakeState, Direction.South)
                    Button(onClick = {
                        snakeState.playRound()
                        snakeCoords = snakeState.getSnakeCoordinates()
                        appleCoords = snakeState.getAppleCoordinates()
                    }) {
                        Text("Run")
                    }
                }

                Board(snakeCoords, appleCoords, snakeState.rows, snakeState.cols)

            }
        }
    }
}


fun convertDirection(key: Key): Direction? {
    return when (key) {
        Key.DirectionRight -> Direction.East
        Key.DirectionLeft -> Direction.West
        Key.DirectionUp -> Direction.North
        Key.DirectionDown -> Direction.South
        else -> {
            null
        }
    }
}

@Composable
fun makeDirectionButton(sn: SnakeModel, direction: Direction) {
    Button(onClick = {
        sn.setSnakeDirection(direction)
    }) {
        Text(direction.toString())
    }
}


@Composable
fun Board(snake: List<V2>, apple: V2, gameRows: Int, gameColumns: Int) {
    BoxWithConstraints() {
        val tileSize = min(maxWidth / gameColumns, maxHeight / gameRows)
        Box(
            Modifier.size(tileSize * gameColumns, tileSize * gameRows).border(10.dp, Black)
        )

        drawBox(apple.x, apple.y, tileSize, Red)
        snake.forEach {
            drawBox(it.x, it.y, tileSize, Green)
        }
    }
}


@Composable
fun drawBox(xIndex: Int, yIndex: Int, tileSize: Dp, bgColor: Color) {
    Box(
        Modifier.size(tileSize).offset(x = tileSize * xIndex, y = tileSize * yIndex).size(tileSize).background(bgColor)
    )
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}