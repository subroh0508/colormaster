package net.subroh0508.colormaster.presentation.search.model

import androidx.annotation.StringRes
import net.subroh0508.colormaster.presentation.search.R

actual enum class SearchByTab(@StringRes val labelRes: Int) {
    BY_NAME(R.string.search_by_tab_name), BY_LIVE(R.string.search_by_tab_live)
}
