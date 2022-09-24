package page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import material.components.*
import material.utilities.MEDIA_QUERY_LAPTOP
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import net.subroh0508.colormaster.model.IdolColor
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import usecase.EmptyIdsRequestException
import usecase.rememberFetchIdolsUseCase
import utilities.LocalI18n
import utilities.buildErrorHeader
import utilities.buildErrorMessage
import utilities.invoke

@Composable
fun PreviewPage(
    topAppBarVariant: String,
    ids: List<String>,
) = FullscreenPreviewPage(topAppBarVariant, ids, isIdolNameVisible = true)

@Composable
fun PenlightPage(
    topAppBarVariant: String,
    ids: List<String>,
) = FullscreenPreviewPage(topAppBarVariant, ids, isIdolNameVisible = false)

@Composable
private fun FullscreenPreviewPage(
    topAppBarVariant: String,
    ids: List<String>,
    isIdolNameVisible: Boolean,
) {
    val idolColorLoadState by rememberFetchIdolsUseCase(ids)

    val items: List<IdolColor> = idolColorLoadState.getValueOrNull() ?: listOf()
    val error = idolColorLoadState.getErrorOrNull()

    when {
        error != null -> ErrorMessage(topAppBarVariant, error)
        items.isNotEmpty() -> FullscreenPreviewFrame(items, isIdolNameVisible)
    }
}

@Composable
private fun FullscreenPreviewFrame(
    items: List<IdolColor>,
    isIdolNameVisible: Boolean,
) = Div({
    classes("mdc-typography--body2")
    style {
        height(100.percent)
        width(100.percent)
        position(Position.Fixed)
        property("z-index", 10)
    }
}) {
    val itemCount = items.size

    items.forEach { item ->
        ColorPreview(
            item,
            itemCount,
            isIdolNameVisible,
        )
    }
}

@Composable
private fun ColorPreview(
    item: IdolColor,
    itemCount: Int,
    isIdolNameVisible: Boolean
) {
    val style = remember(item, itemCount) { ColorPreviewStyle(item, itemCount) }

    Style(style)

    Div({ classes(style.content) }) {
        if (isIdolNameVisible) {
            Text(item.name)
            Br { }
            Text(item.color)
        }
    }
}

@Composable
private fun ErrorMessage(
    topAppBarVariant: String,
    error: Throwable,
) = TopAppBarMainContent(
    topAppBarVariant,
    {
        style {
            height(40.percent)
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
        }
    },
) {
    val t = LocalI18n() ?: return@TopAppBarMainContent

    Style(ErrorMessageStyle)

    OutlinedCard({ classes(ErrorMessageStyle.content) }) {
        CardHeader { TypographyHeadline6 { Text(buildErrorHeader(error)) } }
        CardContent {
            if (error is EmptyIdsRequestException)
                Text(t("errors.4xx"))
            else
                Text(buildErrorMessage(t, error))
        }
    }
}

private class ColorPreviewStyle(
    item: IdolColor,
    itemCount: Int,
) : StyleSheet() {
    val content = "content-${item.id}"

    private val color = if (item.isBrighter) Color.black else Color.white
    private val backgroundColor = Color(item.color)

    init {
        className(content) style {
            height((100.0 / itemCount.toDouble()).percent)
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
            textAlign("center")
            fontWeight("bold")
            color(color)
            backgroundColor(backgroundColor)
        }
    }
}

private object ErrorMessageStyle : StyleSheet() {
    val content by style {
        paddingTop(16.px)
        paddingBottom(16.px)
        paddingLeft(16.px)
        paddingRight(16.px)
        property("border-color", MaterialTheme.Var.divider)
        borderRadius(16.px)
        property("word-wrap", "break-word")
        color(MaterialTheme.Var.onSurface)

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
    }
}
