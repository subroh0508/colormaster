package net.subroh0508.colormaster.test

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*

fun mockApi(
    handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
) = HttpClient(MockEngine) {
    engine { addHandler(handler) }
}
