package material.externals

import org.w3c.dom.Element

@JsModule("@material/tooltip")
@JsNonModule
private external object MDCTooltipModule {
    object MDCTooltip {
        fun attachTo(root: Element?) : material.externals.MDCTooltip
    }
}

external interface MDCTooltip

@Suppress("FunctionName")
fun MDCTooltip(root: Element?) = MDCTooltipModule.MDCTooltip.attachTo(root)
