package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

class AddIdolToInChargeUseCase(
    private val repository: IdolColorsRepository,
    private val scope: CoroutineScope,
    private val inCharges: MutableState<List<String>> = mutableStateOf(listOf()),
) : State<List<String>> by inCharges {
    init {
        scope.launch {
            runCatching {
                repository.getInChargeOfIdolIds()
            }
                .onSuccess { inCharges.value = it }
                .onFailure { inCharges.value = listOf() }
        }
    }

    fun add(id: String, inCharge: Boolean) {
        scope.launch {
            runCatching {
                if (inCharge)
                    repository.registerInChargeOf(id)
                else
                    repository.unregisterInChargeOf(id)
                repository.getInChargeOfIdolIds()
            }
                .onSuccess { inCharges.value = it }
                .onFailure { inCharges.value = listOf() }
        }
    }
}

@Composable
fun rememberAddIdolToInChargeUseCase(
    isSignedIn: Boolean,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): AddIdolToInChargeUseCase {
    val scope = rememberCoroutineScope()

    return remember(koinApp, isSignedIn) { AddIdolToInChargeUseCase(koinApp.koin.get(), scope) }
}
