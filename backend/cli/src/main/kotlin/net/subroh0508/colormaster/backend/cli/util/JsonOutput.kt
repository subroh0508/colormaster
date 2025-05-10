package net.subroh0508.colormaster.backend.cli.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.backend.cli.commands.FetchIdolColorsCommand.IdolColorResult

object JsonOutput {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }
    
    fun formatIdolColors(results: List<IdolColorResult>): String {
        return json.encodeToString(results)
    }
}
