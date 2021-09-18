import containers.*
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import org.w3c.dom.url.URLSearchParams
import pages.DevelopmentPage
import pages.HowToUsePage
import pages.TermsPage
import react.*
import react.router.dom.*

fun RBuilder.routing() = BrowserRouter {
    AppFrameContainer { child(switchComponent) }
}

private val switchComponent = functionComponent<PropsWithChildren> {
    val currentUser = useContext(AuthenticationProviderContext)

    Switch {
        Languages.values().forEach { lang ->
            route(lang) { IdolSearchContainer() }
            route(lang, "preview") { PreviewContainer() }
            route(lang, "penlight") { PenlightContainer() }
            route(lang, "howtouse") { HowToUsePage() }
            route(lang, "development") { DevelopmentPage() }
            route(lang, "terms") { TermsPage() }

            if (currentUser != null) {
                route(lang, "myidols") { MyIdolsContainer() }
            }
        }
    }
}

private fun RBuilder.route(lang: Languages, path: String = "", render: Render) = Route {
    attrs { this.path = arrayOf("${lang.basename}/$path"); exact = true }

    render()
}

fun useQuery() = URLSearchParams(useLocation().search)

fun isRoot(history: History) = """(/[a-z]{2})?/?""".toRegex().matches(history.location.pathname)
fun isExpandAppBar(history: History) = """(/[a-z]{2})?/(myidols|howtouse|development|terms)""".toRegex().matches(history.location.pathname)

fun language(history: History) = Languages.valueOfCode(history.langCode()) ?: Languages.JAPANESE

fun History.toRoot() = to("/")
fun History.toSearchBy(tab: SearchByTab) = to(if (tab == SearchByTab.BY_NAME) "/" else "?by=${tab.query}")
fun History.toMyIdols() = to("/myidols")
fun History.toHowToUse() = to("/howtouse")
fun History.toDevelopment() = to("/development")
fun History.toTerms() = to("/terms")
fun History.toPreview(query: String) = to("/preview?$query")
fun History.toPenlight(query: String) = push("/penlight?$query")

val Languages.basename: String get() = when (this) {
    Languages.JAPANESE -> ""
    Languages.ENGLISH -> "/en"
}

private fun History.langCode() = Languages.values().let { languages ->
    val c = location.pathname.split("/")[1]

    languages.find { it.code == c }?.code ?: ""
}

private fun History.to(path: String) = push("${langCode().takeIf(String::isNotBlank)?.let { "/$it" } ?: ""}$path")
