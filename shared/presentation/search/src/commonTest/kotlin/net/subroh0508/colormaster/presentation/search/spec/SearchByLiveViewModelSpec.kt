package net.subroh0508.colormaster.presentation.search.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.be
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.*
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByLiveViewModel
import net.subroh0508.colormaster.test.TestScope
import net.subroh0508.colormaster.presentation.common.DateNum

class SearchByLiveViewModelSpec : FunSpec() {
    private val observedUiModels: MutableList<SearchUiModel> = mutableListOf()

    private val liveRepository = MockLiveRepository()
    private val idolColorsRepository = MockIdolColorsRepository()

    lateinit var viewModel: SearchByLiveViewModel

    override suspend fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)

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
        IdolColor("Sakuragi_Mano", "櫻木真乃", Triple(255, 186, 214)),
        IdolColor("Kazano_Hiori", "風野灯織", Triple(20, 67, 132)),
        IdolColor("Hachimiya_Meguru", "八宮めぐる", Triple(255, 224, 18)),
    )

    private fun subject(block: () -> Unit): List<SearchUiModel> {
        block()
        return observedUiModels
    }

    private val emptyParams = SearchParams.ByLive.EMPTY

    init {
        test("#changeLiveNameSuggestQuery: when input date it should post SearchUiModel with filled suggestions") {
            liveRepository.everySuggest("2020-01-01" to "2020-12-31") { live2020 }

            subject { viewModel.changeLiveNameSuggestQuery("2020") }.also { models ->
                models should haveSize(4)
                models.last() should {
                    it.items should beEmpty()
                    it.params should be(emptyParams.copy(date = DateNum(2020), suggests = live2020))
                }
            }
        }

        test("#changeLiveNameSuggestQuery: when input name it should post SearchUiModel with filled suggestions") {
            liveRepository.everySuggest("SHINY COLORS") { liveShinyColors }

            subject { viewModel.changeLiveNameSuggestQuery("SHINY COLORS") }.also { models ->
                models should haveSize(4)
                models.last() should {
                    it.items should beEmpty()
                    it.params should be(emptyParams.copy(query = "SHINY COLORS", suggests = liveShinyColors))
                }
            }
        }

        test("#changeLiveNameSuggestQuery: when input exactly live name it should post SearchUiModel with filled items") {
            liveRepository.everySuggest(byLive.value) { listOf(byLive) }
            idolColorsRepository.everySearchByLive(byLive) { byLiveIdols }

            subject { viewModel.changeLiveNameSuggestQuery(byLive.value) }.also { models ->
                models should haveSize(5)
                models.last() should {
                    it.items should containExactlyInAnyOrder(byLiveIdols.map(::IdolColorListItem))
                    it.params should be(emptyParams.copy(liveName = byLive))
                }
            }
        }

        test("#changeLiveNameSuggestQuery: when change name it should post SearchUiModel with filled list") {
            liveRepository.everySuggest(byLive.value) { listOf(byLive) }
            idolColorsRepository.everySearchByLive(byLive) { byLiveIdols }

            viewModel.changeLiveNameSuggestQuery(byLive.value)

            liveRepository.everySuggest("SHINY COLORS") { liveShinyColors }

            subject { viewModel.changeLiveNameSuggestQuery("SHINY COLORS") }.also { models ->
                models should haveSize(9)
                models.last() should {
                    it.items should beEmpty()
                    it.params should be(emptyParams.copy(query = "SHINY COLORS", suggests = liveShinyColors))
                }
            }
        }

        test("#setSearchParams: when search params is empty it should not post SearchUiModel") {
            liveRepository.everySuggest("SHINY COLORS") { liveShinyColors }

            subject {
                viewModel.changeLiveNameSuggestQuery("SHINY COLORS")
                viewModel.setSearchParams(SearchParams.ByLive.EMPTY)
            }.also { models ->
                models should haveSize(6)
                models.last() should {
                    it.items should beEmpty()
                    it.params should be(SearchParams.ByLive.EMPTY)
                }
            }
        }

        test("#setSearchParams: when select live name it should post SearchUiModel with filled list") {
            liveRepository.everySuggest("SHINY COLORS") { liveShinyColors }
            idolColorsRepository.everySearchByLive(byLive) { byLiveIdols }

            subject {
                viewModel.changeLiveNameSuggestQuery("SHINY COLORS")
                viewModel.setSearchParams(emptyParams.select(byLive))
            }.also { models ->
                models should haveSize(8)
                models.last() should {
                    it.items should containExactlyInAnyOrder(byLiveIdols.map(::IdolColorListItem))
                    it.params should be(emptyParams.copy(liveName = byLive))
                }
            }
        }

        test("#select: when change selected item it should post SearchUiModel with select state") {
            liveRepository.everySuggest(byLive.value) { listOf(byLive) }
            idolColorsRepository.everySearchByLive(byLive) { byLiveIdols }

            subject {
                viewModel.changeLiveNameSuggestQuery(byLive.value)
                viewModel.select(byLiveIdols[0], true)
                viewModel.select(byLiveIdols[0], false)
            }.also { models ->
                models should haveSize(7)
                models[models.lastIndex - 1] should {
                    it.items should haveSize(byLiveIdols.size)
                    it.selectedItems should be(listOf(byLiveIdols[0]))
                }
                models.last() should {
                    it.items should haveSize(byLiveIdols.size)
                    it.selectedItems should beEmpty()
                }
            }
        }

        test("#selectAll: when change selected item it should post SearchUiModel with select state") {
            liveRepository.everySuggest(byLive.value) { listOf(byLive) }
            idolColorsRepository.everySearchByLive(byLive) { byLiveIdols }

            subject {
                viewModel.changeLiveNameSuggestQuery(byLive.value)
                viewModel.selectAll(true)
                viewModel.selectAll(false)
            }.also { models ->
                models should haveSize(7)
                models[models.lastIndex - 1] should {
                    it.items should haveSize(byLiveIdols.size)
                    it.selectedItems should containExactlyInAnyOrder(byLiveIdols)
                }
                models.last() should {
                    it.items should haveSize(byLiveIdols.size)
                    it.selectedItems should beEmpty()
                }
            }
        }
    }
}
