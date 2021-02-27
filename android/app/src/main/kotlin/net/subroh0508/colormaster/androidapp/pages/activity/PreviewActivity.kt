package net.subroh0508.colormaster.androidapp.pages.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.pages.Preview
import net.subroh0508.colormaster.androidapp.previewIdolIds
import net.subroh0508.colormaster.androidapp.screenType
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PreviewActivity : AppCompatActivity() {
    private val viewModel: PreviewViewModel by viewModel()

    @ExperimentalMaterialApi
    @ExperimentalLayoutApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { Preview(intent.screenType, viewModel, ::finish) }

        if (intent.screenType == ScreenType.Penlight) {
            lifecycle.addObserver(penlightController)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetch(intent.previewIdolIds)
    }

    private val penlightController = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun keepScreenOn() {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun brightnessFull() {
            window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun brightnessNone() {
            window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
    }
}
