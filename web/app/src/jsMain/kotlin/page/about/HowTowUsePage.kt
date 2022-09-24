package page.about

import androidx.compose.runtime.Composable
import components.atoms.card.OutlinedCard
import components.atoms.card.RawHtml
import components.templates.StaticPageFrame
import components.templates.Strong
import material.components.Icon
import material.components.TypographySubtitle1
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import utilities.invoke

@Composable
fun HowToUsePage(
    topAppBarVariant: String,
) = StaticPageFrame(topAppBarVariant) {
    val t = LocalI18n() ?: return@StaticPageFrame

    OutlinedCard(
        header = t("about.howToUse.introduction.title"),
        rawHtml = t("about.howToUse.introduction.description"),
    )

    OutlinedCard(
        header = { Text(t("about.howToUse.features.title")) },
        content = {
            TypographySubtitle1(applyAttrs = {
                style {
                    display(DisplayStyle.Flex)
                    property("vertical-align", "middle")
                }
            }) {
                Icon("palette")
                Strong { Text(t("about.howToUse.features.preview.title")) }
            }

            RawHtml(t("about.howToUse.features.preview.description"))

            TypographySubtitle1(applyAttrs = {
                style {
                    display(DisplayStyle.Flex)
                    property("vertical-align", "middle")
                }
            }) {
                Icon("highlight")
                Strong { Text(t("about.howToUse.features.penlight.title")) }
            }

            RawHtml(t("about.howToUse.features.penlight.description"))
        },
    )

    OutlinedCard(
        header = t("about.howToUse.howToUse.title"),
        rawHtml = t("about.howToUse.howToUse.description"),
    )
}
