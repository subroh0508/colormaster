package components.organisms

import components.molecules.ColorPreviewProps
import components.molecules.colorPreview
import kotlinext.js.Object
import materialui.components.dialog.dialog
import materialui.components.slide.enums.SlideDirection
import materialui.components.slide.slide
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.setProp

val FullscreenPenlightComponent = functionComponent<FullscreenModalProps> { props ->
    child(FullscreenPreviewDialogComponent) {
        attrs.items = props.items
        attrs.isColorOnly = true
    }
}

val FullscreenPreviewComponent = functionComponent<FullscreenModalProps> { props ->
    child(FullscreenPreviewDialogComponent) {
        attrs.items = props.items
        attrs.isColorOnly = false
    }
}

external interface FullscreenModalProps : RProps {
    var items: List<IdolColor>
}

private val FullscreenPreviewDialogComponent = functionComponent<ColorPreviewProps> { props ->
    dialog {
        attrs.fullScreen = true
        attrs.open = true
        setProp("TransitionComponent", Transition)

        colorPreview(props)
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
