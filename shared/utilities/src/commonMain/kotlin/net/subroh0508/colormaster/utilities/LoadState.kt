package net.subroh0508.colormaster.utilities

sealed class LoadState {
    object Loading : LoadState()
    data class Loaded<out T>(val value: T) : LoadState()
    data class Error(val error: Throwable) : LoadState()

    val isLoading get() = this is Loading

    @Suppress("UNCHECKED_CAST")
    fun <T> getValueOrNull(): T? = (this as? Loaded<T>)?.value
    fun getErrorOrNull(): Throwable? = (this as? Error)?.error
}
