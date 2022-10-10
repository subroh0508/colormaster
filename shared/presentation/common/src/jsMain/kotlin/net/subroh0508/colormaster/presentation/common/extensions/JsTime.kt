package net.subroh0508.colormaster.presentation.common.extensions

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date().getTime().toLong()
