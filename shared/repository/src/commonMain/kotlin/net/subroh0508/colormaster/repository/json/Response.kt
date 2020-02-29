package net.subroh0508.colormaster.repository.json

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val head: Vars,
    val results: Result
) {
    @Serializable
    data class Vars(
        val vars: List<String>
    )

    @Serializable
    data class Result(
        val bindings: List<Binding>
    )

    @Serializable
    data class Binding(
        val s: Map<String, String>,
        val name: Map<String, String>,
        val color: Map<String, String>
    )
}
