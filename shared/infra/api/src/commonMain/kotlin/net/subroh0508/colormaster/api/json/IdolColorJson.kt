package net.subroh0508.colormaster.api.json

import kotlinx.serialization.Serializable

@Serializable
data class IdolColorJson internal constructor(
    val s: Map<String, String>,
    val name: Map<String, String>,
    val color: Map<String, String>
)