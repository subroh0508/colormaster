package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import io.ktor.client.*
import net.subroh0508.colormaster.data.*
import net.subroh0508.colormaster.network.imasparql.di.Api
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.data.IdolColorsRepository
import net.subroh0508.colormaster.data.di.IdolColorsRepositories
import net.subroh0508.colormaster.data.mock.*
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.test.extension.flowToList
import net.subroh0508.colormaster.test.fake.FakeAuthClient
import net.subroh0508.colormaster.test.fake.FakeFirestoreClient
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class IdolColorsRepositorySpec : FunSpec({
    listOf("ja", "en").forEach { lang ->
        test("#idols: when lang = '$lang' it should return idols at random") {
            val repository = mockApi {
                mockRandom(
                    lang,
                    10,
                    if (lang == "ja") IdolColorRandomJA else IdolColorRandomEN,
                )
            }

            val (instances, _) = flowToList(repository.idols(10, lang))

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

            val repository = mockApi {
                mockSearchByName(
                    lang,
                    10,
                    params,
                    res = if (lang == "ja") IdolColorNameJA else IdolColorNameEN,
                )
            }

            val (instances, _) = flowToList(repository.idols(10, lang))

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
            val repository = mockApi {
                mockSearchByName(
                    lang,
                    10,
                    brands = Brands._876,
                    res = if (lang == "ja") IdolColorBrandJA else IdolColorBrandEN,
                )
            }

            val (instances, _) = flowToList(repository.idols(10, lang))

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
            val repository = mockApi {
                mockSearchByName(
                    lang,
                    10,
                    brands = Brands._765,
                    types = setOf(Types.MillionLive.PRINCESS),
                    res = if (lang == "ja") IdolColorBrandAndTypeJA else IdolColorBrandAndTypeEN,
                )
            }

            val (instances, _) = flowToList(repository.idols(10, lang))

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
            val repository = mockApi {
                mockSearchByLive(
                    lang,
                    10,
                    liveName,
                    res = if (lang == "ja") IdolColorLiveNameJA else IdolColorLiveNameEN,
                )
            }

            val (instances, _) = flowToList(repository.idols(10, lang))

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
            val repository = mockApi {
                mockSearchById(
                    lang,
                    10,
                    ids = ids,
                    res = if (lang == "ja") IdolColorIdsJA else IdolColorIdsEN,
                )
            }

            val (instances, _) = flowToList(repository.idols(10, lang))

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
})

private fun mockApi(
    block: () -> HttpClient,
): IdolColorsRepository = koinApplication {
    modules(
        Api.Module(block()) + module {
            single<AuthClient> { FakeAuthClient() }
            single<FirestoreClient> { FakeFirestoreClient() }
        } + IdolColorsRepositories.Module
    )
}.koin.get(IdolColorsRepository::class)
