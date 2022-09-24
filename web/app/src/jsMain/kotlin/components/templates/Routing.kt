package components.templates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import page.PenlightPage
import page.PreviewPage
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

    routerState.active.instance.let {
        when (it) {
            is Search -> SearchIdolPage(topAppBarVariant, isSignedIn, it.initParams)
            is Preview -> PreviewPage(topAppBarVariant, it.ids)
            is Penlight -> PenlightPage(topAppBarVariant, it.ids)
            is HowToUse -> HowToUsePage(topAppBarVariant)
            is Development -> DevelopmentPage(topAppBarVariant)
            is Terms -> TermsPage(topAppBarVariant)
            else -> Unit
        }
    }
}
