package net.subroh0508.colormaster.androidapp.pages.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.pages.Preview
import net.subroh0508.colormaster.androidapp.previewIdolIds
import net.subroh0508.colormaster.androidapp.screenType
import net.subroh0508.colormaster.common.LocalKoinApp
import net.subroh0508.colormaster.common.koinApp

class PreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalKoinApp provides koinApp,
            ) { Preview(intent.screenType, intent.previewIdolIds, ::finish) }
        }

        if (intent.screenType == ScreenType.Penlight) {
            lifecycle.addObserver(penlightController)
        }
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
