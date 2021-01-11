package net.subroh0508.colormaster.presentation.search.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.be
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.MockIdolColorsRepository
import net.subroh0508.colormaster.presentation.search.MockLiveRepository
import net.subroh0508.colormaster.presentation.search.everySearchByLive
import net.subroh0508.colormaster.presentation.search.everySuggest
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByLiveViewModel
import net.subroh0508.colormaster.test.TestScope

class SearchByLiveViewModelSpec : FunSpec() {
    private val observedUiModels: MutableList<SearchUiModel> = mutableListOf()

    private val liveRepository = MockLiveRepository()
    private val idolColorsRepository = MockIdolColorsRepository()

    lateinit var viewModel: SearchByLiveViewModel

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        viewModel = SearchByLiveViewModel(
            liveRepository,
            idolColorsRepository,
            TestScope() /* for JS Runtime */,
        )
        observedUiModels.clear()

        viewModel.uiModel.onEach { observedUiModels.add(it) }
            .launchIn(TestScope())
    }

    private val live2020 = listOf(
        LiveName("THE IDOLM@STER CINDERELLA GIRLS 7thLIVE TOUR Special 3chord♪ Glowing Rock! Day1"),
        LiveName("THE IDOLM@STER CINDERELLA GIRLS 7thLIVE TOUR Special 3chord♪ Glowing Rock! Day2"),
        LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"),
        LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day2"),
    )

    private val liveShinyColors = listOf(
        LiveName("アソビストア presents THE IDOLM@STER SHINY COLORS SUMMER PARTY 2019 昼公演"),
        LiveName("アソビストア presents THE IDOLM@STER SHINY COLORS SUMMER PARTY 2019 夜公演"),
        LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"),
        LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day2"),
    )

    private val byLive = LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1")
    private val byLiveIdols = listOf(
        IdolColor("Sakuragi_Mano", "櫻木真乃", HexColor("FFBAD6")),
        IdolColor("Kazano_Hiori", "風野灯織", HexColor("144384")),
        IdolColor("Hachimiya_Meguru", "八宮めぐる", HexColor("FFE012")),
    )

    private fun subject(block: () -> Unit): List<SearchUiModel> {
        block()
        return observedUiModels
    }

    init {
        test("#suggest: when input date it should post SearchUiModel with filled suggestions") {
            val params = SearchParams.ByLive.EMPTY.change("2020")

            liveRepository.everySuggest("2020-01-01" to "2020-12-31") { live2020 }

            subject { viewModel.setSearchParams(params) }.also { models ->
                models should haveSize(4)
                models.last() should {
                    it.items should beEmpty()
                    it.params should be(params)
                    (it as SearchUiModel.ByLive).suggests should containExactly(live2020)
                }
            }
        }

        test("#suggest: when input name it should post SearchUiModel with filled suggestions") {
            val params = SearchParams.ByLive.EMPTY.change("SHINY COLORS")

            liveRepository.everySuggest("SHINY COLORS") { liveShinyColors }

            subject { viewModel.setSearchParams(params) }.also { models ->
                models should haveSize(4)
                models.last() should {
                    it.items should beEmpty()
                    it.params should be(params)
                    (it as SearchUiModel.ByLive).suggests should containExactly(liveShinyColors)
                }
            }
        }

        test("#search: when search params is empty it should not post SearchUiModel") {
            subject { viewModel.setSearchParams(SearchParams.ByLive.EMPTY) }.also { models ->
                models should haveSize(1)
                models.last() should {
                    it.items should beEmpty()
                    it.params should be(SearchParams.ByLive.EMPTY)
                    (it as SearchUiModel.ByLive).suggests should beEmpty()
                }
            }
        }

        test("#search: when select live name it should post SearchUiModel with filled list") {
            val params = SearchParams.ByLive.EMPTY.select(byLive)

            idolColorsRepository.everySearchByLive(byLive) { byLiveIdols }

            subject { viewModel.setSearchParams(params) }.also { models ->
                models should haveSize(4)
                models.last() should {
                    it.items should containExactlyInAnyOrder(byLiveIdols.map(::IdolColorListItem))
                    it.params should be(params)
                    (it as SearchUiModel.ByLive).suggests should beEmpty()
                }
            }
        }
    }
}
