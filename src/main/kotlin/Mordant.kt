import com.github.ajalt.mordant.terminal.Terminal

class Mordant {
}

fun main() {
    val ter = Terminal()
    val x = ter.cursor
    x.move { setPosition(3,6)
    right(4)}
    ter.print("hello")
}