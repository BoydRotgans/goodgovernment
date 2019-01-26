package types


import Dialogs.*
import functions.TSNEBuild
import functions.returnIndexSidebarLayout
import functions.returnLayoutIndexSidebar
import functions.setDiagram
import jujutsu.tsne.barneshut.BarnesHutTSne.drawer
import jujutsu.utils.MatrixUtils
import org.openrndr.Mouse
import org.openrndr.color.ColorRGBa
import org.openrndr.color.mix
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.math.Vector4
import org.openrndr.math.map
import org.openrndr.panel.elements.Toggle
import org.openrndr.resourceUrl
import org.openrndr.shape.Rectangle
import org.openrndr.text.Writer
import java.io.File

class Island {

    class Contour : Filter(filterShaderFromUrl(resourceUrl("/shaders/contour.frag"))) {
        var resolution: Vector2 by parameters
        var colors: Array<Vector4> by parameters
        init {
            colors = (0..11).map {
                Vector4((0.8-it / 10.0), ( 0.8-it / 10.0), (0.8- it / 10.0), 1.0)
            }.toTypedArray()
        }
    }

    lateinit var contourOut: ColorBuffer
    var selectedTheme = 0
    var selectedHover = mutableListOf<Int>()
    var canvasWidth = 1000
    var canvasHeight = 1000
    var contour = Contour()

    var showThemes = false

    var rt1 = renderTarget(canvasWidth, canvasHeight, 2.0) {
        colorBuffer(type = ColorType.FLOAT16)
    }
    var rt2 = renderTarget(canvasWidth, canvasHeight, 2.0) {
        colorBuffer()
    }
    var rtBlops = renderTarget(canvasWidth, canvasHeight, 2.0) {
        colorBuffer(type = ColorType.FLOAT16)
    }

    var lastSelectionId = 0
    var flagview = loadImage("data/flags/netherlands.png")

    val OriginalCore = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Datasets-Original.csv"), ";")
    val DataCore = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Export-Ranked.csv"), ";")
    val NormalCore = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Export-Normal.csv"), ";")
    val MaxMin = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Export-MaxMin.csv"), ";")
    val Meta = File("data/WDVP-Meta-Original.csv").readLines()
    val Source = File("data/WDVP-Source-Original.csv").readLines()
    lateinit var font: FontImageMap
    lateinit var fontBold: FontImageMap
    lateinit var fontSmall : FontImageMap
    val countries = File("data/countries-short.txt").readLines()
    val countriesLong = File("data/countries.txt").readLines()
    val orderColumn = File("data/invertColumn.txt").readLines()
    var labels = File("data/dimensions.txt").readLines()
    var colors = File("data/colors.txt").readLines()

    var colorArray = mutableListOf<List<ColorRGBa>>()


    init {

        colors.forEach {
            if(it.equals("0")) { // Country
                colorArray.add(listOf(ColorRGBa.RED, ColorRGBa.GREEN))
            } else if(it.equals("1")) { // Happiness
                colorArray.add(listOf(ColorRGBa.BLACK, ColorRGBa.GREEN))
            } else if(it.equals("2")) { // Health
                colorArray.add(listOf(ColorRGBa.RED, ColorRGBa.BLUE))
            } else if(it.equals("3")) { // Society
                colorArray.add(listOf(ColorRGBa.WHITE, ColorRGBa(186/255.0,85/255.0,211/255.0)))
            } else if(it.equals("4")) { // Economy
                colorArray.add(listOf(ColorRGBa.WHITE, ColorRGBa(255/255.0,215/255.0,0/255.0)))
            } else if(it.equals("5")) { // Politics and law
                colorArray.add(listOf(ColorRGBa.BLUE, ColorRGBa(255/255.0,99/255.0,71/255.0)))
            }
        }

        // LOAD DATA
        contourOut = colorBuffer(canvasWidth, canvasHeight, 2.0)
        font = FontImageMap.fromUrl("file:data/fonts/Roboto-Regular.ttf", 16.0, 2.0)
        fontBold = FontImageMap.fromUrl("file:data/fonts/Roboto-Medium.ttf", 18.0, 2.0)
        fontSmall = FontImageMap.fromUrl("file:data/fonts/Roboto-Regular.ttf", 14.0, 2.0)

//        recepi.forEachIndexed { index, b ->
//            if (index < 2) {
//                recepi.set(index, true)
//            } else {
//                recepi.set(index, false)
//            }
//        }

        var tsneBuild = TSNEBuild()
        val tsnePoints = tsneBuild.consume(recepi)
        val points = mutableListOf<DoubleArray>()

        if (tsnePoints != null) {
            tsnePoints.forEachIndexed { index, doubles ->
                var da = DoubleArray(2)
                da.set(0, doubles.get(0))
                da.set(1, doubles.get(1))
                points.add(index, da)
            }
        }

        if (points != null) {
            for (i in 0 until points.size) {
                movingPointsX.add(movingPoint(points.get(i).get(0)))
                movingPointsY.add(movingPoint(points.get(i).get(1)))
            }
        }

        setDiagram(points, false)
    }

