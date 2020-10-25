package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.organisms.StaticColorLists
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel

@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
fun Preview(type: ScreenType, viewModel: PreviewViewModel) {
    val uiModel by viewModel.uiModel.collectAsState(initial = FullscreenPreviewUiModel.INITIALIZED)

    StaticColorLists(type, uiModel.items)
}
