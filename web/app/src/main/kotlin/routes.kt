import components.templates.appFrame
import containers.IdolSearchContainer
import containers.PenlightContainer
import containers.PreviewContainer
import org.w3c.dom.url.URLSearchParams
import pages.HowToUsePage
import react.RBuilder
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import react.router.dom.useLocation

fun RBuilder.routing() = browserRouter {
    appFrame {
        switch {
            route("/preview") { PreviewContainer() }
            route("/penlight") { PenlightContainer() }
            route("/howtouse") { HowToUsePage() }
            route("/") { IdolSearchContainer() }
        }
    }
}

fun useQuery() = URLSearchParams(useLocation().search)
