package routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.decompose.router.stack.webhistory.WebHistoryController
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.w3c.dom.Window

val LocalRouter = compositionLocalOf<Router?> { null }

@Composable
fun CurrentLocalRouter() = LocalRouter.current

@OptIn(ExperimentalDecomposeApi::class)
class Router(
    context: ComponentContext,
    private val pathname: String? = null,
    private val search: String? = null,
    historyController: WebHistoryController = DefaultWebHistoryController(),
) : ComponentContext by context {
    constructor(
        context: ComponentContext,
        window: Window,
    ) : this(
        context,
        window.location.pathname.takeIf(String::isNotBlank),
        window.location.search.takeIf(String::isNotBlank),
    )

    private val navigation = StackNavigation<Page>()

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

    fun toSearch() = navigation.bringToFront(Search(SearchParams.ByName.EMPTY))
    fun toHowToUse() = navigation.bringToFront(HowToUse)
    fun toDevelopment() = navigation.bringToFront(Development)
    fun toTerms() = navigation.bringToFront(Terms)

    private fun getInitialStack() = listOf(
        pathname?.let {
            when {
                it.endsWith(PATH_PREVIEW) -> Preview(listOf())
                it.endsWith(PATH_PENLIGHT) -> Penlight(listOf())
                it.endsWith(PATH_MY_IDOLS) -> MyIdols
                it.endsWith(PATH_HOW_TO_USE) -> HowToUse
                it.endsWith(PATH_DEVELOPMENT) -> Development
                it.endsWith(PATH_TERMS) -> Terms
                else -> null
            }
        } ?: Search(SearchParams.ByName.EMPTY),
    )

    private fun getPath(page: Page) = when (page) {
        is Search -> "/"
        is Preview -> "/$PATH_PREVIEW"
        is Penlight -> "/$PATH_PENLIGHT"
        is MyIdols -> "/$PATH_MY_IDOLS"
        is HowToUse -> "/$PATH_HOW_TO_USE"
        is Development -> "/$PATH_DEVELOPMENT"
        is Terms -> "/$PATH_TERMS"
    }

    private fun getConfiguration(path: String) = when (path.removePrefix("/")) {
        PATH_PREVIEW -> Preview(listOf())
        PATH_PENLIGHT -> Penlight(listOf())
        PATH_MY_IDOLS -> MyIdols
        PATH_HOW_TO_USE -> HowToUse
        PATH_DEVELOPMENT -> Development
        PATH_TERMS -> Terms
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