package containers

import appKodein
import components.templates.idolSearchPanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import react.*
import utilities.useDebounceEffect
import utilities.useEffectDidMount

@Suppress("FunctionName")
fun RBuilder.IdolSearchContainer() = IdolSearchController.Provider(Controller) { child(IdolSearchContainerImpl) }

private val IdolSearchContainerImpl = functionalComponent<RProps> {
    val controller = useContext(IdolSearchController)

    val (items, setItems) = useState(listOf<IdolColor>())
    val (idolName, setIdolName) = useState<String?>(null)

    fun Controller.search(idolName: IdolName?) = launch {
        runCatching { repository.search(idolName) }
                .onSuccess(setItems)
                .onFailure { console.log(it) }
    }

    useEffectDidMount { controller.search(IdolName("")) }
    useDebounceEffect(idolName, 500) { controller.search(it?.let(::IdolName)) }

    idolSearchPanel {
        attrs.items = items
        attrs.idolName = idolName
        attrs.onChangeIdolName = { setIdolName(it) }
    }
}

private val IdolSearchController = createContext<Controller>()

private object Controller : CoroutineScope by mainScope, KodeinAware {
    val repository: IdolColorsRepository by instance()

    override val kodein = appKodein
}
