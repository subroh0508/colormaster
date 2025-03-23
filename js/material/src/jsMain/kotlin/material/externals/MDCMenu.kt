package material.externals

import org.w3c.dom.Element

@JsModule("@material/menu")
@JsNonModule
private external object MDCMenuModule {
    object MDCMenu {
        fun attachTo(root: Element?) : material.externals.MDCMenu
    }
}

external interface MDCMenu {
    var open: Boolean
}

@Suppress("FunctionName")
fun MDCMenu(root: Element?) = MDCMenuModule.MDCMenu.attachTo(root)
