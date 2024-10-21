package net.subroh0508.colormaster.features.preview.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IntColor
import net.subroh0508.colormaster.model.PreviewRepository

const val TEST_ID_1 = "Test_One"
const val TEST_ID_2 = "Test_Two"
const val TEST_ID_3 = "Test_Three"
const val TEST_ID_4 = "Test_Four"

val ENIdolColor1 = IdolColor(TEST_ID_1, "Test1", IntColor(255, 0, 0))
val ENIdolColor2 = IdolColor(TEST_ID_2, "Test2", IntColor(0, 255, 0))
val ENIdolColor3 = IdolColor(TEST_ID_3, "Test3", IntColor(0, 0, 255))
val ENIdolColor4 = IdolColor(TEST_ID_4, "Test4", IntColor(255, 255, 255))
val JAIdolColor1 = IdolColor(TEST_ID_1, "テスト1", IntColor(255, 0, 0))
val JAIdolColor2 = IdolColor(TEST_ID_2, "テスト2", IntColor(0, 255, 0))
val JAIdolColor3 = IdolColor(TEST_ID_3, "テスト3", IntColor(0, 0, 255))
val JAIdolColor4 = IdolColor(TEST_ID_4, "テスト4", IntColor(255, 255, 255))

class FakePreviewRepository : PreviewRepository {
    private val stateFlow = MutableStateFlow<List<IdolColor>>(listOf())

    override fun getPreviewColorsStream() = stateFlow.onStart { clear() }

    override fun clear() {
        stateFlow.value = listOf()
    }

    override suspend fun show(ids: List<String>, lang: String) {
        stateFlow.value = buildIdolColors(ids, lang)
    }

    private fun buildIdolColors(
        ids: List<String>,
        lang: String,
    ) = listOf(
        if (lang == "ja") JAIdolColor1 else ENIdolColor1,
        if (lang == "ja") JAIdolColor2 else ENIdolColor2,
        if (lang == "ja") JAIdolColor3 else ENIdolColor3,
        if (lang == "ja") JAIdolColor4 else ENIdolColor4,
    ).filter { ids.contains(it.id) }
}