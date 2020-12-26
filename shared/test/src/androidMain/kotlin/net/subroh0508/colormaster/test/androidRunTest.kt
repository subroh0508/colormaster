package net.subroh0508.colormaster.test

import kotlinx.coroutines.runBlocking

actual fun <T> runTest(block: suspend () -> T) { runBlocking { block() } }
