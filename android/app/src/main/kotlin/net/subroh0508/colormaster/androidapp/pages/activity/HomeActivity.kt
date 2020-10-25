package net.subroh0508.colormaster.androidapp.pages.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.material.*
import androidx.compose.ui.platform.setContent
import net.subroh0508.colormaster.androidapp.pages.Home
import net.subroh0508.colormaster.presentation.search.viewmodel.IdolSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {
    private val viewModel: IdolSearchViewModel by viewModel()

    @ExperimentalMaterialApi
    @ExperimentalLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { Home(viewModel) }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadRandom()
    }
}
