
class PongModel {



}

class Ball {
    var ballX = 5.0
    var ballY = 5.0
    var ballVX = 0.5
    var ballVY = 0.2

    fun move(){
        ballX += ballVX
        ballY += ballVY
    }
}