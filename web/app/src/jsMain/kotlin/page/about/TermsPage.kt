package page.about

import androidx.compose.runtime.Composable
import components.atoms.card.OutlinedCard
import components.templates.StaticPageFrame
import net.subroh0508.colormaster.components.core.external.invoke
import net.subroh0508.colormaster.components.core.ui.LocalI18n

@Composable
fun TermsPage(
    topAppBarVariant: String,
) = StaticPageFrame(topAppBarVariant) {
    val t = LocalI18n() ?: return@StaticPageFrame

    OutlinedCard(
        header = t("about.terms.top.title"),
        rawHtml = t("about.terms.top.description"),
    )

    OutlinedCard(
        header = t("about.terms.disclaimer.title"),
        rawHtml = t("about.terms.disclaimer.description"),
    )

    OutlinedCard(
        header = t("about.terms.cookie.title"),
        rawHtml = t("about.terms.cookie.description"),
    )
}
