package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.molecules.ScrollableTabs

@Composable
fun HomeTopBar(titles: Array<String>, drawerState: DrawerState) {
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
                .clickable(onClick = { drawerState.open() }),
        )
        Spacer(Modifier.preferredWidth(Dp(16F)))

        ScrollableTabs(
            titles,
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically),
        )
    }
}
