@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package utilities

import react.RBuilder
import react.RClass
import react.RHandler
import react.RProps

@JsModule("react-i18next")
external object ReactI18next {
    val initReactI18next: dynamic
    val I18nextProvider: RClass<ReactI18nextProps>
    val useTranslation: () -> Array<dynamic>
}

external interface ReactI18nextProps : RProps {
    var i18n: I18next
}

@Suppress("FunctionName")
fun RBuilder.I18nextProvider(handler: RHandler<ReactI18nextProps>) = ReactI18next.I18nextProvider(handler)

fun useTranslation(): Pair<T, I18next>  {
    val jsTuple = ReactI18next.useTranslation()

    return jsTuple[0] as T to jsTuple[1] as I18next
}
