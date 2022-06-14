package externals

import org.w3c.dom.Element

@JsModule("@material/ripple")
@JsNonModule
private external object MDCRippleModule {
    object MDCRipple {
        fun attachTo(root: Element?, opts: RippleOption): externals.MDCRipple
    }
}

external interface MDCRipple {
    fun activate()
    fun deactivate()

    var unbounded: Boolean
}

external interface RippleOption {
    var isUnbounded: Boolean
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun attachRippleTo(
    root: Element?,
    opts: RippleOption.() -> Unit = {},
) = MDCRippleModule.MDCRipple.attachTo(root, (js("({})") as RippleOption).apply(opts))
