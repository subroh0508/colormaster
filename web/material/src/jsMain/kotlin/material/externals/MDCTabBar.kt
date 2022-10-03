package material.externals

import org.w3c.dom.Element

@JsModule("@material/tab-bar")
@JsNonModule
private external object MDCTabBarModule {
    class MDCTabBar(root: Element?) : material.externals.MDCTabBar
}

external interface MDCTabBar

@Suppress("FunctionName")
fun MDCTabBar(root: Element?): MDCTabBar = MDCTabBarModule.MDCTabBar(root)
