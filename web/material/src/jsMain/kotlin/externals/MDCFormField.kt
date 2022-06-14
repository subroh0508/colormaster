package externals

import org.w3c.dom.Element

@JsModule("@material/form-field")
@JsNonModule
private external object MDCFormFieldModule {
    class MDCFormField(root: Element?) : externals.MDCFormField {
        override var input: Any?
            get() = definedExternally
            set(value) = definedExternally
    }
}

external interface MDCFormField {
    var input: Any?
}

@Suppress("FunctionName")
fun MDCFormField(root: Element?): MDCFormField = MDCFormFieldModule.MDCFormField(root)
