package components.atoms.tooltip

import androidx.compose.runtime.Composable
import material.components.Tooltip as MaterialTooltip

@Composable
fun Tooltip(id: String, text: String) = MaterialTooltip(id, text, 0L, 0L)
