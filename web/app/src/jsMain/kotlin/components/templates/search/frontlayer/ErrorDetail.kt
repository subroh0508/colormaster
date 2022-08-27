package components.templates.search.frontlayer

import MaterialTheme
import androidx.compose.runtime.Composable
import io.ktor.client.plugins.*
import material.components.*
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import utilities.I18nextText
import utilities.LocalI18n
import utilities.invoke

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
    val header = when (error) {
        is ResponseException -> "${error.response.status.value}: ${error.response.status.description}"
        else -> error::class.simpleName ?: "UnknownException"
    }

    CardHeader { TypographyHeadline6 { Text(header) } }
}

@Composable
private fun ErrorDetailContent(error: Throwable) {
    val t = LocalI18n() ?: return

    CardContent { Text(buildMessage(t, error)) }
}

private fun buildMessage(t: I18nextText, error: Throwable): String {
    if (error !is ResponseException) {
        return t("searchPanel.errors.unknown")
    }

    return when (error.response.status.value / 100) {
        4 -> t("searchPanel.errors.4xx")
        5 -> t("searchPanel.errors.5xx")
        else -> t("searchPanel.errors.unknown")
    }
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
