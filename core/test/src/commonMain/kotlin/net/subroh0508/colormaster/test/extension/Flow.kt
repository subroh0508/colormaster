package net.subroh0508.colormaster.test.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> flowToList(
    flow: Flow<T>,
): Pair<List<T>, TestCoroutineScheduler> {
    val instances = mutableListOf<T>()
    val scheduler = TestCoroutineScheduler()

    flow
        .onEach(instances::add)
        .stateIn(CoroutineScope(UnconfinedTestDispatcher(scheduler)))

    return instances to scheduler
}
