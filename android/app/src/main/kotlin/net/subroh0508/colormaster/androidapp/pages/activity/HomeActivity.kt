package net.subroh0508.colormaster.androidapp.pages.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.*
import androidx.compose.runtime.CompositionLocalProvider
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.intentToPreview
import net.subroh0508.colormaster.androidapp.pages.Home
import net.subroh0508.colormaster.common.LocalKoinApp
import net.subroh0508.colormaster.common.koinApp

class HomeActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalLayoutApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalKoinApp provides koinApp,
            ) { Home(::launchPreviewActivity) }
        }
    }

    private fun launchPreviewActivity(
        type: ScreenType,
        ids: List<String>,
    ) = startActivity(intentToPreview(type, ids))
}
