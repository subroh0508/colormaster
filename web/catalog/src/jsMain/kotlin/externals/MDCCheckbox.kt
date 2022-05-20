package externals

import org.w3c.dom.Element

@JsModule("@material/checkbox")
@JsNonModule
private external object MDCCheckboxModule {
    class MDCCheckbox(root: Element?) : externals.MDCCheckbox {
        override var checked: Boolean
            get() = definedExternally
            set(value) = definedExternally

        override var disabled: Boolean
            get() = definedExternally
            set(value) = definedExternally
    }
}

external interface MDCCheckbox {
    var checked: Boolean
    var disabled: Boolean
}

@Suppress("FunctionName")
fun MDCCheckbox(root: Element?): MDCCheckbox = MDCCheckboxModule.MDCCheckbox(root)
