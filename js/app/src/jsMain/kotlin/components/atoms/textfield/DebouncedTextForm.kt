package components.atoms.textfield

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun DebouncedTextForm(
    text: String?,
    timeoutMillis: Long,
    onDebouncedChange: (String?) -> Unit,
    textfield: @Composable (MutableState<String?>) -> Unit,
) {
    val textState = remember(text) { mutableStateOf(text) }
    val debouncedTextState = remember(text, timeoutMillis) { mutableStateOf(text) }

    LaunchedEffect(text, timeoutMillis) {
        snapshotFlow { textState.value }
            .onEach { debouncedTextState.value = textState.value }
            .launchIn(this)

        snapshotFlow { debouncedTextState.value }
            .debounce(timeoutMillis)
            .onEach(onDebouncedChange)
            .launchIn(this)
    }

    textfield(textState)
}
