package net.subroh0508.colormaster.presentation.myidols.model

import net.subroh0508.colormaster.model.IdolColor

data class MyIdolsUiModel(
    val inCharges: List<IdolColor> = listOf(),
    val favorites: List<IdolColor> = listOf(),
    val selectedInCharges: List<String> = listOf(),
    val selectedFavorites: List<String> = listOf(),
)
