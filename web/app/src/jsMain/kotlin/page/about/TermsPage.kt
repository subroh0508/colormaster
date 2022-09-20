package page.about

import androidx.compose.runtime.Composable
import material.components.TopAppBarMainContent
import org.jetbrains.compose.web.dom.Text

@Composable
fun TermsPage(
    topAppBarVariant: String,
) = TopAppBarMainContent(topAppBarVariant) {
    Text("Hello, World!")
}
