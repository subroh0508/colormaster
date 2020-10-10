package net.subroh0508.colormaster.model.ui.commons

sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    data class Loaded<out T>(val value: T) : LoadState<T>()
    data class Error(val error: Throwable) : LoadState<Nothing>()

    val isLoading get() = this is Loading
    fun getValueOrNull(): T? = (this as? Loaded)?.value
    fun getErrorOrNull(): Throwable? = (this as? Error)?.error
}

