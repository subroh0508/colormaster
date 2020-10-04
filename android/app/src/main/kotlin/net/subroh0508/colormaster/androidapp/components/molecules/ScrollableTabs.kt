package net.subroh0508.colormaster.androidapp.components.molecules

import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import net.subroh0508.colormaster.androidapp.components.atoms.Tab

@Composable
fun ScrollableTabs(
    titles: Array<String>,
    selectedIndex: Int = 0,
    onClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var tabSelected by remember { mutableStateOf(selectedIndex) }

    ScrollableTabRow(
        selectedTabIndex = tabSelected,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onSurface,
        indicator = {},
        divider = {},
        modifier = modifier
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                title,
                index == tabSelected,
                onClick = {
                    tabSelected = index
                    onClick(title)
                },
            )
        }
    }
}
