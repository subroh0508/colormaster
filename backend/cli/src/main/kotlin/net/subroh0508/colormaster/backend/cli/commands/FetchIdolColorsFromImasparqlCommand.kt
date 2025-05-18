package net.subroh0508.colormaster.backend.cli.commands

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.backend.cli.imasparql.ImasparqlApiClient
import net.subroh0508.colormaster.backend.cli.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.backend.cli.imasparql.query.FetchAllIdolsQuery
import net.subroh0508.colormaster.backend.database.ColorMasterDatabase
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

object FetchIdolColorsFromImasparqlCommand {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { execute() }
    }

    private const val DB_FILE_NAME = "color_master.db"
    private const val CONTENT_CATEGORY_IMAS = "The Idolmaster"
    // プロジェクトルートからの相対パスでサーバーのdataディレクトリを指定
    private val DB_DIR_PATH = File("../../backend/server/data").absolutePath

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

            // Database connection setup
            val dbDir = File(DB_DIR_PATH).also { if (!it.exists()) it.mkdirs() }
            val dbFile = File(dbDir, DB_FILE_NAME)
            val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

            // Create database schema if it doesn't exist
            if (!dbFile.exists()) {
                ColorMasterDatabase.Schema.create(driver)
            }

            val database = ColorMasterDatabase(driver)

            // Insert records into database with duplicate checking by name_en and content_category
            var insertedCount = 0
            var skippedCount = 0

            // Sort results by brand and then by id for consistent ordering
            val sortedResults = results.sortedWith(
                compareBy<IdolColorResult> { it.brand }
                    .thenBy { it.id }
            )

            sortedResults.forEach { result ->
                // Check if a record with the same name_en, content_category, and content_title already exists
                val existingRecord = database.idolQueries.selectByNameEnAndContentCategoryAndTitle(
                    result.nameEn,
                    CONTENT_CATEGORY_IMAS,
                    result.brand
                ).executeAsList().firstOrNull()
            
                val isDuplicate = existingRecord != null

                // Ensure color has # prefix
                val formattedColor = if (result.color.startsWith("#")) result.color else "#${result.color}"

                if (!isDuplicate) {
                    // Insert new record
                    database.idolQueries.insertIdol(
                        name_ja = result.nameJa,
                        name_kana_ja = result.nameKanaJa,
                        name_en = result.nameEn,
                        color = formattedColor,
                        content_category = CONTENT_CATEGORY_IMAS,
                        content_title = result.brand
                    )
                    insertedCount++
                } else {
                    skippedCount++
                }
            }

            // Print summary
            println("Import summary:")
            println("- Total records from im@sparql: ${results.size}")
            println("- New records inserted: $insertedCount")
            println("- Duplicate records skipped: $skippedCount")
            println("- Database location: ${dbFile.absolutePath}")
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
