package net.subroh0508.colormaster.backend.cli.util

import net.subroh0508.colormaster.backend.cli.commands.FetchIdolColorsCommand.IdolColorResult
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

object YamlOutput {
    fun formatIdolColors(results: List<IdolColorResult>): String {
        val options = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            isPrettyFlow = true
        }
        
        val yaml = Yaml(options)
        
        // Sort results by brand and then by name
        val sortedResults = results.sortedWith(
            compareBy<IdolColorResult> { it.brand }
                .thenBy { it.name }
        )
        
        // Create a list of maps, each with a primary_key
        val dataList = sortedResults.mapIndexed { index, result ->
            mapOf(
                "primary_key" to index, // Integer primary_key starting from 0
                "id" to result.id,
                "name" to result.name,
                "color" to result.color,
                "brand" to result.brand
            )
        }
        
        return yaml.dump(dataList)
    }
}
