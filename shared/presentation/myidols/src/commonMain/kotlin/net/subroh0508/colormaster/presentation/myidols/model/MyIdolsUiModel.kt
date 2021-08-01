package net.subroh0508.colormaster.presentation.myidols.model

import net.subroh0508.colormaster.model.IdolColor

data class MyIdolsUiModel(
    val managed: List<IdolColor> = listOf(),
    val favorites: List<IdolColor> = listOf(),
)