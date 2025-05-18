package net.subroh0508.colormaster.backend.cli.commands

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.subroh0508.colormaster.backend.cli.util.YamlOutput
import net.subroh0508.colormaster.backend.database.ColorMasterDatabase
import java.io.File

object ExportIdolsToYamlCommand {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { execute() }
    }

    private const val OUTPUT_FILE_PATH = "exported_idols.yaml"
    // プロジェクトルートからの相対パスでサーバーのdataディレクトリを指定
    private val DB_DIR_PATH = File("../../backend/server/data").absolutePath
    private const val DB_FILE_NAME = "color_master.db"

    private suspend fun execute() {
        try {
            // データベース接続の準備
            val dbDir = File(DB_DIR_PATH).also { if (!it.exists()) it.mkdirs() }
            val dbFile = File(dbDir, DB_FILE_NAME)
            
            if (!dbFile.exists()) {
                System.err.println("Error: Database file not found at ${dbFile.absolutePath}")
                return
            }
            
            val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
            val database = ColorMasterDatabase(driver)
            
            // Fetch all idol records
            val idols = database.idolQueries.selectAll().executeAsList()
            
            // Convert to IdolColorResult format for YamlOutput
            val idolResults = idols.map { idol ->
                FetchIdolColorsCommand.IdolColorResult(
                    id = idol.name_en.replace(" ", "_"),  // Convert name to ID format
                    nameJa = idol.name_ja,
                    nameKanaJa = idol.name_kana_ja,
                    nameEn = idol.name_en,
                    color = idol.color.removePrefix("#"),  // Remove # prefix if present
                    brand = idol.content_title
                )
            }
            
            // Format as YAML
            val yamlOutput = YamlOutput.formatIdolColors(idolResults)
            
            // Write to file
            withContext(Dispatchers.IO) {
                File(OUTPUT_FILE_PATH).writeText(yamlOutput)
            }
            
            println("Successfully exported ${idols.size} idol records to $OUTPUT_FILE_PATH")
        } catch (e: Exception) {
            System.err.println("Error exporting idols to YAML: ${e.message}")
            e.printStackTrace()
        }
    }
}
