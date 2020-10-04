package net.subroh0508.colormaster.androidapp.components.molecules

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.components.atoms.DrawerButton

@Composable
fun DrawerMenuList(
    label: String? = null,
    items: Array<Pair<VectorAsset, String>>,
    onClick: (String) -> Unit,
) {
    label?.let {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.preferredHeight(28.dp)
                .padding(start = 16.dp)
                .wrapContentHeight(Alignment.Bottom),
        )
    }

    items.forEach { (asset, item) ->
        Spacer(Modifier.preferredHeight(8.dp))
        DrawerButton(
            asset = asset,
            label = item,
            onClick = { onClick(item) },
        )
    }

    Spacer(Modifier.preferredHeight(8.dp))
    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .12F))
}
