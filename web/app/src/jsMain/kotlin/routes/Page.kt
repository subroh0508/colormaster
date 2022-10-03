package routes

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import net.subroh0508.colormaster.presentation.search.model.SearchParams

sealed interface Page : Parcelable

@Parcelize
data class Search(val initParams: SearchParams) : Page {
    val query get() = when (initParams) {
        is SearchParams.ByName -> ""
        is SearchParams.ByLive -> "?by=live"
        else -> ""
    }

    constructor(query: String) : this(
        query.split("&").let { params ->
            val by = params.find { it.startsWith("by=") }?.replace("by=", "")

            if (by == "live") {
                return@let SearchParams.ByLive.EMPTY
            }

            SearchParams.ByName.EMPTY
        }
    )
}
@Parcelize
data class Preview(val ids: List<String>) : Page {
    val query get() = ids.takeIf(List<String>::isNotEmpty)?.let { ids ->
        "?${ids.joinToString("&") { "id=$it" }}"
    } ?: ""

    constructor(query: String) : this(
        query.split("&").mapNotNull {
            it.replace("id=", "").takeIf(String::isNotBlank)
        }
    )
}
@Parcelize
data class Penlight(val ids: List<String>) : Page {
    val query get() = ids.takeIf(List<String>::isNotEmpty)?.let { ids ->
        "?${ids.joinToString("&") { "id=$it" }}"
    } ?: ""

    constructor(query: String) : this(
        query.split("&").mapNotNull {
            it.replace("id=", "").takeIf(String::isNotBlank)
        }
    )
}
@Parcelize
object MyIdols : Page
@Parcelize
object HowToUse : Page
@Parcelize
object Development : Page
@Parcelize
object Terms : Page