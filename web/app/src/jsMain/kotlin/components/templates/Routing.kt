package components.templates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import page.about.HowToUsePage
import page.SearchIdolPage
import page.about.DevelopmentPage
import page.about.TermsPage
import routes.*
import utilities.subscribeAsState

@Composable
fun Routing(
    topAppBarVariant: String,
    isSignedIn: Boolean,
) {
    val router = CurrentLocalRouter() ?: return
    val routerState by router.stack.subscribeAsState()

    when (routerState.active.instance) {
        is Search -> SearchIdolPage(topAppBarVariant, isSignedIn)
        is HowToUse -> HowToUsePage(topAppBarVariant)
        is Development -> DevelopmentPage(topAppBarVariant)
        is Terms -> TermsPage(topAppBarVariant)
        else -> Unit
    }
}
