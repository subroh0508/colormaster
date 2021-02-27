package net.subroh0508.colormaster.androidapp.pages.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.*
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.intentToPreview
import net.subroh0508.colormaster.androidapp.pages.Home
import net.subroh0508.colormaster.presentation.search.viewmodel.FavoritesViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {
    private val searchByNameViewModel: SearchByNameViewModel by viewModel()
    private val favoritesViewModel: FavoritesViewModel by viewModel()

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalLayoutApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { Home(searchByNameViewModel, favoritesViewModel, ::launchPreviewActivity) }
    }

    private fun launchPreviewActivity(
        type: ScreenType,
        ids: List<String>,
    ) = startActivity(intentToPreview(type, ids))
}
