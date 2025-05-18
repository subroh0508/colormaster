package net.subroh0508.colormaster.backend.model

import kotlinx.serialization.Serializable
import net.subroh0508.colormaster.backend.database.Idol

@Serializable
data class IdolDto(
    val id: Long,
    val nameJa: String,
    val nameKanaJa: String,
    val nameEn: String,
    val color: String,
    val contentCategory: String,
    val contentTitle: String
)

fun Idol.toDto() = IdolDto(
    id = id,
    nameJa = name_ja,
    nameKanaJa = name_kana_ja,
    nameEn = name_en,
    color = color,
    contentCategory = content_category,
    contentTitle = content_title
)
