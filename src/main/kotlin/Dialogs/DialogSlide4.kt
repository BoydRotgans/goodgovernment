package Dialogs

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.launch
import org.openrndr.math.Vector2
import org.openrndr.panel.elements.*
import org.openrndr.panel.style.*
import org.openrndr.shape.Rectangle
import org.openrndr.text.Writer
import types.Dialog
import java.io.File


var dialogSelect = listOf(

        // code
        styleSheet(has class_ "dialogSelect") {
            position = Position.FIXED
            width = 100.percent
            height = 100.percent
            zIndex = ZIndex.Value(1)
            left = 0.px
            top = 0.px
            background = Color.RGBa(ColorRGBa.BLACK.opacify(0.9))

            child(has class_ "background") {
                position = Position.FIXED
                marginLeft = 0.px
                marginTop = 0.px
                width = (1600).px
                height = (1000).px
                zIndex = ZIndex.Value(0)
            }

            child(has class_ "canvas") {
                marginLeft = 200.px
                marginTop = 100.px
                width = (1200).px
                height = (200).px
            }

            child(has class_ "box") {
                width = 1200.px
                height = 100.px
                marginLeft = 300.px
                child(has class_ "dropdown") {
                    width = 800.px
                    height = 50.px
                    fontSize = 30.px
                    paddingLeft = 10.px
                    paddingTop = 10.px
                    paddingRight = 10.px
                    paddingBottom = 10.px

                }
            }

            child(has class_ "buttons") {
                marginLeft = 1290.px
                width = 110.px
                display = Display.FLEX
                flexDirection = FlexDirection.Row
                //background = Color.RGBa(ColorRGBa.BLACK.opacify(0.9))
                child(has class_ "button") {
                    width = 100.px
                }
            }

        })


fun Body.dialogSlide4() : Dialog {

    val result = Dialog()
    lateinit var font: FontImageMap
    font = FontImageMap.fromUrl("file:data/fonts/Roboto-Regular.ttf", 56.0, 2.0)
    class Motion(var slider:Double) : Animatable()
    var motion = Motion(0.0)
    motion.animate("slider", 1.0, 1000, Easing.CubicInOut)
    val countries = File("data/countries.txt").readLines()

    contour.resolution = Vector2(1600.0, 1000.0)
    var cb : ColorBuffer
    cb = colorBuffer(1600, 1000, 2.0)
    var rt = renderTarget(1600, 1000) {
        colorBuffer()
    }

    layout {
        div("dialogSelect") {
            canvas("background") {
                draw {
                    drawer ->
                    (root() as Body).controlManager.program.launch {
                        this@canvas.draw.dirty = true
                    }
                    contour.time = frame
                    contour.apply(rt.colorBuffer(0), cb)
                    drawer.image(cb)
                    frame -= 0.005

                }
            }

             canvas("canvas") {
                 draw {
                     drawer ->
                     (root() as Body).controlManager.program.launch {
                         this@canvas.draw.dirty = true
                     }
                     //drawer.background(ColorRGBa.BLUE)
                     drawer.fontMap = font
                     var writer = Writer(drawer)
                     writer.box = Rectangle(100.0, 100.0+(20*motion.slider), 1000.0, 200.0)
                     drawer.fill = ColorRGBa.WHITE.opacify(motion.slider)
                     writer.text("The last step, select a country of interest.")

                     // show image
                     motion.updateAnimation()
                 }
            }

            div("box") {
                dropdownButton("dropdown") {
                    this.label = "country"

                    countries.forEachIndexed { index, s ->
                        this.item {
                            this.label = s
                            this.data = index
                            this.events.picked.subscribe() {
                                flagId = it.source.data as Int
                                flag = loadImage("data/flags/"+countries.get(flagId)+".png")
                            }
                        }
                    }
                }
            }

            var thisDiv = this@div
            div("buttons") {
                button("button") {
                    label = "Start"
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
