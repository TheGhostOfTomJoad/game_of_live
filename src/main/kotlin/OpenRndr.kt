import org.openrndr.draw.Drawer
import org.openrndr.internal.Driver
import org.openrndr.internal.RectangleDrawer
import org.openrndr.internal.gl3.DriverGL3
import org.openrndr.internal.gl3.DriverVersionGL
import  org.lwjgl.opengl.GL15.*
fun main() {
    val driver =  DriverGL3(DriverVersionGL.VERSION_4_6)
    Driver.driver = driver
    val z = RectangleDrawer()
//    val x = Drawer(driver)
    val y = Driver.driver
//    x.rectangle(1.0, 2.0, 3.0, 4.0)
}