    fun draw(drawer: Drawer, mousePosition: Vector2) {

        // mouse: Mouse
        var mouse = mousePosition

        drawer.ortho()

        contour.resolution = Vector2(canvasWidth * 1.0, canvasHeight * 1.0)

        movingPointsX.forEachIndexed { index, movingPoint ->
            movingPointsX.get(index).updateAnimation()
            movingPointsY.get(index).updateAnimation()
        }

        drawer.isolatedWithTarget(rt1) {

            ortho(rt1)

            background(ColorRGBa.TRANSPARENT)
            drawer.fontMap = font
            drawer.pushStyle()
            drawer.drawStyle.blendMode = BlendMode.ADD

            drawer.shadeStyle = shadeStyle {
                fragmentTransform = """
                     x_fill.rgb *= 0.025 * smoothstep(0.5, 0.0, vec3( length( va_texCoord0.xy - vec2(0.5, 0.5) )));
                     x_fill.a = 1.0;
                 """.trimIndent()
            }

            movingPointsX.forEachIndexed { index, point ->

                val mapC = map(195.0, 0.0, 0.0, 1.0, DataCore.get(index).get(selectedTheme))


                var getColors = colorArray.get(selectedTheme)
                var mixCOlor = mix(getColors.get(0), getColors.get(1), mapC)

                var dataVal = OriginalCore.get(index).get(selectedTheme)
                if (dataVal.toDouble() == -999.0) {
                    mixCOlor = ColorRGBa.GRAY
                }

                drawer.fill = mixCOlor

                val x = movingPointsX.get(index).value
                val y = movingPointsY.get(index).value

                drawer.stroke = null
                drawer.circle(Vector2(x, y), 150.0)

            }

            drawer.popStyle()
        }

        drawer.isolatedWithTarget(rt2) {

            ortho(rt2)

            background(ColorRGBa.TRANSPARENT)
            drawer.fontMap = font

            movingPointsX.forEachIndexed { index, point ->

                var x = movingPointsX.get(index).value
                var y = movingPointsY.get(index).value

                var dataVal = OriginalCore.get(index).get(selectedTheme)

                var circles1 = mutableListOf<Vector2>()
                var circles2 = mutableListOf<Vector2>()

                if (dataVal.toDouble() != -999.0) {

                    circles1.add(Vector2(x, y))
                    drawer.fill = ColorRGBa.BLACK

                    drawer.text(countries.get(index), x + 8.0, y + 3.5)

                } else {

                    circles2.add(Vector2(x, y))

                }

                drawer.fill = ColorRGBa.WHITE
                drawer.stroke = null
                drawer.circles(circles1, 4.0)

                drawer.fill = ColorRGBa.GRAY
                drawer.stroke = null
                drawer.circles(circles2, 4.0)
            }

            // ////////
            // FLAG
            // ////////

            drawer.pushStyle()
            var x = movingPointsX.get(flagId).value
            var y = movingPointsY.get(flagId).value

            drawer.stroke = ColorRGBa.BLACK
            drawer.lineSegment(Vector2(x, y), Vector2(x, y - 18))

            drawer.fill = ColorRGBa.BLACK
            drawer.rectangle(x-1, y-20, 17.0, 12.0)

            drawer.image(flag,
                    Rectangle(0.0, 0.0, flag.width*1.0, flag.height*1.0),
                    Rectangle(x, y-19, 15.0, 10.0))

            //drawer.rectangle(Vector2(x, y - 19), 15.0, 10.0)

            drawer.popStyle()

            // ////////
            // LEGEND
            // ////////

            drawer.fill = ColorRGBa(0.7, 0.7, 0.7).opacify(0.3)
            if(showThemes) {
                drawer.fill = ColorRGBa(0.7, 0.7, 0.7)
            }

            drawer.rectangle(0.0, height - 90.0, 230.0, 90.0)

            drawer.fontMap = fontBold
            drawer.fill = ColorRGBa.BLACK
            drawer.text(labels.get(selectedTheme), 10.0, height - 65.0)

            var min = MaxMin.get(selectedTheme).get(0).toString()
            var max = MaxMin.get(selectedTheme).get(1).toString()

            drawer.fontMap = font
            drawer.text(min, 10.0, height - 20.0)
            drawer.text(max, (20 * 10.0) - 30.0, height - 20.0)


            var getColors = colorArray.get(selectedTheme)

            for (i in 0..20) {
                drawer.fill = (mix(getColors.get(0), getColors.get(1), i / 20.0)).opacify(0.5)
                drawer.rectangle(Rectangle(10.0 + (i * 10), height - 50.0, 10.0, 10.0))
            }

            // META

            var writer = Writer(drawer)
            writer.box = Rectangle(250.0, height-20.0, 750.0, 20.0)
            drawer.fill = ColorRGBa.BLACK
            writer.text(Meta.get(selectedTheme))


            // other options
            if(mousePosition.x < 230.0 && mousePosition.y > height-90) {
                showThemes = true
            }

            if(showThemes && (mousePosition.x > 230.0)) {
                showThemes = false
            }


            if(showThemes) {

                var hSpace = (height - 90.0)
                var step = hSpace / (labels.size)
                drawer.fontMap = fontSmall

                var calc = map(0.0, hSpace, 0.0, labels.size*1.0, mousePosition.y).toInt()
                if(calc > 31) { calc = 31 }
                if(calc < 0) { calc = 0 }

                selectedTheme = returnLayoutIndexSidebar(calc)

                labels.forEachIndexed { index, s ->

                    var indexNew = returnIndexSidebarLayout(index)

                    drawer.pushTransforms()
                    drawer.translate(Vector2(0.0, (indexNew * step * 1.0)))
                    drawer.fill = ColorRGBa.GRAY.opacify(0.5)
                    if(selectedTheme == index) {
                        drawer.fill = ColorRGBa.GRAY
                    }
                    rectangle(0.0, 0.0, 230.0, (step * 1.0) - 1.0)

                    drawer.fill = ColorRGBa.BLACK
                    drawer.text(labels.get(index), 10.0, 12.0)
                    var getColors = colorArray.get(index)


                    for (i in 0..20) {
                        drawer.fill = (mix(getColors.get(0), getColors.get(1), i / 20.0)).opacify(0.5)
                        drawer.rectangle(Rectangle(10.0 + (i * 10), 18.0, 10.0, 5.0))
                    }
                    drawer.popTransforms()
                }

            }

            // drawer.text(Meta.get(selectedTheme).toString(), 320.0, height-20.0)
            // drawer.text(Source.get(selectedTheme).toString(), 1000.0-150.0, height-20.0)


            // HOVERING

            if(showThemes == false && countTrue > 0) {
                selectedHover.clear()

                movingPointsX.forEachIndexed { index, movingPoint ->

                    var x1 = mouse.x
                    var y1 = mouse.y
                    var x2 = movingPointsX.get(index).value
                    var y2 = movingPointsY.get(index).value

                    var dist = Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1))

                    if (dist < 15) {
                        selectedHover.add(index)
                    }
                }

                if (selectedHover.size > 0) {

                    val selectionId = selectedHover.first()

                    val x2 = movingPointsX.get(selectionId).value
                    val y2 = movingPointsY.get(selectionId).value

                    if(lastSelectionId != selectionId) {
                        flagview = loadImage("data/flags/" + countriesLong.get(selectionId).replace(" ", "_") + ".png")
                    }
                    lastSelectionId = selectionId

                    drawer.fill = ColorRGBa.BLACK
                    drawer.circle(Vector2(x2 * 1.0, y2 * 1.0), 4.0)
                    drawer.pushTransforms()
                    drawer.translate(mouse.x, mouse.y)
                    drawer.fill = ColorRGBa.WHITE
                    drawer.rectangle(Rectangle(-75.0, -10.0 - 100.0, 250.0, 90.0))

                    var writer = Writer(drawer)
                    var widthT = writer.textWidth(countriesLong.get(selectionId))

                    drawer.image(flagview,
                            Rectangle(0.0, 0.0, flag.width*1.0, flag.height*1.0),
                            Rectangle(-10.0, -95.0, 15.0, 10.0))

                    drawer.fill = ColorRGBa.BLACK
                    drawer.fontMap = fontBold


                    writer.box = Rectangle(10.0, -85.0, 1020.0, 200.0)
                    writer.text(countriesLong.get(selectionId))

                    drawer.fill = ColorRGBa.BLACK
                    drawer.text(labels.get(selectedTheme), -65.0, -53.0)

                    val dataVal = OriginalCore.get(selectionId).get(selectedTheme)
                    val dataRank = DataCore.get(selectionId).get(selectedTheme)

                    val mapC = map(195.0, 0.0, 0.0, 1.0, dataRank)


                    var getColors = colorArray.get(selectedTheme)
                    val mixCOlor = mix(getColors.get(0), getColors.get(1), mapC)

                    drawer.fill = mixCOlor

                    if (dataVal.toDouble() != -999.0) {

                        //drawer.fontMap = font
                        drawer.text("#" + (dataRank.toInt() + 1), -65.0, -85.0)
                        //drawer.fontMap = fontBold
                        drawer.text(dataVal.toFloat().toString(), -65.0, -33.0)

                    } else {

                        //drawer.fontMap = font
                        drawer.text("#" + (dataRank.toInt() + 1), -65.0, -85.0)
                        //drawer.fontMap = fontBold
                        drawer.fill = ColorRGBa.RED
                        drawer.text("No data", -65.0, -33.0)

                    }

                    drawer.popTransforms()

                }
            }



        }

        drawer.isolatedWithTarget(rtBlops) {

            ortho(rtBlops)

            background(ColorRGBa.BLACK)

            drawer.pushStyle()
            drawer.drawStyle.blendMode = BlendMode.ADD

            drawer.shadeStyle = shadeStyle {

                fragmentTransform = """
                     x_fill.rgb = 0.025 * smoothstep(0.5, 0.0, vec3( length( va_texCoord0.xy - vec2(0.5, 0.5) )));
                     x_fill.a = 1.0;
                 """.trimIndent()
            }

            val circles = mutableListOf<Vector2>()

            movingPointsX.forEachIndexed { index, point ->
                val x = movingPointsX.get(index).value
                val y = movingPointsY.get(index).value
                circles.add(Vector2(x, y))
            }

            drawer.fill = ColorRGBa.WHITE.shade(1.0)
            drawer.stroke = null
            drawer.circles(circles, 128.0)
            drawer.popStyle()
        }

