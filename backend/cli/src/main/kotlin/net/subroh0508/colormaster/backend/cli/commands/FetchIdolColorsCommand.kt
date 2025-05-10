package net.subroh0508.colormaster.backend.cli.commands

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.backend.cli.imasparql.ImasparqlApiClient
import net.subroh0508.colormaster.backend.cli.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.backend.cli.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.backend.cli.util.JsonOutput
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

class FetchIdolColorsCommand {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val httpClient = HttpClient(OkHttp) {
        engine {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }

    private val imasparqlClient = ImasparqlApiClient(httpClient, json)

    suspend fun execute(args: Array<String>) {
        try {
            // Parse command-line arguments
            val lang = args.findOption("--lang") ?: "ja"
            val outputPath = args.findOption("--output")
            val format = args.findOption("--format") ?: "csv"

            // Create the query
            val query = SearchByIdQuery(lang)

            // Execute the query
            val response = imasparqlClient.search(
                query.build(),
                IdolColorJson.serializer()
            )

            // Process the results
            val results = response.results.bindings.map { idolColor ->
                val id = idolColor.id["value"] ?: ""
                val name = idolColor.name["value"] ?: ""
                val color = idolColor.color["value"] ?: ""

                IdolColorResult(id, name, color)
            }

            // Output the results
            val output = formatResults(results, format)
            if (outputPath != null) {
                withContext(Dispatchers.IO) {
                    File(outputPath).writeText(output)
                }
                println("Results written to $outputPath")
            } else {
                println(output)
            }
        } catch (e: Exception) {
            System.err.println("Error: ${e.message}")
            e.printStackTrace()
        } finally {
            httpClient.close()
        }
    }

    private fun formatResults(results: List<IdolColorResult>, format: String = "csv"): String {
        return when (format.lowercase()) {
            "json" -> JsonOutput.formatIdolColors(results)
            "csv" -> buildString {
                appendLine("ID,Name,Color")
                results.forEach { result ->
                    appendLine("${result.id},${result.name},${result.color}")
                }
            }
            else -> throw IllegalArgumentException("Unsupported format: $format")
        }
    }

    private fun Array<String>.findOption(prefix: String): String? {
        return this.find { it.startsWith("$prefix=") }?.substringAfter("=")
    }

    data class IdolColorResult(
        val id: String,
        val name: String,
        val color: String
    )
}
