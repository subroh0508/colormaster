package net.subroh0508.colormaster.utilities

import kotlinx.coroutines.CoroutineScope

expect open class ViewModel constructor(coroutineScope: CoroutineScope? = null) {
    protected val viewModelScope: CoroutineScope
}
