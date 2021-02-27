package net.subroh0508.colormaster.androidapp.components.molecules

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.components.atoms.DrawerButton

interface MenuListLabel  {
    val resId: Int
}

@Composable
fun <T: MenuListLabel> DrawerMenuList(
    label: String? = null,
    items: Array<Pair<ImageVector, T>>,
    onClick: (T) -> Unit,
) {
    label?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .height(28.dp)
                .padding(start = 16.dp)
                .wrapContentHeight(Alignment.Bottom),
        )
    }

    items.forEach { (asset, item) ->
        Spacer(Modifier.height(8.dp))
        DrawerButton(
            asset = asset,
            label = stringResource(item.resId),
            onClick = { onClick(item) },
        )
    }

    Spacer(Modifier.height(8.dp))
    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .12F))
}
