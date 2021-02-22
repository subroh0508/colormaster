package net.subroh0508.colormaster.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

actual open class ViewModel actual constructor(coroutineScope: CoroutineScope?) {
    protected actual val viewModelScope = coroutineScope ?: MainScope()
}
