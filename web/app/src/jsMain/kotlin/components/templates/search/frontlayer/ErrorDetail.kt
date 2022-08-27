package components.templates.search.frontlayer

import androidx.compose.runtime.Composable
import material.components.*
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import utilities.invoke

@Composable
fun ErrorDetail(error: Throwable) {
    val t = LocalI18n() ?: return

    Style(ErrorDetailStyle)

    OutlinedCard({ classes(ErrorDetailStyle.content) }) {
        CardHeader {
            TypographyHeadline6 { Text(error::class.simpleName ?: "UnknownException") }
        }
        CardContent {
            Text(error.message ?: t("searchPanel.errors.unknown"))
        }
    }
}

private object ErrorDetailStyle : StyleSheet() {
    val content by style {
        margin(8.px, 8.px, 56.px)
        borderRadius(16.px)
        property("word-wrap", "break-word")

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                margin(8.px, 0.px, 56.px, 8.px)
                borderRadius(
                    topLeft = 16.px,
                    topRight = 0.px,
                    bottomLeft = 16.px,
                    bottomRight = 0.px,
                )
            }
        }
    }
}
