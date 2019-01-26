package functions

import Dialogs.movingPointsX
import Dialogs.movingPointsY
import org.openrndr.draw.Drawer
import types.movingPoint
import kotlin.concurrent.thread

// ////////////
// LOAD SET
// ////////////

fun loadSet(recepiIn: BooleanArray) {

    var tsneBuild = TSNEBuild()

    thread {
        val tsnePoints = tsneBuild.consume(recepiIn)

        val points = mutableListOf<DoubleArray>()

        if (tsnePoints != null) {
            tsnePoints.forEachIndexed { index, doubles ->
                var da = DoubleArray(2)
                da.set(0, doubles.get(0))
                da.set(1, doubles.get(1))
                points.add(index, da)
            }
        }

        println(points.size)
        setDiagram(points, true)
    }
}