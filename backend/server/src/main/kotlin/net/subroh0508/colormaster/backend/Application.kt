package net.subroh0508.colormaster.backend

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.backend.database.ColorMasterDatabase
import net.subroh0508.colormaster.backend.model.toDto
import java.io.File

fun main() {
    // データベースファイルをdata/ディレクトリに保存
    val dbDir = File("data").also { if (!it.exists()) it.mkdirs() }
    val dbFile = File(dbDir, "color_master.db")
    val driver = createSqlDriver(dbFile.absolutePath)
    val database = ColorMasterDatabase(driver)

    // ポート番号を環境変数から取得（デフォルトは8080）
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port, host = "0.0.0.0") { 
        module(database)
    }.start(wait = true)
}

private fun createSqlDriver(path: String): SqlDriver {
    val driver = JdbcSqliteDriver("jdbc:sqlite:$path")

    println(path)
    if (!File(path).exists()) {
        ColorMasterDatabase.Schema.create(driver)

        // サンプルデータの挿入
        val db = ColorMasterDatabase(driver)
        db.idolQueries.insertIdol(
            name = "天海春香",
            name_kana = "あまみはるか",
            color = "#e22b30",
            content_category = "アイドルマスター",
            content_title = "765AS"
        )
        db.idolQueries.insertIdol(
            name = "島村卯月",
            name_kana = "しまむらうづき",
            color = "#ff91a4",
            content_category = "アイドルマスター",
            content_title = "CinderellaGirls"
        )
    }

    return driver
}

fun Application.module(database: ColorMasterDatabase) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    routing {
        get("/") {
            call.respondText("COLOR M@STER バックエンドサーバーへようこそ！")
        }

        get("/api/status") {
            call.respondText("稼働中")
        }

        get("/api/idols") {
            val idols = database.idolQueries.selectAll().executeAsList()
            call.respond(idols.map { it.toDto() })
        }

        get("/api/idols/{id}") {
            val idParam = call.parameters["id"] ?: return@get call.respondText("Missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
            val id = idParam.toLongOrNull() ?: return@get call.respondText("Invalid id format", status = io.ktor.http.HttpStatusCode.BadRequest)
            val idol = database.idolQueries.selectById(id).executeAsOneOrNull()
                ?: return@get call.respondText("No idol with id $id", status = io.ktor.http.HttpStatusCode.NotFound)

            call.respond(idol.toDto())
        }
    }
}
