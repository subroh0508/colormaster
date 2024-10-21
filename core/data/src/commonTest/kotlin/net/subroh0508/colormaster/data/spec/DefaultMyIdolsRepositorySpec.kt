package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.IdolColorIdsEN
import net.subroh0508.colormaster.data.IdolColorIdsJA
import net.subroh0508.colormaster.data.extension.setUserDocument
import net.subroh0508.colormaster.data.mock.mockIdolSearch
import net.subroh0508.colormaster.data.module.buildMyIdolsRepository
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.test.extension.flowToList

class DefaultMyIdolsRepositorySpec : FunSpec({
    fun idsQuery(
        lang: String,
        ids: List<String>,
    ) = SearchByIdQuery(
        lang,
        ids,
    ) to (if (lang == "ja") IdolColorIdsJA else IdolColorIdsEN)

    listOf("ja", "en").forEach { lang ->
        test("#getInChargeOfIdolsStream: when lang = '$lang' and sign in it should return idol list") {
            val ids = listOf("Tsukimura_Temari", "Mitsumine_Yuika")
            val (repository, auth, firestore) = buildMyIdolsRepository {
                mockIdolSearch(
                    idsQuery(lang, ids),
                )
            }
            setUserDocument(auth, firestore, inChargeIds = ids)

            val (instances, _) = flowToList(repository.getInChargeOfIdolsStream(lang))

            instances.let {
                it should haveSize(1)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Tsukimura_Temari", if (lang == "ja") "月村手毬" else "Temari Tsukimura", Triple(79, 160, 206)),
                    IdolColor("Mitsumine_Yuika", if (lang == "ja") "三峰結華" else "Yuika Mitsumine", Triple(59, 145, 196)),
                )
            }
        }

        test("#getFavoriteIdolsStream: when lang = '$lang' and sign in it should return idol list") {
            val ids = listOf("Tsukimura_Temari", "Mitsumine_Yuika")
            val (repository, auth, firestore) = buildMyIdolsRepository {
                mockIdolSearch(
                    idsQuery(lang, ids),
                )
            }
            setUserDocument(auth, firestore, favoriteIds = ids)

            val (instances, _) = flowToList(repository.getFavoriteIdolsStream(lang))

            instances.let {
                it should haveSize(1)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Tsukimura_Temari", if (lang == "ja") "月村手毬" else "Temari Tsukimura", Triple(79, 160, 206)),
                    IdolColor("Mitsumine_Yuika", if (lang == "ja") "三峰結華" else "Yuika Mitsumine", Triple(59, 145, 196)),
                )
            }
        }
    }
})
