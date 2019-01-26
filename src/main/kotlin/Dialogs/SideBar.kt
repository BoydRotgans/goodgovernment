package Dialogs

import functions.checkToggleStatus
import functions.returnLayoutIndexSidebar
import functions.setToggleStatus
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.FontImageMap
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.launch
import org.openrndr.math.Vector2
import org.openrndr.panel.elements.*
import org.openrndr.panel.style.*
import org.openrndr.shape.Rectangle
import org.openrndr.text.Writer
import saveFileDialog
import types.Dialog
import java.io.File


var sideBarStyle = listOf(
        styleSheet(has class_ "outer") {
        display = Display.FLEX
        flexDirection = FlexDirection.Column
        width = 600.px
        height = 100.percent
        marginRight = 0.px
        marginLeft = 1000.px
        marginTop = 0.px
        marginBottom = 0.px
        background = Color.RGBa(ColorRGBa.GRAY.shade(0.9))
        zIndex = ZIndex.Value(1)

        child(has class_ "holder") {
            display = Display.FLEX
            flexDirection = FlexDirection.Row
            width = 550.px
            marginLeft = 20.px
            marginTop = 20.px
            child(has class_ "outerdivleft") {
                display = Display.FLEX
                flexDirection = FlexDirection.Column
                width = 300.px
                height = 760.px
                marginRight = 0.px
                marginLeft = 10.px
                marginTop = 0.px
                marginBottom = 0.px
                //paddingLeft = 20.px
                zIndex = ZIndex.Value(1)
                //background = Color.RGBa(ColorRGBa.RED.shade(0.9))
                child(has class_ "box") {
                    display = Display.FLEX
                    flexDirection = FlexDirection.Column
                    width = 300.px
                    height = 125.px
                    marginLeft = 0.px
                    marginBottom = 0.px
                    fontSize = 30.px
                    child(has class_ "toggle") {
                        width = 25.px
                        height = 25.px
                    }
                }
            }
            child(has class_ "outerdivright") {
                display = Display.FLEX
                flexDirection = FlexDirection.Column
                width = 300.px
                height = 760.px
                marginRight = 0.px
                marginLeft = 0.px
                marginTop = 0.px
                marginBottom = 0.px
                //background = Color.RGBa(ColorRGBa.RED.shade(0.9))
                zIndex = ZIndex.Value(1)
                child(has class_ "box") {
                    display = Display.FLEX
                    flexDirection = FlexDirection.Column
                    width = 300.px
                    height = 125.px
                    marginLeft = 0.px
                    fontSize = 30.px
                    marginBottom = 0.px

                    child(has class_ "toggle") {
                        width = 25.px
                        height = 25.px
                    }
                }
            }
        }

        child(has class_ "header") {
            paddingLeft = 15.px
            paddingTop = 30.px
            width = 100.percent
            height = 70.px
            fontSize = 30.px
            flexDirection = FlexDirection.Row
            display = Display.FLEX
            //background = Color.RGBa(ColorRGBa.RED.shade(0.9))
            child(has class_ "button") {
                marginLeft = 10.px
            }
            child(has class_ "dropdown") {

                marginLeft = 133.px
                width = 200.px
            }
            child(has class_ "canvas") {
                marginLeft = 10.px
                width = 40.px
                height = 40.px
            }

            child(has class_ "description") {
                width = 100.percent
                //background = Color.RGBa(ColorRGBa.RED.shade(0.9))
                this.fontSize = 20.px
                height = 25.px
                marginBottom = 0.px
                marginLeft = 6.px

            }
//            descendant(has class_ "text") {
//                color = Color.RGBa(ColorRGBa.BLACK)
//            }
//            child(has class_ "title") {
//                width = 100.percent
////                        background = Color.RGBa(ColorRGBa.RED.shade(0.9))
//            }
        }


        child(has class_ "footer") {
            display = Display.FLEX
            flexDirection = FlexDirection.Row
            descendant(has class_ "themes") {
                width = (30).px
                paddingTop = 20.px
                paddingBottom = 20.px
                paddingLeft = 25.px
                paddingRight = 20.px
                height = 130.px
                flexDirection = FlexDirection.Column
                child(has class_ "togglebig") {
                    width = 17.px
                    height = 17.px
                }
            }
        }

        child(has class_ "export") {
            child(has class_ "button") {
                paddingTop = 100.px
                marginLeft = 325.px
                paddingBottom = 25.px
                fontSize = 30.px
                height = 50.px
            }
        }

        child(has class_ "dropdown") {
            width = 250.px
            marginLeft = 25.px
            background = Color.RGBa(ColorRGBa.GRAY.shade(0.8))
            child(has class_ "item"){
                width = 500.px
                color = Color.RGBa(ColorRGBa.GRAY.shade(0.8))
            }
        }
    })


