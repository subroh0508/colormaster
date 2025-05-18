package net.subroh0508.colormaster.backend.cli.commands

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.backend.cli.imasparql.ImasparqlApiClient
import net.subroh0508.colormaster.backend.cli.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.backend.cli.imasparql.query.FetchAllIdolsQuery
import net.subroh0508.colormaster.backend.cli.util.YamlOutput
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

object FetchIdolColorsCommand {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { execute() }
    }


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

    private suspend fun execute() {
        try {
            val outputPath = "result.yaml"

            // Create the query
            val query = FetchAllIdolsQuery

            // Execute the query
            val response = imasparqlClient.search(
                query.build(),
                IdolColorJson.serializer()
            )

            // Process the results
            val results = response.results.bindings.map { idolColor ->
                val id = idolColor.id["value"] ?: ""
                val nameJa = idolColor.nameJa["value"] ?: ""
                val nameKanaJa = idolColor.nameKanaJa["value"] ?: ""
                val nameEn = idolColor.nameEn["value"] ?: ""
                val color = idolColor.color["value"] ?: ""
                val brand = idolColor.brandName["value"] ?: ""

                IdolColorResult(
                    id,
                    nameJa,
                    nameKanaJa,
                    nameEn,
                    color,
                    brand,
                )
            }

            // Output the results
            val output = YamlOutput.formatIdolColors(results)
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

    data class IdolColorResult(
        val id: String,
        val nameJa: String,
        val nameKanaJa: String,
        val nameEn: String,
        val color: String,
        val brand: String
    )
}
