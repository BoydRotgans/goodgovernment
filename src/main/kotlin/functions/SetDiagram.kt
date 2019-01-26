package functions

import Dialogs.movingPointsX
import Dialogs.movingPointsY
import org.openrndr.animatable.easing.Easing
import org.openrndr.math.map
import types.movingPoint

// ////////////
// SET DIAGRAM
// ////////////
fun setDiagram(points: MutableList<DoubleArray>, animate: Boolean) {

    var arrayX = DoubleArray(points.size)
    var arrayY = DoubleArray(points.size)

    points.forEachIndexed { index, doubles ->
        arrayX.set(index, doubles.get(0))
        arrayY.set(index, doubles.get(1))
    }

    val xMin = arrayX.min()
    val yMin = arrayY.min()
    val xMax = arrayX.max()
    val yMax = arrayY.max()

    points.forEachIndexed { index, doubles ->

        var xpos = map(xMin!!, xMax!!, 100.0, 900.0, arrayX.get(index))
        var ypos = map(yMin!!, yMax!!, 100.0, 900.0, arrayY.get(index))

        if(animate) {
            movingPointsX.get(index).delay(index * 2.toLong())
            movingPointsX.get(index).animate("value", xpos, 1000, Easing.CubicInOut)
            movingPointsY.get(index).delay(index * 2.toLong())
            movingPointsY.get(index).animate("value", ypos, 1000, Easing.CubicInOut)
        } else {
            movingPointsX.get(index).value = xpos
            movingPointsY.get(index).value = ypos
        }
    }

}