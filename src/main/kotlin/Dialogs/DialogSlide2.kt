package Dialogs

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

var dialogStyleLarge = listOf(

    styleSheet(has class_ "dialogLarge") {
        position = Position.FIXED
        width = 100.percent
        height = 100.percent
        zIndex = ZIndex.Value(1)
        left = 0.px
        top = 0.px
        background = Color.RGBa(ColorRGBa.BLACK.opacify(0.9))

        child(has class_ "canvas") {
            marginLeft = 200.px
            marginTop = 100.px
            width = (1200).px
            height = (800).px
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

fun Body.dialogSlide2() : Dialog {

    val result = Dialog()

    lateinit var font: FontImageMap
    font = FontImageMap.fromUrl("file:data/fonts/Roboto-Regular.ttf", 56.0, 2.0)
    var island= ColorBuffer.fromFile("data/images/island.png")
    class Motion(var slider:Double) : Animatable()
    var motion = Motion(0.0)

    motion.animate("slider", 1.0, 1000, Easing.CubicInOut)

    class Particle : Animatable() {
        var normal = Vector2(Math.random()*10.0, Math.random()*10.0)

        var random = Vector2(Math.random()*300.0 + 100, Math.random()*300.0 + 100)
        var posx = random.x
        var posy = random.y
        var color = ColorRGBa.RED

        fun animate(index: Int) {
            if(!this.hasAnimations()) {
                this.delay((100+Math.random()*300).toLong())
                this.animate("posx", random.x, 2000, Easing.CubicInOut)
                this.animate("posy", random.y, 2000, Easing.CubicInOut)
                this.complete()
                this.delay(2000)
                this.animate("posx", normal.x, 2000, Easing.CubicInOut)
                this.animate("posy", normal.y, 2000, Easing.CubicInOut)
                this.complete()
            }

        }
    }

    var particles = mutableListOf<Particle>()

    for(i in 0 until 3 ) {

        if(i == 0) {
            for(x in 0 until 13 ) {
                var p = Particle()
                p.color = ColorRGBa.BLUE
                p.normal = Vector2(350.0 + Math.random() * 60.0, 100.0 + Math.random() * 100.0)
                particles.add(p)
            }
        }
        if(i == 1) {
            for(x in 0 until 13 ) {
                var p = Particle()
                p.color = ColorRGBa.RED
                p.normal = Vector2(300.0 + Math.random() * 50.0, 320.0 + Math.random() * 50.0)
                particles.add(p)
            }
        }
        if(i == 2) {
            for(x in 0 until 13 ) {
                var p = Particle()
                p.color = ColorRGBa.WHITE
                p.normal = Vector2(100.0 + Math.random() * 100.0, 220.0 + Math.random() * 50.0)
                particles.add(p)
            }
        }


    }




    contour.resolution = Vector2(1600.0, 1000.0)
    var cb : ColorBuffer
    cb = colorBuffer(1600, 1000, 2.0)
    var rt = renderTarget(1600, 1000) {
        colorBuffer()
    }

    layout {
        div("dialogLarge") {
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
                     writer.box = Rectangle(100.0, 50.0+(20*motion.slider), 1000.0, 700.0)
                     drawer.fill = ColorRGBa.WHITE.opacify(motion.slider)
                     writer.text("In this tool you can explore 195 governments and build correlation maps to explore their similarities and contradictions. \n" +
                             "\n" +
                             "A correlation map can be seen as an island where people that are more similar are standing closer together.")

                     drawer.translate(Vector2(400.0, 350.0))
                     //drawer.image(island)

                     particles.forEachIndexed { index, it ->
                         it.animate(index)
                         it.updateAnimation()
                         drawer.fill = it.color
                         drawer.circle(it.posx, it.posy, 8.0)
                     }

                     // show image
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
                            dialogSlide3()
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

