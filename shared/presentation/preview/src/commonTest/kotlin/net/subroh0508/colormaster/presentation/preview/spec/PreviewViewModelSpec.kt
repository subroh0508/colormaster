package net.subroh0508.colormaster.presentation.preview.spec

import io.kotest.core.test.TestCase
import io.kotest.matchers.be
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.test.TestScope
import net.subroh0508.colormaster.test.ViewModelSpec
import net.subroh0508.colormaster.test.collectOnTestScope

class PreviewViewModelSpec : ViewModelSpec() {
    private val observedUiModels: MutableList<FullscreenPreviewUiModel> = mutableListOf()

    lateinit var repository: IdolColorsRepository
    lateinit var viewModel: PreviewViewModel

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        repository = object : IdolColorsRepository {
            override suspend fun favorite(id: String) = Unit
            override suspend fun unfavorite(id: String) = Unit
            override suspend fun getFavoriteIdolIds() = listOf<String>()
            override suspend fun rand(limit: Int) = listOf(
                IdolColor("Shimabara_Elena", "島原エレナ" , HexColor("9BCE92")),
                IdolColor("Abe_nana", "安部菜々", HexColor("E64A79")),
                IdolColor("Kohinata_Miho", "小日向美穂", HexColor("C64796")),
            )

            override suspend fun search(ids: List<String>) = listOf(
                IdolColor("Mitsumine_Yuika", "三峰結華", HexColor("3B91C4")),
                IdolColor("Hayami_Kanade", "速水奏", HexColor("0D386D")),
            )

            override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>) = listOf(
                IdolColor("Hidaka_Ai", "日高愛", HexColor("E85786")),
                IdolColor("Mizutani_Eri", "水谷絵理", HexColor("00ADB9")),
                IdolColor("Akizuki_Ryo_876", "秋月涼", HexColor("B2D468")),
            )
        }

        viewModel = PreviewViewModel(repository, TestScope())
        observedUiModels.clear()

        viewModel.uiModel.collectOnTestScope { observedUiModels.add(it) }
    }

    private fun subject(block: () -> Unit): List<FullscreenPreviewUiModel> {
        block()
        return observedUiModels
    }

    init {
        test("#fetch") {
            subject { viewModel.fetch(listOf("Mitsumine_Yuika", "Hayami_Kanade")) }.also { models ->
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
                    it.items should containExactlyInAnyOrder(
                        IdolColor("Mitsumine_Yuika", "三峰結華", HexColor("3B91C4")),
                        IdolColor("Hayami_Kanade", "速水奏", HexColor("0D386D")),
                    )
                    it.error should beNull()
                    it.isLoading should be(false)
                }
            }
        }
    }
}
