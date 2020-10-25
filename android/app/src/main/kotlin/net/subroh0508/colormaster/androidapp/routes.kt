package net.subroh0508.colormaster.androidapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import net.subroh0508.colormaster.androidapp.pages.activity.PreviewActivity
import kotlin.reflect.KClass

fun <T: AppCompatActivity> Context.intentTo(kClass: KClass<T>) = Intent().also {
    it.setClass(this, kClass.java)
}

const val PREVIEW_IDOL_IDS = "PREVIEW_IDOL_IDS"
enum class ScreenType {
    Preview, Penlight;

    companion object {
        const val KEY = "ScreenType.KEY"
    }
}

fun Context.intentToPreview(type: ScreenType, ids: List<String>) = intentTo(PreviewActivity::class).also {
    it.putExtra(ScreenType.KEY, type)
    it.putExtra(PREVIEW_IDOL_IDS, ids.toTypedArray())
}

val Intent.screenType get() = getSerializableExtra(ScreenType.KEY) as ScreenType
val Intent.previewIdolIds get() = getStringArrayListExtra(PREVIEW_IDOL_IDS).toList()
