package material.externals

import org.w3c.dom.Element

@JsModule("@material/textfield/icon")
@JsNonModule
private external object MDCTextFieldIconModule {
    class MDCTextFieldIcon(root: Element?): material.externals.MDCTextFieldIcon
}

external interface MDCTextFieldIcon

@Suppress("FunctionName")
fun MDCTextFieldIcon(root: Element?): MDCTextFieldIcon = MDCTextFieldIconModule.MDCTextFieldIcon(root)
