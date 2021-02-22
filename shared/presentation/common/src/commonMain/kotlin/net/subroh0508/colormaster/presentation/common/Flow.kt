package net.subroh0508.colormaster.presentation.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.throttleFirst(durationMillis: Long) = flow {
    var lastTime = 0L
    collect { value ->
        val currentTime = currentTimeMillis()
        if (durationMillis < currentTime - lastTime) {
            lastTime = currentTime
            emit(value)
        }
    }
}
