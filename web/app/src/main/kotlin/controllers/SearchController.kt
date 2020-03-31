package controllers

import kotlinx.coroutines.CoroutineScope
import mainScope
import org.kodein.di.KodeinAware
import appKodein
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.erased.instance
import kotlin.js.Promise

object SearchController : CoroutineScope by mainScope, KodeinAware {
    private val repository: IdolColorsRepository by instance()

    fun loadItems(name: IdolName?): Promise<List<IdolColor>> = promise {
        repository.search(name)
    }

    override val kodein = appKodein
}
