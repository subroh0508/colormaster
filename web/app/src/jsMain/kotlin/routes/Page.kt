package routes

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import net.subroh0508.colormaster.presentation.search.model.SearchParams

sealed interface Page : Parcelable

@Parcelize
data class Search(val initParams: SearchParams) : Page
@Parcelize
data class Preview(val ids: List<String>) : Page
@Parcelize
data class Penlight(val ids: List<String>) : Page
@Parcelize
object MyIdols : Page
@Parcelize
object HowToUse : Page
@Parcelize
object Development : Page
@Parcelize
object Terms : Page