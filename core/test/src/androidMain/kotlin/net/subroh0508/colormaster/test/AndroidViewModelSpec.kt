package net.subroh0508.colormaster.test

import androidx.annotation.CallSuper
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

actual abstract class ViewModelSpec actual constructor() : FunSpec() {
    private val testDispatcher: TestCoroutineDispatcher by lazy(::TestCoroutineDispatcher)

    @CallSuper
    override suspend fun beforeEach(testCase: TestCase) {
        Dispatchers.setMain(testDispatcher)
    }

    @CallSuper
    override fun afterSpec(f: suspend (Spec) -> Unit) {
        Dispatchers.resetMain()
    }
}
