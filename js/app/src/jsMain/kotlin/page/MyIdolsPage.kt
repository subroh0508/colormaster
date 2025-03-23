package page

import androidx.compose.runtime.Composable
import components.templates.StaticPageFrame
import components.templates.myidols.FavoriteIdolsCard
import components.templates.myidols.InChargeIdolsCard

@Composable
fun MyIdolsPage(
    topAppBarVariant: String,
    isSignedIn: Boolean,
) = StaticPageFrame(topAppBarVariant) {
    InChargeIdolsCard(isSignedIn)
    FavoriteIdolsCard(isSignedIn)
}
