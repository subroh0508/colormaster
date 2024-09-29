package net.subroh0508.colormaster.network.imasparql.json

import kotlinx.serialization.Serializable

@Serializable
data class LiveNameJson internal constructor(
    val name: Map<String, String>,
)
