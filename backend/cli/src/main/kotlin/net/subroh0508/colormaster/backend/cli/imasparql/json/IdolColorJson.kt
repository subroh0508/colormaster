package net.subroh0508.colormaster.backend.cli.imasparql.json

import kotlinx.serialization.Serializable

@Serializable
data class IdolColorJson(
    val id: Map<String, String>,
    val nameJa: Map<String, String>,
    val nameKanaJa: Map<String, String>,
    val nameEn: Map<String, String>,
    val color: Map<String, String>,
    val brandName: Map<String, String>
)
