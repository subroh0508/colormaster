package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.organisms.StaticColorLists
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.features.preview.rememberFetchIdolsUseCase
import net.subroh0508.colormaster.model.IdolColor

@Composable
fun Preview(
    type: ScreenType,
    ids: List<String>,
    finish: () -> Unit,
) = ColorMasterTheme {
    val idolColorLoadState by rememberFetchIdolsUseCase(ids)

    val items: List<IdolColor> = idolColorLoadState.getValueOrNull() ?: listOf()
    val isLoading = idolColorLoadState.isLoading
    val error = idolColorLoadState.getErrorOrNull()

    when {
        isLoading -> LoadingDialog(finish)
        error != null -> ErrorDialog(error, finish)
        else -> StaticColorLists(type, items)
    }
}

@Composable
private fun LoadingDialog(
    confirmButton: () -> Unit,
) = AlertDialog(
    onDismissRequest = {},
    text = { Text(stringResource(R.string.loading)) },
    confirmButton = {
        TextButton(onClick = confirmButton) {
            Text(stringResource(R.string.cancel))
        }
    }
)

@Composable
private fun ErrorDialog(
    error: Throwable,
    confirmButton: () -> Unit,
) = AlertDialog(
    onDismissRequest = {},
    title = { Text(stringResource(R.string.error)) },
    text = {
        Column {
            Text(stringResource(R.string.preview_loading_error))
            Text(error.message ?: "Unknown")
        }
    },
    confirmButton = {
        TextButton(onClick = confirmButton) {
            Text(stringResource(R.string.close))
        }
    }
)
