package components.molecules

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import material.components.TopAppNavigationIcon
import material.components.TopAppBar as MaterialTopAppBar

@Composable
fun TopAppBar(
    variant: String,
    onClickNavigation: (SyntheticMouseEvent) -> Unit,
    actionContent: @Composable () -> Unit,
    content: (@Composable () -> Unit)? = null,
) = MaterialTopAppBar(
    variant,
    navigationContent = {
        TopAppNavigationIcon("menu", onClick = onClickNavigation)
    },
    actionContent = actionContent,
    mainContent = content,
)
