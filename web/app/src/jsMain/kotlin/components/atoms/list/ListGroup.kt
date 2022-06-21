package components.atoms.list

import androidx.compose.runtime.Composable
import material.components.TypographySubtitle1
import org.jetbrains.compose.web.dom.Text
import material.components.ListGroupSubHeader as MaterialListGroupSubHeader

@Composable
fun ListGroupSubHeader(
    text: String,
) = MaterialListGroupSubHeader(
    tag = "div",
) { TypographySubtitle1 { Text(text) } }

@Composable
fun ListGroupSubHeader(
    content: @Composable () -> Unit,
) = MaterialListGroupSubHeader(
    tag = "div",
) { TypographySubtitle1 { content() } }
