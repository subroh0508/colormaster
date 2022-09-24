package components.templates

import MaterialTheme
import androidx.compose.runtime.Composable
import material.components.TopAppBarMainContent
import material.utilities.MEDIA_QUERY_LAPTOP
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.TagElementBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.TagElement

@Composable
fun StaticPageFrame(
    topAppBarVariant: String,
    content: @Composable () -> Unit,
) = TopAppBarMainContent(topAppBarVariant) {
    Style(StaticPageFrameStyle)

    Div({ classes(StaticPageFrameStyle.frame) }) { content() }
}

@Composable
fun Strong(
    content: @Composable () -> Unit,
) = TagElement(TagElementBuilder("strong"), null) { content() }

private object StaticPageFrameStyle : StyleSheet() {
    val frame by style {
        paddingTop(16.px)
        paddingBottom(32.px)
        paddingLeft(16.px)
        paddingRight(16.px)
        property("margin-left", "auto")
        property("margin-right", "auto")

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                paddingLeft(24.px)
                paddingRight(24.px)
            }
        }

        media(MEDIA_QUERY_LAPTOP) {
            self style {
                maxWidth(1240.px)
            }
        }

        child(self, className("mdc-card")) style  {
            marginBottom(32.px)
        }

        desc(self, type("a")) style {
            color(MaterialTheme.Var.textLink)
            textDecoration("none")

            hover(self) style  {
                textDecoration("underline")
            }
        }
    }
}
