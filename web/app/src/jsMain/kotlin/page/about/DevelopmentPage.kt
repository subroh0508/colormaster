package page.about

import androidx.compose.runtime.Composable
import components.atoms.card.OutlinedCard
import components.atoms.card.RawHtml
import components.templates.StaticPageFrame
import components.templates.Strong
import material.components.Icon
import material.components.TopAppBarMainContent
import material.components.TypographySubtitle1
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import utilities.invoke

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
