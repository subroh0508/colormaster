package routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.decompose.router.stack.webhistory.WebHistoryController
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.w3c.dom.Window
import utilities.I18next
import utilities.basename

val LocalRouter = compositionLocalOf<Router?> { null }

@Composable
fun CurrentLocalRouter() = LocalRouter.current

@OptIn(ExperimentalDecomposeApi::class)
class Router(
    context: ComponentContext,
    private val path: String? = null,
    private val i18next: I18next,
    historyController: WebHistoryController = DefaultWebHistoryController(),
) : ComponentContext by context {
    constructor(
        context: ComponentContext,
        window: Window,
        i18next: I18next,
    ) : this(
        context,
        (window.location.pathname + window.location.search).takeIf(String::isNotBlank),
        i18next,
    )

    private val navigation = StackNavigation<Page>()
    private val basename get() = Languages.valueOfCode(i18next.language)?.basename ?: ""

    val stack = childStack(
        source = navigation,
        initialStack = ::getInitialStack,
        childFactory = { page, _ -> page }
    )

    init {
        historyController.attach(
            navigator = navigation,
            stack = stack,
            getPath = ::getPath,
            getConfiguration = ::getConfiguration,
        )
    }
    fun changeLanguage(lang: Languages) { i18next.changeLanguage(lang.code) }

    fun toSearch(initParams: SearchParams = SearchParams.ByName.EMPTY) = navigation.bringToFront(Search(initParams))
    fun toMyIdols() = navigation.bringToFront(MyIdols)
    fun toPreview(ids: List<String>) = navigation.bringToFront(Preview(ids))
    fun toPenlight(ids: List<String>) = navigation.bringToFront(Penlight(ids))
    fun toPenlight(id: String) = toPenlight(listOf(id))
    fun toHowToUse() = navigation.bringToFront(HowToUse)
    fun toDevelopment() = navigation.bringToFront(Development)
    fun toTerms() = navigation.bringToFront(Terms)

    private fun getInitialStack() = listOf(
        path?.let(::getConfiguration) ?: Search(SearchParams.ByName.EMPTY),
    )

    private fun getPath(page: Page) = when (page) {
        is Search -> "$basename/${page.query}"
        is Preview -> "$basename/$PATH_PREVIEW${page.query}"
        is Penlight -> "$basename/$PATH_PENLIGHT${page.query}"
        is MyIdols -> "$basename/$PATH_MY_IDOLS"
        is HowToUse -> "$basename/$PATH_HOW_TO_USE"
        is Development -> "$basename/$PATH_DEVELOPMENT"
        is Terms -> "$basename/$PATH_TERMS"
    }

    private fun getConfiguration(path: String): Page {
        val pathname = path.split("?").firstOrNull() ?: ""
        val query = path.split("?").getOrNull(1) ?: ""

        return when {
            pathname.endsWith(PATH_PREVIEW) -> Preview(query)
            pathname.endsWith(PATH_PENLIGHT) -> Penlight(query)
            pathname.endsWith(PATH_MY_IDOLS) -> MyIdols
            pathname.endsWith(PATH_HOW_TO_USE) -> HowToUse
            pathname.endsWith(PATH_DEVELOPMENT) -> Development
            pathname.endsWith(PATH_TERMS) -> Terms
            else -> Search(query)
        }
    }

    companion object {
        private const val PATH_PREVIEW = "preview"
        private const val PATH_PENLIGHT = "penlight"
        private const val PATH_MY_IDOLS = "myidols"
        private const val PATH_HOW_TO_USE = "howtouse"
        private const val PATH_DEVELOPMENT = "development"
        private const val PATH_TERMS = "terms"
    }
}