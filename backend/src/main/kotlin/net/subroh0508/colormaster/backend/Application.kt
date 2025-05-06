package net.subroh0508.colormaster.backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("COLOR M@STER バックエンドサーバーへようこそ！")
        }
        
        get("/api/status") {
            call.respondText("稼働中")
        }
    }
}
