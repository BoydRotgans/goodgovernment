package Dialogs

import functions.checkToggleStatus
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.FontImageMap
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.renderTarget
import org.openrndr.launch
import org.openrndr.math.Vector2
import org.openrndr.panel.elements.*
import org.openrndr.panel.style.*
import org.openrndr.shape.Rectangle
import org.openrndr.text.Writer
import types.Dialog
import java.io.File

var dialogStyleToggles = listOf(
        styleSheet(has class_ "dialogToggles") {
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

            child(has class_ "holder") {
                display = Display.FLEX
                flexDirection = FlexDirection.Row
                width = 550.px
                marginLeft = 200.px
                child(has class_ "outerdivleft") {
                    display = Display.FLEX
                    flexDirection = FlexDirection.Column
                    width = 400.px
                    height = 600.px
                    marginRight = 0.px
                    marginLeft = 100.px
                    marginTop = 0.px
                    marginBottom = 0.px
                    zIndex = ZIndex.Value(1)
                    child(has class_ "box") {
                        display = Display.FLEX
                        flexDirection = FlexDirection.Column
                        width = 500.px
                        height = 150.px
                        marginLeft = 0.px
                        fontSize = 30.px
                        child(has class_ "toggle") {
                            width = 30.px
                            height = 30.px
                        }
                    }
                }
                child(has class_ "outerdivright") {
                    display = Display.FLEX
                    flexDirection = FlexDirection.Column
                    width = 400.px
                    height = 600.px
                    marginRight = 0.px
                    marginLeft = 0.px
                    marginTop = 0.px
                    marginBottom = 0.px
                    zIndex = ZIndex.Value(1)
                    child(has class_ "box") {
                        display = Display.FLEX
                        flexDirection = FlexDirection.Column
                        width = 500.px
                        height = 150.px
                        marginLeft = 0.px
                        fontSize = 30.px
                        child(has class_ "toggle") {
                            width = 30.px
                            height = 30.px
                        }
                    }
                }
            }
            child(has class_ "canvas") {
                marginLeft = 200.px
                marginTop = 0.px
                width = (1200).px
                height = (200).px
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




fun Body.dialogSlide3() : Dialog {

    val result = Dialog()

    lateinit var font: FontImageMap
    font = FontImageMap.fromUrl("file:data/fonts/Roboto-Regular.ttf", 56.0, 2.0)

    class Motion(var slider:Double) : Animatable()

    var motion = Motion(0.0)

    motion.animate("slider", 1.0, 1000, Easing.CubicInOut)

    var labels = File("data/dimensions.txt").readLines()

    contour.resolution = Vector2(1600.0, 1000.0)
    var cb : ColorBuffer
    cb = colorBuffer(1600, 1000, 2.0)
    var rt = renderTarget(1600, 1000) {
        colorBuffer()
    }

    var direction = 0.01


    layout {
        div("dialogToggles") {
            canvas("background") {
                draw {
                    drawer ->
                    (root() as Body).controlManager.program.launch {
                        this@canvas.draw.dirty = true
                    }
                    contour.time = frame
                    contour.apply(rt.colorBuffer(0), cb)
                    drawer.image(cb)
                    frame += direction
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
                     writer.text("Select the topics you would like to explore")

                     // show image
                     motion.updateAnimation()
                 }
             }



            div("holder") {
                div("outerdivleft") {
                    div("box") {
                        text("Country")
                        toggles.add(toggle("toggle") { this.label = labels[0]; this.value = true; })
                        toggles.add(toggle("toggle") { this.label = labels[1]; this.value = false; })
                    }
                    div("box") {
                        text("Health")
                        toggles.add(toggle("toggle") { this.label = labels[10]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[11]; this.value = false; })
                    }
                    div("box") {
                        text("Society")
                        toggles.add(toggle("toggle") { this.label = labels[4]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[14]; this.value = false; })
                    }
                }

                div("outerdivright") {
                    div("box") {
                        text("Happiness")
                        toggles.add(toggle("toggle") { this.label = labels[3]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[5]; this.value = false; })
                    }
                    div("box") {
                        text("Politics and law")
                        toggles.add(toggle("toggle") { this.label = labels[21]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[27]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[24]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[19]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[18]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[23]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[22]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[31]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[20]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[26]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[25]; this.value = false; })
                    }
                }
                div("outerdivright") {
                    div("box") {
                        text("Economy")
                        toggles.add(toggle("toggle") { this.label = labels[2]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[7]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[8]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[9]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[6]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[12]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[13]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[16]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[17]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[15]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[30]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[29]; this.value = false; })
                        toggles.add(toggle("toggle") { this.label = labels[28]; this.value = false; })
                    }
                }
            }

            toggles.forEachIndexed { index, toggle ->
                toggle.events.valueChanged.subscribe {
                    checkToggleStatus(false, "slide3")
                }
            }

            var thisDiv = this@div
            div("buttons") {
                button("button") {
                    label = "Next"
                    events.clicked.subscribe {
                        this@layout.remove(thisDiv)
                        this@layout.append(apply {
                            dialogSlide4()
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
