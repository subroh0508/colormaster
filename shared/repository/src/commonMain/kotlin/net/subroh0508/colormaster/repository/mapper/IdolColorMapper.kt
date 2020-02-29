package net.subroh0508.colormaster.repository.mapper

import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response
import net.subroh0508.colormaster.domain.valueobject.IdolColor

fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (sMap, nameMap, colorMap) ->
    val id = sMap["value"] ?: return@mapNotNull null
    val name = nameMap["value"] ?: return@mapNotNull null
    val color = colorMap["value"] ?: return@mapNotNull null

    IdolColor(id, name, color)
}
