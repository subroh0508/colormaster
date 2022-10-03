package net.subroh0508.colormaster.presentation.preview.spec

import io.kotest.core.test.TestCase
import io.kotest.matchers.be
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.presentation.preview.MockIdolColorsRepository
import net.subroh0508.colormaster.presentation.preview.mockSearch
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import net.subroh0508.colormaster.test.TestScope
import net.subroh0508.colormaster.test.ViewModelSpec

class PreviewViewModelSpec : ViewModelSpec() {
    private val observedUiModels: MutableList<FullscreenPreviewUiModel> = mutableListOf()

    private val repository = MockIdolColorsRepository()

    lateinit var viewModel: PreviewViewModel

    override suspend fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)

        viewModel = PreviewViewModel(repository, TestScope()/* for JS Runtime */)
        observedUiModels.clear()

        viewModel.uiModel.onEach { observedUiModels.add(it) }
            .launchIn(TestScope())
    }

    private fun subject(block: () -> Unit): List<FullscreenPreviewUiModel> {
        block()
        return observedUiModels
    }

    init {
        val idols = listOf(
            IdolColor("Mitsumine_Yuika", "三峰結華", HexColor("3B91C4")),
            IdolColor("Hayami_Kanade", "速水奏", HexColor("0D386D")),
        )

        test("#fetch: when repository#search returns idols colors it should post FullscreenPreviewUiModel with filled list") {
            repository.mockSearch(idols.map(IdolColor::id)) { idols }

            subject { viewModel.fetch(idols.map(IdolColor::id)) }.also { models ->
                models should haveSize(3)
                models[0] should {
                    it.items should beEmpty()
                    it.error should beNull()
                    it.isLoading should be(false)
                }
                models[1] should {
                    it.items should beEmpty()
                    it.error should beNull()
                    it.isLoading should be(true)
                }
                models[2] should {
                    it.items should containExactlyInAnyOrder(idols)
                    it.error should beNull()
                    it.isLoading should be(false)
                }
            }
        }

        test("#fetch: when repository#search raises Exception it should post FullscreenPreviewUiModel with empty list") {
            val error = IllegalStateException()

            repository.mockSearch(idols.map(IdolColor::id)) { throw error }

            subject { viewModel.fetch(idols.map(IdolColor::id)) }.also { models ->
                models should haveSize(3)
                models[0] should {
                    it.items should beEmpty()
                    it.error should beNull()
                    it.isLoading should be(false)
                }
                models[1] should {
                    it.items should beEmpty()
                    it.error should beNull()
                    it.isLoading should be(true)
                }
                models[2] should {
                    it.items should beEmpty()
                    it.error should be(error)
                    it.isLoading should be(false)
                }
            }
        }
    }
}
