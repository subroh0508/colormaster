package net.subroh0508.colormaster.backend.model

import kotlinx.serialization.Serializable
import net.subroh0508.colormaster.backend.database.Idol

@Serializable
data class IdolDto(
    val id: String,
    val name: String,
    val nameKana: String,
    val color: String,
    val brand: String?
)

fun Idol.toDto() = IdolDto(
    id = id,
    name = name,
    nameKana = nameKana,
    color = color,
    brand = brand
)
