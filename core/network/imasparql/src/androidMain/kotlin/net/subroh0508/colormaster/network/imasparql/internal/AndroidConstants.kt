package net.subroh0508.colormaster.network.imasparql.internal

import android.os.Build
import net.subroh0508.colormaster.network.imasparql.BuildConfig
import java.util.*

internal val UserAgent = "$APP_NAME/${BuildConfig.VERSION_CODE} (Android ${Build.VERSION.SDK_INT}; ${Locale.getDefault().language}; ${Build.PRODUCT})"
