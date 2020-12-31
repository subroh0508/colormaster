package net.subroh0508.colormaster.test

import androidx.annotation.CallSuper
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

actual abstract class ViewModelSpec actual constructor() : FunSpec() {
    @ExperimentalCoroutinesApi
    private val testDispatcher: TestCoroutineDispatcher by lazy(::TestCoroutineDispatcher)
    @ExperimentalCoroutinesApi
    private val testScope: TestCoroutineScope by lazy { TestCoroutineScope(testDispatcher) }

    @CallSuper
    override fun beforeTest(testCase: TestCase) {
        Dispatchers.setMain(testDispatcher)
    }

    @CallSuper
    override fun afterTest(testCase: TestCase, result: TestResult) {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }
}
