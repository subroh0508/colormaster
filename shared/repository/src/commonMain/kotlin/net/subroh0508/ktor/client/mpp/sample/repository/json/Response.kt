package net.subroh0508.ktor.client.mpp.sample.repository.json

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val heads: List<String>,
    val results: Result
) {
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
