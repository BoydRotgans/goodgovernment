package functions

import Dialogs.movingPointsX
import Dialogs.movingPointsY
import Dialogs.recepi
import Dialogs.toggles
import org.openrndr.draw.Drawer
import org.openrndr.panel.elements.Toggle
import types.movingPoint

// ////////////
// CHECK STATUS
// ////////////

fun setToggleStatus(allFalse : Boolean) {


    if(allFalse) {

        toggles.forEach {
            it.value = true
        }

    } else {

        toggles.forEach {
            it.value = false
        }

    }

    checkToggleStatus(true, "sidebar")

}
