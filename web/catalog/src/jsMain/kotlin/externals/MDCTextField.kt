package externals

import org.w3c.dom.Element

@JsModule("@material/textfield")
@JsNonModule
private external object MDCTextFieldModule {
    class MDCTextField(root: Element?): externals.MDCTextField
}

external interface MDCTextField

@Suppress("FunctionName")
fun MDCTextField(root: Element?): MDCTextField = MDCTextFieldModule.MDCTextField(root)
