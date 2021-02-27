package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.androidapp.themes.darkBackground
import kotlin.math.max

@Composable
fun FlexRow(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, outerConstrains ->
        val rows = mutableListOf<List<Placeable>>()
        val rowHeight = mutableListOf<Int>()

        val currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0
        var currentRowHeight = 0

        fun canAddToCurrentRow(
            placeable: Placeable,
        ) = currentRow.isEmpty() || currentRowWidth + placeable.width + spacing.roundToPx() <= outerConstrains.maxWidth

        fun startNewRow() {
            rows += currentRow.toList()
            rowHeight.add(currentRowHeight)

            currentRow.clear()
            currentRowWidth = 0
            currentRowHeight = 0
        }

        for (measurable in measurables) {
            val placeable = measurable.measure(outerConstrains)

            if (!canAddToCurrentRow(placeable)) {
                startNewRow()
            }

            currentRow.add(placeable)

            currentRowWidth += placeable.width + spacing.roundToPx()
            currentRowHeight = max(currentRowHeight, placeable.height)
        }

        if (currentRow.isNotEmpty()) {
            startNewRow()
        }

        val height = rowHeight.sum() + (rowHeight.size - 1) * spacing.roundToPx()

        layout(outerConstrains.maxWidth, height) {
            var y = 0
            rows.fastForEachIndexed { i, placeables ->
                y += if (i == 0) 0 else rowHeight[i - 1] + spacing.roundToPx()

                var offsetX = 0
                placeables.fastForEachIndexed { j, placeable ->
                    val x = if (j == 0) 0 else offsetX
                    offsetX += placeable.width + spacing.roundToPx()

                    placeable.placeRelative(x, y)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewChipGroup() {
    ColorMasterTheme(darkTheme = true) {
        Box(
            modifier = Modifier.width(360.dp)
                .background(color = darkBackground)
        ) {
            FlexRow(spacing = 8.dp) {
                (0..10).forEach { i -> Chip("Chip #$i", false) }
            }
        }
    }
}
