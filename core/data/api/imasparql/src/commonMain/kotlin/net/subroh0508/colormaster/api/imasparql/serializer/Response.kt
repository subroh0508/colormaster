package net.subroh0508.colormaster.api.imasparql.serializer

import kotlinx.serialization.Serializable

@Serializable
data class Response<out T>(
    val head: Vars,
    val results: Results<T>
) {
    @Serializable
    data class Vars(
        val vars: List<String>
    )

    @Serializable
    data class Results<out T>(
        val bindings: List<T>
    )
}
