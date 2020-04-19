import containers.IdolSearchContainer
import containers.PenlightContainer
import containers.PreviewContainer
import org.w3c.dom.url.URLSearchParams
import pages.search.IdolSearchPage
import react.RBuilder
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import react.router.dom.useLocation

fun RBuilder.routing() = browserRouter {
    switch {
        route("/preview") { PreviewContainer() }
        route("/penlight") { PenlightContainer() }
        route("/") { IdolSearchPage() }
    }
}

fun useQuery() = URLSearchParams(useLocation().search)
