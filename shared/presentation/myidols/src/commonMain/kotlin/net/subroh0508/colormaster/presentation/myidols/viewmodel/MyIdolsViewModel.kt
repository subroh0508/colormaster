package net.subroh0508.colormaster.presentation.myidols.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.subroh0508.colormaster.presentation.common.ViewModel
import net.subroh0508.colormaster.presentation.myidols.model.MyIdolsUiModel

class MyIdolsViewModel(
    coroutineScope: CoroutineScope? = null
) : ViewModel(coroutineScope) {
    val uiModel: StateFlow<MyIdolsUiModel> by lazy { MutableStateFlow(MyIdolsUiModel()) }
}
