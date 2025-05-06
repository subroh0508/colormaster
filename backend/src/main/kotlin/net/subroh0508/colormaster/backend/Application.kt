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
    val dbFile = File("color_master.db")
    val driver = createSqlDriver(dbFile.absolutePath)
    val database = ColorMasterDatabase(driver)
    
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") { 
        module(database)
    }.start(wait = true)
}

private fun createSqlDriver(path: String): SqlDriver {
    val driver = JdbcSqliteDriver("jdbc:sqlite:$path")
    
    if (!File(path).exists()) {
        ColorMasterDatabase.Schema.create(driver)
        
        // サンプルデータの挿入
        val db = ColorMasterDatabase(driver)
        db.idolQueries.insertIdol(
            id = "amami_haruka",
            name = "天海春香",
            nameKana = "あまみはるか",
            color = "#e22b30",
            brand = "765AS"
        )
        db.idolQueries.insertIdol(
            id = "shimamura_uzuki",
            name = "島村卯月",
            nameKana = "しまむらうづき",
            color = "#ff91a4",
            brand = "CinderellaGirls"
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
            val id = call.parameters["id"] ?: return@get call.respondText("Missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
            val idol = database.idolQueries.selectById(id).executeAsOneOrNull()
                ?: return@get call.respondText("No idol with id $id", status = io.ktor.http.HttpStatusCode.NotFound)
            
            call.respond(idol.toDto())
        }
    }
}
