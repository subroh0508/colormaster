package net.subroh0508.colormaster.test

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*

fun mockHttpClient(
    handler: MockRequestHandler = { respondOk() },
) = HttpClient(MockEngine) {
    engine { addHandler(handler) }
}
