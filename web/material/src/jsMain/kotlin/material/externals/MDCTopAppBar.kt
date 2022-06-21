package material.externals

import org.w3c.dom.Element

@JsModule("@material/top-app-bar")
@JsNonModule
private external object MDCTopAppBarModule {
    object MDCTopAppBar {
        fun attachTo(root: Element?): material.externals.MDCTopAppBar
    }
}

external interface MDCTopAppBar

@Suppress("FunctionName")
fun MDCTopAppBar(root: Element?) = MDCTopAppBarModule.MDCTopAppBar.attachTo(root)