fun Body.sideBar() : Dialog {

    val result = Dialog()

    class Motion(var slider:Double) : Animatable()
    var motion = Motion(0.0)

    motion.animate("slider", 1.0, 1000, Easing.CubicInOut)

    toggles.clear()

    var labels = File("data/dimensions.txt").readLines()
    val countries = File("data/countries.txt").readLines()


    var index = 0
    var index2 = 0
            layout {
                div("outer") {
                    div("header") {
//                        div("title") {
//                            text("Theme")
//                        }
//                        div("title") {
//                           text("Highlight")
//                        }
                        button("button") {
                            this.label = "Select all"
                            this.events.clicked.subscribe {
                                setToggleStatus(true)
                            }
                        }

                        button("button") {
                            this.label = "Deselect all"
                            this.events.clicked.subscribe {
                                setToggleStatus(false)
                            }
                        }





                        dropdownButton("dropdown") {
                            this.label = "Highlight"
                            this.value = item {
                                this.label = countries.get(flagId)
                            }
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

                        canvas("canvas") {
                            draw {
                                it.background(ColorRGBa.TRANSPARENT)

                                it.pushStyle()
                                var x = 10.0
                                var y = 30.0

                                it.stroke = ColorRGBa.BLACK
                                it.lineSegment(Vector2(x, y), Vector2(x, y - 18))

                                it.fill = ColorRGBa.BLACK
                                it.rectangle(x-1, y-20, 17.0, 12.0)

                                it.image(flag,
                                        Rectangle(0.0, 0.0, flag.width*1.0, flag.height*1.0),
                                        Rectangle(x, y-19, 15.0, 10.0))

                                it.popStyle()

                            }
                        }




                    }

                    div("header") {
                        div("description") {
                            text("Explore correlations by prioritising")
                        }
                    }

                    div("holder") {
                        div("outerdivleft") {
                            div("box") {
                                text("Country")
                                toggles.add(toggle("toggle") { this.label = labels[0]; })
                                toggles.add(toggle("toggle") { this.label = labels[1]; })
                            }
                            div("box") {
                                text("Health")
                                toggles.add(toggle("toggle") { this.label = labels[10]; })
                                toggles.add(toggle("toggle") { this.label = labels[11]; })
                            }
                            div("box") {
                                text("Economy")
                                toggles.add(toggle("toggle") { this.label = labels[2]; })
                                toggles.add(toggle("toggle") { this.label = labels[7]; })
                                toggles.add(toggle("toggle") { this.label = labels[8]; })
                                toggles.add(toggle("toggle") { this.label = labels[9]; })
                                toggles.add(toggle("toggle") { this.label = labels[6]; })
                                toggles.add(toggle("toggle") { this.label = labels[12]; })
                                toggles.add(toggle("toggle") { this.label = labels[13]; })
                                toggles.add( toggle("toggle") { this.label = labels[16]; })
                                toggles.add( toggle("toggle") { this.label = labels[17]; })
                                toggles.add( toggle("toggle") { this.label = labels[15]; })
                                toggles.add( toggle("toggle") { this.label = labels[30]; })
                                toggles.add( toggle("toggle") { this.label = labels[29]; })
                                toggles.add( toggle("toggle") { this.label = labels[28]; })
                            }
                        }

                        div("outerdivright") {
                            div("box") {
                                text("Happiness")
                                toggles.add( toggle("toggle") { this.label = labels[3]; })
                                toggles.add( toggle("toggle") { this.label = labels[5]; })
                            }

                            div("box") {
                                text("Society")
                                toggles.add(toggle("toggle") { this.label = labels[4]; })
                                toggles.add( toggle("toggle") { this.label = labels[14]; })

                            }

                            div("box") {
                                text("Politics and law")
                                toggles.add( toggle("toggle") { this.label = labels[21]; })
                                toggles.add( toggle("toggle") { this.label = labels[27]; })
                                toggles.add( toggle("toggle") { this.label = labels[24]; })
                                toggles.add( toggle("toggle") { this.label = labels[19]; })
                                toggles.add( toggle("toggle") { this.label = labels[18]; })
                                toggles.add( toggle("toggle") { this.label = labels[23]; })
                                toggles.add( toggle("toggle") { this.label = labels[22]; })
                                toggles.add( toggle("toggle") { this.label = labels[31]; })
                                toggles.add( toggle("toggle") { this.label = labels[20]; })
                                toggles.add( toggle("toggle") { this.label = labels[26]; })
                                toggles.add( toggle("toggle") { this.label = labels[25]; })
                            }
                        }
                    }

                    toggles.forEachIndexed { index, toggle ->
                        toggle.value = recepi.get(returnLayoutIndexSidebar(index))
                        toggle.events.valueChanged.subscribe {
                            checkToggleStatus(true, "sidebar")
                        }
                    }

                    div("export") {
                        button("button") {
                            this.label = "Save as Image"
                            this.events.clicked.subscribe {
                                saveFileDialog(supportedExtensions = listOf("png")) {
                                    renderImage.colorBuffer(0).saveToFile(it)
                                }
                            }
                        }

                    }
                }
            }

    return result



}