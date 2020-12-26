package net.subroh0508.colormaster.test

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun <T> runTest(block: suspend () -> T): dynamic = GlobalScope.promise { block() }
