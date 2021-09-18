@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package utilities

import react.RBuilder
import react.ComponentClass
import react.RHandler
import react.PropsWithChildren

@JsModule("react-i18next")
external object ReactI18next {
    val initReactI18next: dynamic
    val I18nextProvider: ComponentClass<ReactI18nextProps>
    val useTranslation: () -> Array<dynamic>
    val Trans: ComponentClass<TransProps>
}

external interface ReactI18nextProps : PropsWithChildren {
    var i18n: I18next
}

external interface TransProps : PropsWithChildren {
    var i18nKey: String
}

@Suppress("FunctionName")
fun RBuilder.I18nextProvider(handler: RHandler<ReactI18nextProps>) = ReactI18next.I18nextProvider(handler)

@Suppress("FunctionName")
fun RBuilder.Trans(handler: RHandler<TransProps>) = ReactI18next.Trans(handler)

fun useTranslation(): Pair<I18nextText, I18next>  {
    val jsTuple = ReactI18next.useTranslation()

    return jsTuple[0] as I18nextText to jsTuple[1] as I18next
}
