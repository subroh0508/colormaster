package utilities

import kotlinext.js.jsObject
import react.*

typealias RConsumerHandler<T> = RBuilder.(T) -> Unit

fun <T, P: RConsumerProps<T>> RBuilder.child(
    functionalComponent: FunctionalComponent<P>,
    props: P = jsObject {},
    handler: RConsumerHandler<T>
): ReactElement {
    return child(functionalComponent, props.apply {
        children = { value: T -> buildElement { handler(value) } as Any }
    })
}

fun <T, P: RConsumerProps<T>> RBuilder.children(props: P, t: T) {
    childList.add(props.children(t))
}

