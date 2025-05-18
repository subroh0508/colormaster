package net.subroh0508.colormaster.backend.cli.commands

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.subroh0508.colormaster.backend.database.ColorMasterDatabase
import org.yaml.snakeyaml.Yaml
import java.io.File

object ImportIdolsFromYamlCommand {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { execute() }
    }

    private const val INPUT_FILE_PATH = "result.yaml"
    private const val DB_FILE_NAME = "color_master.db"
    // プロジェクトルートからの相対パスでサーバーのdataディレクトリを指定
    private val DB_DIR_PATH = File("../../backend/server/data").absolutePath

    private suspend fun execute() {
        try {
            // YAMLファイルを読み込む
            val yamlFile = File(INPUT_FILE_PATH)
            if (!yamlFile.exists()) {
                System.err.println("Error: $INPUT_FILE_PATH not found")
                return
            }

            // データベース接続の準備
            val dbDir = File(DB_DIR_PATH).also { if (!it.exists()) it.mkdirs() }
            val dbFile = File(dbDir, DB_FILE_NAME)
            val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

            // データベースが存在しない場合はスキーマを作成
            if (!dbFile.exists()) {
                ColorMasterDatabase.Schema.create(driver)
            }

            val database = ColorMasterDatabase(driver)

            // YAMLファイルを解析
            val yaml = Yaml()
            val idolRecords = withContext(Dispatchers.IO) {
                yaml.load<List<Map<String, Any>>>(yamlFile.readText())
            }

            // レコードをデータベースに挿入（重複チェック付き）
            var insertedCount = 0
            var skippedCount = 0

            idolRecords.forEach { record ->
                // primary_keyを取得（YAMLファイル内のインデックス）
                val primaryKey = (record["primary_key"] as Int).toLong() + 1 // データベースのIDは1から始まるため+1

                // 同じIDのレコードがデータベースに存在するか確認
                val existingRecord = database.idolQueries.selectById(primaryKey).executeAsOneOrNull()

                // 重複チェック
                val isDuplicate = existingRecord != null

                val nameJa = record["name_ja"] as String
                val nameKanaJa = record["name_kana_ja"] as String
                val nameEn = record["name_en"] as String
                val color = (record["color"] as String).let { 
                    if (it.startsWith("#")) it else "#$it" 
                }
                val brand = record["brand"] as String

                if (!isDuplicate) {
                    // 重複がなければ挿入
                    database.idolQueries.insertIdol(
                        name_ja = nameJa,
                        name_kana_ja = nameKanaJa,
                        name_en = nameEn,
                        color = color,
                        content_category = "アイドルマスター", // 固定値として設定
                        content_title = brand
                    )
                    insertedCount++
                } else {
                    skippedCount++
                }
            }

            println("Import summary:")
            println("- Total records in YAML: ${idolRecords.size}")
            println("- New records inserted: $insertedCount")
            println("- Duplicate records skipped: $skippedCount")
            println("- Database location: ${dbFile.absolutePath}")
        } catch (e: Exception) {
            System.err.println("Error: ${e.message}")
            e.printStackTrace()
        }
    }
}
