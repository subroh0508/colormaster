package externals

import org.w3c.dom.Element

@JsModule("@material/tab")
@JsNonModule
private external object MDCTabModule {
    class MDCTab(root: Element?) : externals.MDCTab
}

external interface MDCTab

@Suppress("FunctionName")
fun MDCTab(root: Element?): MDCTab = MDCTabModule.MDCTab(root)
