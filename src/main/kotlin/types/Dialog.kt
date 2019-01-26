package types

import com.sun.javafx.css.Stylesheet

class Dialog {
    var text:String
        get() = TODO()
        set(value) = setText(value)

    var setText: (String) -> Unit = {}

    var dispose: ()->Unit = {}

}
