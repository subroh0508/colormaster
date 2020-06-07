import containers.AppFrameContainer
import containers.IdolSearchContainer
import containers.PenlightContainer
import containers.PreviewContainer
import org.w3c.dom.url.URLSearchParams
import pages.DevelopmentPage
import pages.HowToUsePage
import pages.TermsPage
import react.RBuilder
import react.RProps
import react.child
import react.functionalComponent
import react.router.dom.*

fun RBuilder.routing() = browserRouter { child(AppComponent) }

private val AppComponent = functionalComponent<RProps> {
    val history = useHistory()
    val lang = language(history)

    AppFrameContainer {
        attrs.lang = lang

        switch {
            Languages.values().forEach { (_, _, basename) ->
                route("$basename/preview", exact = true) { PreviewContainer() }
                route("$basename/penlight", exact = true) { PenlightContainer() }
                route("$basename/howtouse", exact = true) { HowToUsePage() }
                route("$basename/development", exact = true) { DevelopmentPage() }
                route("$basename/terms", exact = true) { TermsPage() }
                route("$basename/", exact = true) { IdolSearchContainer() }
            }
        }
    }
}

fun useQuery() = URLSearchParams(useLocation().search)

fun isExpandAppBar(history: RouteResultHistory) = listOf(
    "/howtouse",
    "/development",
    "/terms"
).contains(history.location.pathname)

fun language(history: RouteResultHistory): Languages {
    val code = history.location.pathname.split("/")[1]

    return Languages.valueOfCode(code) ?: Languages.JAPANESE
}
