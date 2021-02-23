package net.subroh0508.colormaster.api.imasparql.serializer

import kotlinx.serialization.Serializable

@Serializable
data class Response<out T>(
    val head: Vars,
    @Serializable(with = ResultsSerializer::class)
    val results: Results<T>
) {
    @Serializable
    data class Vars(
        val vars: List<String>
    )

    data class Results<out T>(
        val bindings: List<T>
    )
}
