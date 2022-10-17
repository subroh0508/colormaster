package net.subroh0508.colormaster.base.extensions

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date().getTime().toLong()
