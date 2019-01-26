import Dialogs.*
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import functions.TSNEBuild
import functions.checkToggleStatus
import functions.setDiagram
import org.openrndr.PresentationMode
import org.openrndr.animatable.Animatable
import org.openrndr.color.mix
import java.io.File
import org.openrndr.math.Vector2
import org.openrndr.math.map
import org.openrndr.panel.controlManager
import org.openrndr.panel.elements.*
import org.openrndr.panel.style.*
import org.openrndr.shape.Rectangle
import org.openrndr.draw.*
import org.openrndr.math.Vector4
import org.openrndr.resourceUrl
import types.Dialog
import types.movingPoint

fun main() = application {

    configure {
        width = 1600
        height = 1000
        title = "What Makes a \"Good\" Government?"
    }

    program {

        val cmView = controlManager {
            styleSheets(dialogStyle)
            styleSheets(dialogStyleLarge)
            styleSheets(dialogStyleFull)
            styleSheets(sideBarStyle)
            styleSheets(dialogStyleToggles)
            styleSheets(dialogSelect)

            layout {
                div("container") {
                    val dialog = (this@layout).dialog()
                }
            }
        }

        extend(cmView)
        extend {
        }
    }
}
