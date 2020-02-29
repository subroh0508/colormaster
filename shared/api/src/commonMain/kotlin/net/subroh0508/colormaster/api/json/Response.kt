package net.subroh0508.colormaster.api.json

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val head: Vars,
    val results: Result<T>
) {
    @Serializable
    data class Vars(
        val vars: List<String>
    )

    @Serializable
    data class Result<T>(
        val bindings: List<T>
    )
}
