package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.LiveNameDateRange
import net.subroh0508.colormaster.data.LiveNameTitle
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.data.mock.mockSuggestLiveName
import net.subroh0508.colormaster.data.module.buildLiveRepository
import net.subroh0508.colormaster.network.imasparql.query.SuggestLiveQuery
import net.subroh0508.colormaster.test.extension.flowToList

class DefaultLiveRepositorySpec : FunSpec({
    test("#suggest(by dateRange): it should return live names") {
        val range = "2020-01-01" to "2020-12-31"
        val repository = buildLiveRepository {
            mockSuggestLiveName(
                SuggestLiveQuery(dateRange = range) to LiveNameDateRange,
            )
        }

        val (instances, _) = flowToList(repository.getLiveNamesStream())

        repository.suggest(range)

        instances.let {
            it should haveSize(2)
            it.last() should containExactlyInAnyOrder(
                LiveName("THE IDOLM@STER CINDERELLA GIRLS 7thLIVE TOUR Special 3chord♪ Glowing Rock! Day1"),
                LiveName("THE IDOLM@STER CINDERELLA GIRLS 7thLIVE TOUR Special 3chord♪ Glowing Rock! Day2"),
                LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"),
                LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day2"),
            )
        }
    }

    test("#suggest(by name): it should return live names") {
        val name = "SHINY COLORS"
        val repository = buildLiveRepository {
            mockSuggestLiveName(
                SuggestLiveQuery(name = name) to LiveNameTitle,
            )
        }

        val (instances, _) = flowToList(repository.getLiveNamesStream())

        repository.suggest(name)

        instances.let {
            it should haveSize(2)
            it.last() should containExactlyInAnyOrder(
                LiveName("アソビストア presents THE IDOLM@STER SHINY COLORS SUMMER PARTY 2019 夜公演"),
                LiveName("アソビストア presents THE IDOLM@STER SHINY COLORS SUMMER PARTY 2019 昼公演"),
                LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"),
                LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day2"),
                LiveName("THE IDOLM@STER SHINY COLORS 2ndLIVE STEP INTO THE SUNSET SKY Day1"),
            )
        }
    }
})
