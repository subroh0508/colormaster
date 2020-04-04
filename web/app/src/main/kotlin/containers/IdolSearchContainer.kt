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
    val (error, setError) = useState<String?>(null)

    fun Controller.search(idolName: IdolName? = null) = launch {
        runCatching { fetchItems(idolName) }
                .onSuccess {
                    setError(null)
                    setItems(it)
                }
                .onFailure {
                    console.log(it)
                    setError("エラーが発生しました")
                }
    }

    useEffectDidMount { controller.search() }
    useDebounceEffect(idolName, 500) { controller.search(it?.takeIf(String::isNotBlank)?.let(::IdolName)) }

    idolSearchPanel {
        attrs.items = items
        attrs.idolName = idolName
        attrs.error = error
        attrs.onChangeIdolName = { setIdolName(it) }
    }
}

private val IdolSearchController = createContext<Controller>()

private object Controller : CoroutineScope by mainScope, KodeinAware {
    const val LIMIT = 10

    val repository: IdolColorsRepository by instance()

    suspend fun fetchItems(
        idolName: IdolName?
    ) = if (idolName == null) repository.rand(LIMIT) else repository.search(idolName)

    override val kodein = appKodein
}