//        drawer.image(rtBlops.colorBuffer(0),
//                Rectangle(0.0, 0.0, 1000.0, 1000.0),
//                Rectangle(0.0, 0.0, 1000.0, 1000.0)
//        )

        contour.apply(rtBlops.colorBuffer(0), contourOut)


        drawer.isolatedWithTarget(renderImage) {

            drawer.background(ColorRGBa.RED)

            drawer.ortho(renderImage)

            image(contourOut,
                    Rectangle(0.0, 0.0, 1000.0, 1000.0),
                    Rectangle(0.0, 0.0, 1000.0, 1000.0)
            )

            drawer.pushStyle()
            drawer.drawStyle.blendMode = BlendMode.ADD

            drawer.image(rt1.colorBuffer(0),
                    Rectangle(0.0, 0.0, 1000.0, 1000.0),
                    Rectangle(0.0, 0.0, 1000.0, 1000.0)
            )

            drawer.popStyle()

            drawer.image(rt2.colorBuffer(0),
                    Rectangle(.0, .0, 1000.0, 1000.0),
                    Rectangle(0.0, 0.0, 1000.0, 1000.0)
            )

        }

        drawer.image(renderImage.colorBuffer(0),
                Rectangle(.0, .0, 1000.0, 1000.0),
                Rectangle(0.0, 0.0, 1000.0, 1000.0)
        )

        if(countTrue == 0) {
            drawer.stroke = null
            drawer.fontMap = fontBold
            drawer.fill = ColorRGBa.GRAY.opacify(0.9)
            drawer.rectangle(0.0, 0.0, 1000.0, 1000.0)
            drawer.fill = ColorRGBa.WHITE
            drawer.text("Make a selection to continue", 410.0, 500.0)

        }

    }
}