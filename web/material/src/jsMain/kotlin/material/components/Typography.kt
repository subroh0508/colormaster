package material.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement
import material.utilities.TagElementBuilder

object TypographyVariant {
    const val Headline1 = "headline1"
    const val Headline2 = "headline2"
    const val Headline3 = "headline3"
    const val Headline4 = "headline4"
    const val Headline5 = "headline5"
    const val Headline6 = "headline6"
    const val Subtitle1 = "subtitle1"
    const val Subtitle2 = "subtitle2"
    const val Body1 = "body1"
    const val Body2 = "body2"
    const val Caption = "caption"
    const val Button = "button"
    const val Overline = "overline"
}

@Composable
fun TypographyHeadline1(
    tag: String = "h1",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Headline1, tag, applyAttrs, content)

@Composable
fun TypographyHeadline2(
    tag: String = "h2",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Headline2, tag, applyAttrs, content)

@Composable
fun TypographyHeadline3(
    tag: String = "h3",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Headline3, tag, applyAttrs, content)

@Composable
fun TypographyHeadline4(
    tag: String = "h4",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Headline4, tag, applyAttrs, content)

@Composable
fun TypographyHeadline5(
    tag: String = "h5",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Headline5, tag, applyAttrs, content)

@Composable
fun TypographyHeadline6(
    tag: String = "h1",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Headline6, tag, applyAttrs, content)

@Composable
fun TypographySubtitle1(
    tag: String = "div",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Subtitle1, tag, applyAttrs, content)

@Composable
fun TypographySubtitle2(
    tag: String = "div",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Subtitle2, tag, applyAttrs, content)

@Composable
fun TypographyBody1(
    tag: String = "p",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Body1, tag, applyAttrs, content)

@Composable
fun TypographyBody2(
    tag: String = "p",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Body2, tag, applyAttrs, content)

@Composable
fun TypographyButton(
    tag: String = "span",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Button, tag, applyAttrs, content)

@Composable
fun TypographyCaption(
    tag: String = "span",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Caption, tag, applyAttrs, content)

@Composable
fun TypographyOverline(
    tag: String = "span",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Typography(TypographyVariant.Overline, tag, applyAttrs, content)

@Composable
fun Typography(
    variant: String,
    tag: String = "div",
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    TagElement(
        TagElementBuilder(tag),
        {
            classes("mdc-typography--$variant")
            applyAttrs?.invoke(this)
        },
    ) { content() }
}
