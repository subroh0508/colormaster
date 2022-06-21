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
    onClickTranslate: (SyntheticMouseEvent) -> Unit,
    onClickBrightness: (SyntheticMouseEvent) -> Unit,
    content: @Composable () -> Unit,
) = MaterialTopAppBar(
    TopAppBarVariant.Fixed,
    navigationContent = {
        TopAppNavigationIcon("menu", onClick = onClickNavigation)
    },
    actionContent = {
        TopAppActionIcon("translate", onClick = onClickTranslate)
        TopAppActionIcon("brightness_4", onClick = onClickBrightness)
    },
    mainContent = content,
)
