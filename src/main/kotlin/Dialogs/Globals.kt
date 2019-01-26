package Dialogs

import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.panel.elements.Toggle
import org.openrndr.resourceUrl
import types.movingPoint

var movingPointsX = mutableListOf<movingPoint>()
var movingPointsY = mutableListOf<movingPoint>()

var toggles = mutableListOf<Toggle>()
var recepi = BooleanArray(32)

var flagId = 125

class Contour : Filter(filterShaderFromUrl(resourceUrl("/shaders/outline.frag"))) {
    var resolution: Vector2 by parameters
    var time : Double by parameters
    var slider : Double by parameters
}

val contour = Contour()

var frame = 0.0

var renderImage = renderTarget(1000, 1000, 2.0) {
    colorBuffer() //type = ColorType.FLOAT16
}

var flag = loadImage("data/flags/netherlands.png")

var countTrue = 0