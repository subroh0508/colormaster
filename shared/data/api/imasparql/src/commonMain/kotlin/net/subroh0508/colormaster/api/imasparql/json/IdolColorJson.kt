package net.subroh0508.colormaster.api.imasparql.json

import kotlinx.serialization.Serializable

@Serializable
data class IdolColorJson internal constructor(
    val id: Map<String, String>,
    val name: Map<String, String>,
    val color: Map<String, String>
)
