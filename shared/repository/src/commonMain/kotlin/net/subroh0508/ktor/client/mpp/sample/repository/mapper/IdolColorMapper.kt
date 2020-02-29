package net.subroh0508.ktor.client.mpp.sample.repository.mapper

import net.subroh0508.ktor.client.mpp.sample.domain.valueobject.IdolColor
import net.subroh0508.ktor.client.mpp.sample.repository.json.Response

fun Response.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (sMap, nameMap, colorMap) ->
    val id = sMap["value"] ?: return@mapNotNull null
    val name = nameMap["value"] ?: return@mapNotNull null
    val color = colorMap["value"] ?: return@mapNotNull null

    IdolColor(id, name, color)
}
