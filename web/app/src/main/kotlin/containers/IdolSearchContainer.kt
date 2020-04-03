package containers

import appKodein
import components.organisms.idolColorGrids
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import mainScope
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import react.*
import utilities.useEffectDidMount
import kotlin.js.Promise

fun RBuilder.idolSearchContainer(handler: RHandler<RProps>) = child(functionalComponent<RProps> {
    val (items, setItems) = useState(listOf<IdolColor>())

    useEffectDidMount {
        Controller.loadItems(IdolName(""))
            .then { setItems(it) }
            .catch { console.log(it) }
    }

    idolColorGrids { attrs.items = items }
}, handler = handler)

private object Controller : CoroutineScope by mainScope, KodeinAware {
    private val repository: IdolColorsRepository by instance()

    fun loadItems(name: IdolName?): Promise<List<IdolColor>> = promise {
        repository.search(name)
    }

    override val kodein = appKodein
}
