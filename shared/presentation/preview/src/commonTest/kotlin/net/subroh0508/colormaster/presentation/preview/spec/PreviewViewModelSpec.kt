package net.subroh0508.colormaster.presentation.preview.spec

import io.kotest.core.test.TestCase
import io.kotest.matchers.be
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.test.TestScope
import net.subroh0508.colormaster.test.ViewModelSpec
import net.subroh0508.colormaster.test.collectOnTestScope

class PreviewViewModelSpec : ViewModelSpec() {
    private val observedUiModels: MutableList<FullscreenPreviewUiModel> = mutableListOf()

    @MockK
    lateinit var repository: IdolColorsRepository

    lateinit var viewModel: PreviewViewModel

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)

        viewModel = PreviewViewModel(repository, TestScope()/* for JS Runtime */)
        observedUiModels.clear()

        viewModel.uiModel.collectOnTestScope { observedUiModels.add(it) }
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
            coEvery { repository.search(idols.map(IdolColor::id)) } returns idols

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

        test("#fetch: when repository#search raises Exception it should post FullscreenPreviewUiModel with empty") {
            val error = IllegalStateException()

            coEvery { repository.search(idols.map(IdolColor::id)) } throws error

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
