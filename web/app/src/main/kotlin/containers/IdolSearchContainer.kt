package containers

import appKodein
import components.organisms.idolColorGrids
import components.templates.idolSearchPanel
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

val IdolSearchContainer = functionalComponent<RProps> {
    val (items, setItems) = useState(listOf<IdolColor>())
    val (idolName, setIdolName) = useState<String?>(null)

    fun search(idolName: IdolName?) = Controller.loadItems(
        idolName,
        resolve = { setItems(it) },
        reject = { console.log(it) }
    )

    useEffectDidMount { search(IdolName("")) }
    useEffect(listOf(idolName)) { search(idolName?.let(::IdolName)) }

    idolSearchPanel {
        attrs.items = items
        attrs.idolName = idolName
        attrs.onChangeIdolName = { setIdolName(it) }
    }
}

private object Controller : CoroutineScope by mainScope, KodeinAware {
    private val repository: IdolColorsRepository by instance()

    fun loadItems(
        name: IdolName?,
        resolve: (List<IdolColor>) -> Unit,
        reject: (Throwable) -> Unit
    ) = promise { repository.search(name) }
        .then(resolve)
        .catch(reject)

    override val kodein = appKodein
}
