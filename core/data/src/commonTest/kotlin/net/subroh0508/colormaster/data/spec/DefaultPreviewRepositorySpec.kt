package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.IdolColorIdsEN
import net.subroh0508.colormaster.data.IdolColorIdsJA
import net.subroh0508.colormaster.data.LiveNameDateRange
import net.subroh0508.colormaster.data.LiveNameTitle
import net.subroh0508.colormaster.data.mock.mockIdolSearch
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.data.mock.mockSuggestLiveName
import net.subroh0508.colormaster.data.module.buildLiveRepository
import net.subroh0508.colormaster.data.module.buildPreviewRepository
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.network.imasparql.query.SuggestLiveQuery
import net.subroh0508.colormaster.test.extension.flowToList

class DefaultPreviewRepositorySpec : FunSpec({
    fun idsQuery(
        lang: String,
        ids: List<String>,
    ) = SearchByIdQuery(
        lang,
        ids,
    ) to (if (lang == "ja") IdolColorIdsJA else IdolColorIdsEN)

    listOf("ja", "en").forEach { lang ->
        test("#getPreviewColorsStream: when lang = '$lang' and sign in it should return idol list") {
            val ids = listOf("Tsukimura_Temari", "Mitsumine_Yuika")
            val repository = buildPreviewRepository {
                mockIdolSearch(
                    idsQuery(lang, ids),
                )
            }

            println("hash / ${repository.hashCode()}")
            val (instances, _) = flowToList(repository.getPreviewColorsStream())

            repository.show(ids, lang)
            repository.clear()

            instances.let {
                it should haveSize(3)
                it should containExactly(
                    listOf(),
                    listOf(
                        IdolColor("Tsukimura_Temari", if (lang == "ja") "月村手毬" else "Temari Tsukimura", Triple(79, 160, 206)),
                        IdolColor("Mitsumine_Yuika", if (lang == "ja") "三峰結華" else "Yuika Mitsumine", Triple(59, 145, 196)),
                    ),
                    listOf(),
                )
            }
        }
    }
})
