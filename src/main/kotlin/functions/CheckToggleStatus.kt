package functions

import Dialogs.*
import org.openrndr.draw.Drawer
import org.openrndr.panel.elements.Toggle
import types.movingPoint

// ////////////
// CHECK STATUS
// ////////////

fun checkToggleStatus(setData : Boolean, view : String) {

    countTrue = 0

    println("amount " + toggles.size)
    toggles.forEachIndexed { index, toggle ->

        var theRightToggle = toggles[returnIndexSlide3Layout(0)]
        if(view.equals("slide3")) {
            theRightToggle = toggles[returnIndexSlide3Layout(index)]
        } else if(view.equals("sidebar")) {
            theRightToggle = toggles[returnIndexSidebarLayout(index)]
        }

        if (theRightToggle.value == true) {
            recepi.set(index, true)
            countTrue++
        } else {
            recepi.set(index, false)
        }

    }

    if(setData) {
        loadSet(recepi)
    }
}
