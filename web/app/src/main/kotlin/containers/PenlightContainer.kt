package containers

import react.*
import react.dom.div
import react.dom.h3
import useQuery

@Suppress("FunctionName")
fun RBuilder.PenlightContainer() = child(PenlightContainerComponent)

private val PenlightContainerComponent = functionalComponent<RProps> {
    val ids = useQuery().getAll("id")

    ids.forEach {
        div { h3 { +it } }
    }
}
