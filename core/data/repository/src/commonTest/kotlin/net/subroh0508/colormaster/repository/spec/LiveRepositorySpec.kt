package net.subroh0508.colormaster.repository.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.ktor.client.*
import net.subroh0508.colormaster.network.imasparql.di.Api
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.repository.LiveRepository
import net.subroh0508.colormaster.repository.di.LiveRepositories
import net.subroh0508.colormaster.repository.mock.mockSuggestLiveName
import org.koin.dsl.koinApplication

class LiveRepositorySpec : FunSpec() {
    private fun mockApi(
        block: () -> HttpClient,
    ): LiveRepository = koinApplication {
        modules(
            Api.Module(block()) + LiveRepositories.Module
        )
    }.koin.get(LiveRepository::class)

    init {
        val live2020 = listOf(
            LiveName("THE IDOLM@STER CINDERELLA GIRLS 7thLIVE TOUR Special 3chord♪ Glowing Rock! Day1"),
            LiveName("THE IDOLM@STER CINDERELLA GIRLS 7thLIVE TOUR Special 3chord♪ Glowing Rock! Day2"),
            LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"),
            LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day2"),
        )

        test("#suggest(by dateRange): it should return live names") {
            val range = "2020-01-01" to "2020-12-31"
            val repository = mockApi {
                mockSuggestLiveName(range, live2020)
            }

            repository.suggest(range) should containExactly(live2020)
        }

        val liveShinyColors = listOf(
            LiveName("アソビストア presents THE IDOLM@STER SHINY COLORS SUMMER PARTY 2019 昼公演"),
            LiveName("アソビストア presents THE IDOLM@STER SHINY COLORS SUMMER PARTY 2019 夜公演"),
            LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"),
            LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day2"),
        )

        test("#suggest(by name): it should return live names") {
            val name = "SHINY COLORS"
            val repository = mockApi {
                mockSuggestLiveName(name, liveShinyColors)
            }

            repository.suggest(name) should containExactly(liveShinyColors)
        }
    }
}
