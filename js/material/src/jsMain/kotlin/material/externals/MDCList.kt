package material.externals

import org.w3c.dom.Element

@JsModule("@material/list")
@JsNonModule
private external object MDCListModule {
    class MDCList(root: Element?) : material.externals.MDCList
}

external interface MDCList

@Suppress("FunctionName")
fun MDCList(root: Element?): MDCList = MDCListModule.MDCList(root)
