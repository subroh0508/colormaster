import components.templates.appFrame
import containers.IdolSearchContainer
import containers.PenlightContainer
import containers.PreviewContainer
import org.w3c.dom.url.URLSearchParams
import pages.DevelopmentPage
import pages.HowToUsePage
import pages.TermsPage
import react.RBuilder
import react.router.dom.*

fun RBuilder.routing() = browserRouter {
    appFrame {
        switch {
            route("/preview") { PreviewContainer() }
            route("/penlight") { PenlightContainer() }
            route("/howtouse") { HowToUsePage() }
            route("/development") { DevelopmentPage() }
            route("/terms") { TermsPage() }
            route("/") { IdolSearchContainer() }
        }
    }
}

fun useQuery() = URLSearchParams(useLocation().search)

fun isExpandAppBar(history: RouteResultHistory) = listOf(
    "/howtouse",
    "/development",
    "/terms"
).contains(history.location.pathname)
