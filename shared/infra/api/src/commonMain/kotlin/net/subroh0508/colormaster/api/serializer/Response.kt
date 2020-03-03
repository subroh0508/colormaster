package net.subroh0508.colormaster.api.serializer

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val head: Vars,
    @Serializable(with = ResultsSerializer::class)
    val results: Results<T>
) {
    @Serializable
    data class Vars(
        val vars: List<String>
    )

    data class Results<T>(
        val bindings: List<T>
    )
}
