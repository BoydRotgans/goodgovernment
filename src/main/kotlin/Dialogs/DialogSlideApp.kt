package Dialogs

import jujutsu.tsne.barneshut.BarnesHutTSne.drawer
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.FontImageMap
import org.openrndr.launch
import org.openrndr.math.Vector2
import org.openrndr.panel.elements.*
import org.openrndr.panel.style.*
import org.openrndr.shape.Rectangle
import org.openrndr.text.Writer
import types.Dialog
import types.Island

var dialogStyleFull = listOf(
    styleSheet(has class_ "dialogFull") {
        position = Position.FIXED
        width = 1000.px
        height = 100.percent
        zIndex = ZIndex.Value(0)
        left = 0.px
        top = 0.px

        child(has class_ "canvas") {
            marginLeft = 0.px
            marginTop = 0.px
            width = 1000.px
            height = 100.percent
        }
})

fun Body.dialogSlideApp() : Dialog {

    var sidebar = sideBar()

    val result = Dialog()


    lateinit var font: FontImageMap
    font = FontImageMap.fromUrl("file:data/fonts/Roboto-Regular.ttf", 56.0, 2.0)

    class Motion(var slider:Double) : Animatable()

    var motion = Motion(0.0)

    motion.animate("slider", 1.0, 2000, Easing.CubicInOut)

    layout {
        div("dialogFull") {
            canvas("canvas") {

                var island = Island()
                var mouseposition = Vector2(0.0, 0.0)

                draw {
                    drawer ->

                    mouse.moved.subscribe() {
                        mouseposition = it.position
                    }

                    (root() as Body).controlManager.program.launch {
                        this@canvas.draw.dirty = true
                    }

                    //drawer.background(ColorRGBa.YELLOW.shade(motion.slider))
                    //drawer.fontMap = font
                    island.draw(drawer, mouseposition)
                    motion.updateAnimation()
                }
            }

            var thisDiv = this@div
            div("buttons") {
                button("button") {
                    label = "Next"
                    events.clicked.subscribe {
                        this@layout.remove(thisDiv)
                        this@layout.append(apply {

                        })
                        this@layout.remove(this)
                    }
                }
            }

            result.dispose = { this@layout.remove(this@div) }
        }
    }

    return result

}

