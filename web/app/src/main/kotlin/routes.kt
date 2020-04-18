import containers.IdolSearchContainer
import containers.PenlightContainer
import containers.PreviewContainer
import org.w3c.dom.url.URLSearchParams
import react.RBuilder
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import utilities.useLocation

fun RBuilder.routing() = browserRouter {
    switch {
        route("/preview") { PreviewContainer() }
        route("/penlight") { PenlightContainer() }
        route("/") { IdolSearchContainer() }
    }
}

fun useQuery() = URLSearchParams(useLocation().search)
