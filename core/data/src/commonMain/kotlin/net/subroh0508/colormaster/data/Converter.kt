package net.subroh0508.colormaster.data

import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.network.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.network.imasparql.serializer.Response

internal fun Response<IdolColorJson>.toIdolColors() = results
    .bindings
    .mapNotNull { (idMap, nameMap, colorMap) ->
        val id = idMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        val intColor = Triple(
            color.substring(0, 2).toInt(16),
            color.substring(2, 4).toInt(16),
            color.substring(4, 6).toInt(16),
        )

        IdolColor(id, name, intColor)
    }
