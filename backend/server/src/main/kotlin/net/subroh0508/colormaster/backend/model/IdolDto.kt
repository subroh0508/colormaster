package net.subroh0508.colormaster.backend.model

import kotlinx.serialization.Serializable
import net.subroh0508.colormaster.backend.database.Idol

@Serializable
data class IdolDto(
    val id: Long,
    val name: String,
    val nameKana: String,
    val color: String,
    val contentCategory: String,
    val contentTitle: String
)

fun Idol.toDto() = IdolDto(
    id = id,
    name = name,
    nameKana = name_kana,
    color = color,
    contentCategory = content_category,
    contentTitle = content_title
)
