import containers.*
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import org.w3c.dom.url.URLSearchParams
import pages.DevelopmentPage
import pages.HowToUsePage
import pages.TermsPage
import react.*
import react.router.dom.*

fun RBuilder.routing() = browserRouter {
    AppFrameContainer { child(switchComponent) }
}

private val switchComponent = functionalComponent<RProps> {
    val currentUser = useContext(AuthenticationProviderContext)

    switch {
        Languages.values().forEach { lang ->
            route("${lang.basename}/", exact = true) { IdolSearchContainer() }
            route("${lang.basename}/preview", exact = true) { PreviewContainer() }
            route("${lang.basename}/penlight", exact = true) { PenlightContainer() }
            route("${lang.basename}/howtouse", exact = true) { HowToUsePage() }
            route("${lang.basename}/development", exact = true) { DevelopmentPage() }
            route("${lang.basename}/terms", exact = true) { TermsPage() }

            if (currentUser != null) {
                route("${lang.basename}/myidols", exact = true) { MyIdolsContainer() }
            }
        }
    }
}

fun useQuery() = URLSearchParams(useLocation().search)

fun isRoot(history: RouteResultHistory) = """(/[a-z]{2})?/?""".toRegex().matches(history.location.pathname)
fun isExpandAppBar(history: RouteResultHistory) = """(/[a-z]{2})?/(myidols|howtouse|development|terms)""".toRegex().matches(history.location.pathname)

fun language(history: RouteResultHistory) = Languages.valueOfCode(history.langCode()) ?: Languages.JAPANESE

fun RouteResultHistory.toRoot() = to("/")
fun RouteResultHistory.toSearchBy(tab: SearchByTab) = to(if (tab == SearchByTab.BY_NAME) "/" else "?by=${tab.query}")
fun RouteResultHistory.toMyIdols() = to("/myidols")
fun RouteResultHistory.toHowToUse() = to("/howtouse")
fun RouteResultHistory.toDevelopment() = to("/development")
fun RouteResultHistory.toTerms() = to("/terms")
fun RouteResultHistory.toPreview(query: String) = to("/preview?$query")
fun RouteResultHistory.toPenlight(query: String) = push("/penlight?$query")

val Languages.basename: String get() = when (this) {
    Languages.JAPANESE -> ""
    Languages.ENGLISH -> "/en"
}

private fun RouteResultHistory.langCode() = Languages.values().let { languages ->
    val c = location.pathname.split("/")[1]

    languages.find { it.code == c }?.code ?: ""
}

private fun RouteResultHistory.to(path: String) = push("${langCode().takeIf(String::isNotBlank)?.let { "/$it" } ?: ""}$path")
