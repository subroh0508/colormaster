package utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arkivanov.decompose.value.Value

// @see: https://github.com/arkivanov/Decompose/blob/master/extensions-compose-jetbrains/src/commonMain/kotlin/com/arkivanov/decompose/extensions/compose/jetbrains/SubscribeAsState.kt
@Composable
fun <T : Any> Value<T>.subscribeAsState(): State<T> {
    val state = remember(this) { mutableStateOf(value) }

    DisposableEffect(this) {
        val observer: (T) -> Unit = { state.value = it }

        subscribe(observer)

        onDispose {
            unsubscribe(observer)
        }
    }

    return state
}
