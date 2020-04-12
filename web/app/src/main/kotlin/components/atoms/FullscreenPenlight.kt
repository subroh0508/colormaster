package components.atoms

import kotlinext.js.Object
import materialui.components.dialog.dialog
import materialui.components.slide.enums.SlideDirection
import materialui.components.slide.slide
import react.*

fun RBuilder.fullscreenPenlight(handler: RHandler<FullscreenPenlightProps>) = child(FullscreenPenlightComponent, handler = handler)

private val FullscreenPenlightComponent = functionalComponent<FullscreenPenlightProps> { props ->
    dialog {
        attrs.fullScreen = true
        attrs.open = true
        setProp("TransitionComponent", Transition)
        attrs.paperProps {
            setProp("style", kotlinext.js.js {
                this["backgroundColor"] = props.colors.first()
            } as Any)
        }
    }
}

private val Transition = forwardRef<RProps> { props, ref ->
    slide {
        attrs {
            direction = SlideDirection.up
            this.ref = ref

            Object.assign(this, props)
        }
    }
}

external interface FullscreenPenlightProps : RProps {
    var colors: List<String>
}
