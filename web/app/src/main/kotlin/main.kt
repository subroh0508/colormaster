
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.repository.IdolColorsRepository
import kotlin.browser.window

private val httpClient: HttpClient = HttpClient(Js) {
    defaultRequest {
        accept(ContentType("application", "sparql-results+json"))
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json.nonstrict)
        acceptContentTypes += ContentType("application", "sparql-results+json")
    }
}

private val repository: IdolColorsRepository by lazy {
    IdolColorsRepository(httpClient)
}

fun main() {
    window.onload = {
        MainApplication(repository).loadItems()
    }
}
