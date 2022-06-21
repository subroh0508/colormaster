package material.externals

import org.w3c.dom.Element

@JsModule("@material/drawer")
@JsNonModule
private external object MDCDrawerModule {
    object MDCDrawer {
        fun attachTo(root: Element?): material.externals.MDCDrawer
    }
}

external interface MDCDrawer {
    var open: Boolean
}

fun MDCDrawer.open() { open = true }
fun MDCDrawer.close() { open = false }

@Suppress("FunctionName")
fun MDCDrawer(root: Element?) = MDCDrawerModule.MDCDrawer.attachTo(root)
