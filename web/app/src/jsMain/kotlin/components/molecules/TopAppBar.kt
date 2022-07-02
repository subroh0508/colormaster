package components.molecules

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import material.components.Icon
import material.components.TopAppActionIcon
import material.components.TopAppBarVariant
import material.components.TopAppNavigationIcon
import material.components.TopAppBar as MaterialTopAppBar

@Composable
fun TopAppBar(
    onClickNavigation: (SyntheticMouseEvent) -> Unit,
    actionContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) = MaterialTopAppBar(
    TopAppBarVariant.Fixed,
    navigationContent = {
        TopAppNavigationIcon("menu", onClick = onClickNavigation)
    },
    actionContent = actionContent,
    mainContent = content,
)
