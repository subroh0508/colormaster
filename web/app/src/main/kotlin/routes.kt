import containers.AppFrameContainer
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
    AppFrameContainer {
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

fun isExpandAppBar(history: RouteResultHistory) = """(/[a-z]{2})?/(howtouse|development|terms)""".toRegex().matches(history.location.pathname)

fun language(history: RouteResultHistory) = Languages.valueOfCode(history.langCode()) ?: Languages.JAPANESE

fun RouteResultHistory.toRoot() = to("/")
fun RouteResultHistory.toHowToUse() = to("/howtouse")
fun RouteResultHistory.toDevelopment() = to("/development")
fun RouteResultHistory.toTerms() = to("/terms")
fun RouteResultHistory.toPreview(query: String) = to("/preview?$query")
fun RouteResultHistory.toPenlight(query: String) = push("/penlight?$query")

private fun RouteResultHistory.langCode() = location.pathname.split("/")[1]

private fun RouteResultHistory.to(path: String) = push("${langCode().takeIf(String::isNotBlank)?.let { "/$it" } ?: ""}$path")
