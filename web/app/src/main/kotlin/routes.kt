import containers.IdolSearchContainer
import containers.PenlightContainer
import react.RBuilder
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch

fun RBuilder.routing() = browserRouter {
    switch {
        route("/penlight") { PenlightContainer() }
        route("/") { IdolSearchContainer() }
    }
}
