package utilities

import react.*

@Suppress("UNCHECKED_CAST")
fun <T, P: RConsumerProps<T>> RBuilder.children(props: P, t: T) {
    childList.add(props.children(t))
}

