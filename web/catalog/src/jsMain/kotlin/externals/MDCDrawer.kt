package externals

import org.w3c.dom.Element

@JsModule("@material/drawer")
@JsNonModule
private external object MDCDrawerModule {
    object MDCDrawer {
        fun attachTo(root: Element?): externals.MDCDrawer
    }
}

external interface MDCDrawer {
    var open: Boolean
}

@Suppress("FunctionName")
fun MDCDrawer(root: Element?) = MDCDrawerModule.MDCDrawer.attachTo(root)
