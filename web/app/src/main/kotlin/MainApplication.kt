import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.style
import net.subroh0508.colormaster.components.core.AppModule
import net.subroh0508.colormaster.domain.valueobject.IdolColor
import net.subroh0508.colormaster.domain.valueobject.IdolName
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import kotlin.browser.document
import kotlin.coroutines.CoroutineContext

class MainApplication : CoroutineScope, KodeinAware {
    override val coroutineContext: CoroutineContext get() = SupervisorJob()

    private val repository: IdolColorsRepository by instance()

    fun loadItems() {
        launch {
            runCatching { repository.search(IdolName("dummy")) }
                .onSuccess { showColors(it) }
                .onFailure { console.log(it) }
        }
    }

    private fun showColors(items: List<IdolColor>) {
        document.body?.append?.div {
            if (items.isEmpty()) {
                +"検索結果は0件です"
                return@div
            }

            items.forEach {
                div {
                    style = buildStyle(it)
                    +it.name
                    br
                    +it.color
                }
            }
        }
    }

    private fun buildStyle(item: IdolColor) = buildString {
        append("display: inline-block;")
        append("width: 200px;")
        append("height: 50px;")
        append("margin: 2px;")
        append("background-color: ${item.color};")
        append("color: ${if (item.isBrighter) "black" else "white"};")
        append("text-align: center;")
    }

    override val kodein = Kodein { import(AppModule) }
}
