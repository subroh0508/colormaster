package utilities

import kotlinext.js.jsObject
import react.*

fun <T, P: RProps> RBuilder.child(
    functionalComponent: FunctionalComponent<P>,
    props: P = jsObject {},
    handler: RBuilder.(T) -> Unit
): ReactElement {
    return child(functionalComponent, props, listOf { value: T -> buildElement { handler(value) } })
}

fun <T, P: RProps> RBuilder.children(props: P, t: T) {
    childList.add((props.children as (T) -> Any).invoke(t))
}
