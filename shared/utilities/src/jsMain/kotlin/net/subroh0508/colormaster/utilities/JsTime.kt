package net.subroh0508.colormaster.utilities

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date().getTime().toLong()
