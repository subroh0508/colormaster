package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.*
import net.subroh0508.colormaster.data.extension.signInWithGoogle
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.data.mock.*
import net.subroh0508.colormaster.data.module.buildIdolColorsRepository
import net.subroh0508.colormaster.network.imasparql.query.RandomQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByLiveQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByNameQuery
import net.subroh0508.colormaster.test.extension.flowToList
import net.subroh0508.colormaster.test.mockHttpClient

class DefaultIdolColorsRepositorySpec : FunSpec({
    fun randomQuery(lang: String) = RandomQuery(
        lang,
        limit = 10,
    ) to (if (lang == "ja") IdolColorRandomJA else IdolColorRandomEN)

    fun searchQuery(
        lang: String,
        name: IdolName? = null,
        brands: Brands? = null,
        types: Set<Types> = setOf(),
        ja: String,
        en: String,
    ) = SearchByNameQuery(
        lang,
        name?.value,
        brands?.queryStr,
        types.map(Types::queryStr),
    ) to (if (lang == "ja") ja else en)

    fun searchQuery(
        lang: String,
        name: LiveName,
    ) = SearchByLiveQuery(
        lang,
        name.value,
    ) to (if (lang == "ja") IdolColorLiveNameJA else IdolColorLiveNameEN)

    fun idsQuery(
        lang: String,
        ids: List<String>,
    ) = SearchByIdQuery(
        lang,
        ids,
    ) to (if (lang == "ja") IdolColorIdsJA else IdolColorIdsEN)

    listOf("ja", "en").forEach { lang ->
        test("#getIdolColorsStream: when lang = '$lang' it should return idols at random") {
            val (repository, _, _) = buildIdolColorsRepository {
                mockIdolSearch(
                    randomQuery(lang),
                )
            }

            val (instances, _) = flowToList(repository.getIdolColorsStream(10, lang))

            instances.let {
                it should haveSize(1)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Morino_Rinze", if (lang == "ja") "杜野凛世" else "Rinze Morino", Triple(137, 195, 235)),
                    IdolColor("Momose_Rio", if (lang == "ja") "百瀬莉緒" else "Rio Momose", Triple(241, 149, 145)),
                    IdolColor("Maihama_Ayumu", if (lang == "ja") "舞浜歩" else "Ayumu Maihama", Triple(226, 90, 155)),
                    IdolColor("Wakiyama_Tamami", if (lang == "ja") "脇山珠美" else "Tamami Wakiyama", Triple(58, 117, 187)),
                    IdolColor("Kitamura_Sora", if (lang == "ja") "北村想楽" else "Sora Kitamura", Triple(71, 117, 37)),
                    IdolColor("Osaki_Tenka", if (lang == "ja") "大崎甜花" else "Tenka Osaki", Triple(231, 91, 236)),
                    IdolColor("Hazama_Michio", if (lang == "ja") "硲道夫" else "Michio Hazama", Triple(67, 108, 169)),
                    IdolColor("Futami_Mami", if (lang == "ja") "双海真美" else "Mami Futami", Triple(255, 228, 63)),
                    IdolColor("Takagaki_Kaede", if (lang == "ja") "高垣楓" else "Kaede Takagaki", Triple(71, 215, 172)),
                    IdolColor("Himeno_Kanon", if (lang == "ja") "姫野かのん" else "Kanon Himeno", Triple(247, 181, 196)),
                )
            }
        }

        test("#search(by name): when lang = '$lang' it should return idols") {
            val params = IdolName(if (lang == "ja") "久川" else "Hisakawa")

            val (repository, _, _) = buildIdolColorsRepository {
                mockIdolSearch(
                    randomQuery(lang),
                    searchQuery(
                        lang,
                        name = params,
                        ja = IdolColorNameJA,
                        en = IdolColorNameEN,
                    ),
                )
            }

            val (instances, _) = flowToList(repository.getIdolColorsStream(10, lang))

            repository.search(
                name = params,
                brands = null,
                types = setOf(),
                lang = lang,
            )

            instances.let {
                it should haveSize(2)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Hisakawa_Nagi", if (lang == "ja") "久川凪" else "Nagi Hisakawa", Triple(247, 161, 186)),
                    IdolColor("Hisakawa_Hayate", if (lang == "ja") "久川颯" else "Hayate Hisakawa", Triple(122, 218, 214)),
                )
            }
        }

        test("#search(by brand): when lang = '$lang' it should return idols") {
            val (repository, _, _) = buildIdolColorsRepository {
                mockIdolSearch(
                    randomQuery(lang),
                    searchQuery(
                        lang,
                        brands = Brands._876,
                        ja = IdolColorBrandJA,
                        en = IdolColorBrandEN,
                    ),
                )
            }

            val (instances, _) = flowToList(repository.getIdolColorsStream(10, lang))

            repository.search(
                name = null,
                brands = Brands._876,
                types = setOf(),
                lang = lang,
            )

            instances.let {
                it should haveSize(2)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Hidaka_Ai", if (lang == "ja") "日高愛" else "Ai Hidaka", Triple(232, 87, 134)),
                    IdolColor("Mizutani_Eri", if (lang == "ja") "水谷絵理" else "Eri Mizutani", Triple(0, 173, 185)),
                    IdolColor("Akizuki_Ryo_876", if (lang == "ja") "秋月涼" else "Ryo Akizuki", Triple(178, 212, 104)),
                )
            }
        }

        test("#search(by brand and types): when lang = '$lang' it should return idols") {
            val (repository, _, _) = buildIdolColorsRepository {
                mockIdolSearch(
                    randomQuery(lang),
                    searchQuery(
                        lang,
                        brands = Brands._765,
                        types = setOf(Types.MillionLive.PRINCESS),
                        ja = IdolColorBrandAndTypeJA,
                        en = IdolColorBrandAndTypeEN,
                    ),
                )
            }

            val (instances, _) = flowToList(repository.getIdolColorsStream(10, lang))

            repository.search(
                name = null,
                brands = Brands._765,
                types = setOf(Types.MillionLive.PRINCESS),
                lang = lang,
            )

            instances.let {
                it should haveSize(2)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Amami_Haruka", if (lang == "ja") "天海春香" else "Haruka Amami", Triple(226, 43, 48)),
                    IdolColor("Ganaha_Hibiki", if (lang == "ja") "我那覇響" else "Hibiki Ganaha", Triple(1, 173, 185)),
                    IdolColor("Kikuchi_Makoto", if (lang == "ja") "菊地真" else "Makoto Kikuchi", Triple(81, 85, 88)),
                    IdolColor("Hagiwara_Yukiho", if (lang == "ja") "萩原雪歩" else "Yukiho Hagiwara", Triple(211, 221, 233)),
                )
            }
        }

        test("#search(by live): when lang = '$lang' it should return idols") {
            val liveName = LiveName("ラジオdeアイマSHOW! 公開録音EVENT ～今、田舎村で落ち合おう！TORICO de 2006 Autumn～")
            val (repository, _, _) = buildIdolColorsRepository {
                mockIdolSearch(
                    randomQuery(lang),
                    searchQuery(lang, liveName),
                )
            }

            val (instances, _) = flowToList(repository.getIdolColorsStream(10, lang))

            repository.search(liveName, lang)

            instances.let {
                it should haveSize(2)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Amami_Haruka", if (lang == "ja") "天海春香" else "Haruka Amami", Triple(226, 43, 48)),
                    IdolColor("Kisaragi_Chihaya", if (lang == "ja") "如月千早" else "Chihaya Kisaragi", Triple(39, 67, 210)),
                )
            }
        }

        test("#search(by id): when lang = '$lang' it should return idols") {
            val ids = listOf("Tsukimura_Temari", "Mitsumine_Yuika")
            val (repository, _, _) = buildIdolColorsRepository {
                mockIdolSearch(
                    randomQuery(lang),
                    idsQuery(lang, ids),
                )
            }

            val (instances, _) = flowToList(repository.getIdolColorsStream(10, lang))

            repository.search(ids = ids, lang = lang)

            instances.let {
                it should haveSize(2)
                it.last() should containExactlyInAnyOrder(
                    IdolColor("Tsukimura_Temari", if (lang == "ja") "月村手毬" else "Temari Tsukimura", Triple(79, 160, 206)),
                    IdolColor("Mitsumine_Yuika", if (lang == "ja") "三峰結華" else "Yuika Mitsumine", Triple(59, 145, 196)),
                )
            }
        }
    }

    test("#getInChargeOfIdolIdsStream: when sign in it should return id list") {
        val (repository, auth, _) = buildIdolColorsRepository {
            mockHttpClient()
        }

        val (instances, _) = flowToList(repository.getInChargeOfIdolIdsStream())
        signInWithGoogle(auth)
        repository.registerInChargeOf("Amami_Haruka")
        repository.registerInChargeOf("Kisaragi_Chihaya")
        repository.registerInChargeOf("Hoshii_Miki")

        repository.unregisterInChargeOf("Hoshii_Miki")
        repository.unregisterInChargeOf("Kisaragi_Chihaya")
        repository.unregisterInChargeOf("Amami_Haruka")

        instances.let {
            it should haveSize(7)
            it should containExactly(
                listOf(),
                listOf("Amami_Haruka"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya", "Hoshii_Miki"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka"),
                listOf(),
            )
        }
    }

    test("#getInChargeOfIdolIdsStream: when sign out it should return empty list") {
        val (repository, auth, _) = buildIdolColorsRepository {
            mockHttpClient()
        }

        val (instances, _) = flowToList(repository.getInChargeOfIdolIdsStream())
        auth.signOut()
        repository.registerInChargeOf("Amami_Haruka")
        repository.registerInChargeOf("Kisaragi_Chihaya")
        repository.registerInChargeOf("Hoshii_Miki")

        repository.unregisterInChargeOf("Amami_Haruka")
        repository.unregisterInChargeOf("Kisaragi_Chihaya")
        repository.unregisterInChargeOf("Hoshii_Miki")

        instances.let {
            it should haveSize(1)
            it.last() should beEmpty()
        }
    }

    test("#getFavoriteIdolIdsStream: when sign in it should return id list") {
        val (repository, auth, _) = buildIdolColorsRepository {
            mockHttpClient()
        }

        val (instances, _) = flowToList(repository.getFavoriteIdolIdsStream())
        signInWithGoogle(auth)
        repository.favorite("Amami_Haruka")
        repository.favorite("Kisaragi_Chihaya")
        repository.favorite("Hoshii_Miki")

        repository.unfavorite("Hoshii_Miki")
        repository.unfavorite("Kisaragi_Chihaya")
        repository.unfavorite("Amami_Haruka")

        instances.let {
            it should haveSize(7)
            it should containExactly(
                listOf(),
                listOf("Amami_Haruka"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya", "Hoshii_Miki"),
                listOf("Amami_Haruka", "Kisaragi_Chihaya"),
                listOf("Amami_Haruka"),
                listOf(),
            )
        }
    }

    test("#getFavoriteIdolIdsStream: when sign out it should return empty list") {
        val (repository, auth, _) = buildIdolColorsRepository {
            mockHttpClient()
        }

        val (instances, _) = flowToList(repository.getFavoriteIdolIdsStream())
        auth.signOut()
        repository.favorite("Amami_Haruka")
        repository.favorite("Kisaragi_Chihaya")
        repository.favorite("Hoshii_Miki")

        repository.unfavorite("Amami_Haruka")
        repository.unfavorite("Kisaragi_Chihaya")
        repository.unfavorite("Hoshii_Miki")

        instances.let {
            it should haveSize(1)
            it.last() should beEmpty()
        }
    }
})
