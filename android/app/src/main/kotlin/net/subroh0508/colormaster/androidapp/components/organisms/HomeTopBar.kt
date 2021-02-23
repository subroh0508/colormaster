package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.molecules.ScrollableTabs

@Composable
fun HomeTopBar(drawerState: DrawerState, title: String) = HomeTopBar(drawerState, arrayOf(title))

@Composable
fun HomeTopBar(drawerState: DrawerState, titles: Array<String> = arrayOf()) {
    Row(
        Modifier
            .preferredHeight(56.dp)
            .background(MaterialTheme.colors.background),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_menu),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable(onClick = drawerState::open)
                .padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
        )

        Title(titles)
    }
}

@Composable
private fun RowScope.Title(titles: Array<String>) = when (titles.size) {
    1 -> Text(
        titles.first(),
        style = MaterialTheme.typography.h6,
        modifier = Modifier.align(Alignment.CenterVertically)
            .padding(start = 16.dp),
    )
    else -> ScrollableTabs(
        titles,
        modifier = Modifier
            .weight(1F)
            .align(Alignment.CenterVertically)
            .padding(start = 16.dp),
    )
}
