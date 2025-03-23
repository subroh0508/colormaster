package page.about

import androidx.compose.runtime.Composable
import components.atoms.card.OutlinedCard
import components.templates.StaticPageFrame
import net.subroh0508.colormaster.common.external.invoke
import net.subroh0508.colormaster.common.ui.LocalI18n

@Composable
fun DevelopmentPage(
    topAppBarVariant: String,
) = StaticPageFrame(topAppBarVariant) {
    val t = LocalI18n() ?: return@StaticPageFrame

    OutlinedCard(
        header = t("about.development.imasparql.title"),
        rawHtml = t("about.development.imasparql.description"),
    )

    OutlinedCard(
        header = t("about.development.frontend.title"),
        rawHtml = t("about.development.frontend.description"),
    )

    OutlinedCard(
        header = t("about.development.requests.title"),
        rawHtml = t("about.development.requests.description"),
    )
}
