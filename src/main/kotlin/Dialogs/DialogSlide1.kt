package Dialogs

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.launch
import org.openrndr.math.Vector2
import org.openrndr.math.Vector4
import org.openrndr.panel.elements.*
import org.openrndr.panel.style.*
import org.openrndr.resourceUrl
import org.openrndr.shape.Rectangle
import org.openrndr.text.Writer
import types.Dialog


var dialogStyle = listOf(

    // code
    styleSheet(has class_ "dialog") {
        position = Position.FIXED
        width = 100.percent
        height = 100.percent
        zIndex = ZIndex.Value(1)
        left = 0.px
        top = 0.px
        background = Color.RGBa(ColorRGBa.BLACK.opacify(0.85))

        child(has class_ "canvas") {
            marginLeft = 200.px
            marginTop = 300.px
            width = (1200).px
            height = (400).px
            zIndex = ZIndex.Value(1)
        }

        child(has class_ "background") {
            position = Position.FIXED
            marginLeft = 0.px
            marginTop = 0.px
            width = (1600).px
            height = (1000).px
            zIndex = ZIndex.Value(0)
        }

        child(has class_ "buttons") {
            marginLeft = 1180.px
            width = 220.px
            display = Display.FLEX
            flexDirection = FlexDirection.Row
            //background = Color.RGBa(ColorRGBa.BLACK.opacify(0.9))
            child(has class_ "button") {
                width = 100.px
            }
        }
})


fun Body.dialog() : Dialog {

    val result = Dialog()

    lateinit var font: FontImageMap
    font = FontImageMap.fromUrl("file:data/fonts/Roboto-Regular.ttf", 56.0, 2.0)
    class Motion(var slider:Double) : Animatable()
    val motion = Motion(0.0)
    val motion2 = Motion(0.0)


    contour.resolution = Vector2(1600.0, 1000.0)
    var cb : ColorBuffer
    cb = colorBuffer(1600, 1000, 2.0)
    var rt = renderTarget(1600, 1000) {
        colorBuffer()
    }

    motion.delay(500)
    motion.animate("slider", 1.0, 1000, Easing.CubicInOut)
    motion2.animate("slider", 1.0, 3000, Easing.CubicInOut)

    layout {
        div("dialog") {
            canvas("background") {
                draw {
                    drawer ->
                    (root() as Body).controlManager.program.launch {
                        this@canvas.draw.dirty = true
                    }

                    contour.time = frame
                    contour.slider = motion2.slider
                    contour.apply(rt.colorBuffer(0), cb)
                    drawer.image(cb)
                    frame += 0.005

                    motion2.updateAnimation()
                }
            }

            canvas("canvas") {
                 draw {
                     drawer ->
                     (root() as Body).controlManager.program.launch {
                         this@canvas.draw.dirty = true
                     }

                     drawer.fontMap = font
                     var writer = Writer(drawer)
                     writer.box = Rectangle(90.0, 50.0+(20*motion.slider), 1020.0, 200.0)
                     drawer.fill = ColorRGBa.WHITE.opacify(motion.slider)

                     writer.text("Governments are unique, every single one of them makes different decisions, resulting in a very diverse landscape of outcomes. How can we compare them?")

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
                            dialogSlide2()
                        })
                        this@layout.remove(this)
                    }
                }
                button("button") {
                    label = "Skip"
                    events.clicked.subscribe {
                        this@layout.remove(thisDiv)
                        this@layout.append(apply {
                            dialogSlideApp()
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

