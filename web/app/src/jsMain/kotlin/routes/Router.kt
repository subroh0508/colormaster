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
    private val pathname: String? = null,
    private val search: String? = null,
    private val i18next: I18next,
    historyController: WebHistoryController = DefaultWebHistoryController(),
) : ComponentContext by context {
    constructor(
        context: ComponentContext,
        window: Window,
        i18next: I18next,
    ) : this(
        context,
        window.location.pathname.takeIf(String::isNotBlank),
        window.location.search.takeIf(String::isNotBlank),
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

    fun toSearch() = navigation.bringToFront(Search(SearchParams.ByName.EMPTY))
    fun toHowToUse() = navigation.bringToFront(HowToUse)
    fun toDevelopment() = navigation.bringToFront(Development)
    fun toTerms() = navigation.bringToFront(Terms)

    private fun getInitialStack() = listOf(
        pathname?.let(::getConfiguration) ?: Search(SearchParams.ByName.EMPTY),
    )

    private fun getPath(page: Page) = when (page) {
        is Search -> "$basename/"
        is Preview -> "$basename/$PATH_PREVIEW"
        is Penlight -> "$basename/$PATH_PENLIGHT"
        is MyIdols -> "$basename/$PATH_MY_IDOLS"
        is HowToUse -> "$basename/$PATH_HOW_TO_USE"
        is Development -> "$basename/$PATH_DEVELOPMENT"
        is Terms -> "$basename/$PATH_TERMS"
    }

    private fun getConfiguration(path: String) = when {
        path.endsWith(PATH_PREVIEW) -> Preview(listOf())
        path.endsWith(PATH_PENLIGHT) -> Penlight(listOf())
        path.endsWith(PATH_MY_IDOLS) -> MyIdols
        path.endsWith(PATH_HOW_TO_USE) -> HowToUse
        path.endsWith(PATH_DEVELOPMENT) -> Development
        path.endsWith(PATH_TERMS) -> Terms
        else -> Search(SearchParams.ByName.EMPTY)
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