package components.templates.search.frontlayer

import MaterialTheme
import androidx.compose.runtime.Composable
import material.components.*
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import net.subroh0508.colormaster.components.core.ui.LocalI18n
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import utilities.*

@Composable
fun ErrorDetail(error: Throwable) {
    Style(ErrorDetailStyle)

    OutlinedCard({ classes(ErrorDetailStyle.content) }) {
        ErrorDetailHeader(error)
        ErrorDetailContent(error)
    }
}

@Composable
private fun ErrorDetailHeader(error: Throwable) {
    val header = buildErrorHeader(error)

    CardHeader { TypographyHeadline6 { Text(header) } }
}

@Composable
private fun ErrorDetailContent(error: Throwable) {
    val t = LocalI18n() ?: return

    CardContent { Text(buildErrorMessage(t, error)) }
}

private object ErrorDetailStyle : StyleSheet() {
    val content by style {
        margin(8.px, 8.px, 56.px)
        property("border-color", MaterialTheme.Var.divider)
        borderRadius(16.px)
        property("word-wrap", "break-word")
        color(MaterialTheme.Var.onSurface)

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                margin(8.px, 0.px, 56.px, 8.px)
                borderRadius(
                    topLeft = 16.px,
                    topRight = 0.px,
                    bottomLeft = 16.px,
                    bottomRight = 0.px,
                )
                property("border-right", "none")
            }
        }
    }
}
