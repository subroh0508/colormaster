package net.subroh0508.colormaster.utilities

import androidx.lifecycle.viewModelScope as androidViewModelScope
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as AndroidViewModel

actual open class ViewModel actual constructor(coroutineScope: CoroutineScope?) : AndroidViewModel() {
    protected actual val viewModelScope = coroutineScope ?: androidViewModelScope
}
