package net.subroh0508.colormaster.repository.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.should
import io.ktor.client.*
import net.subroh0508.colormaster.api.imasparql.di.Api
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories
import net.subroh0508.colormaster.repository.mock.*
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class IdolColorsRepositorySpec : FunSpec() {
    private fun mockApi(
        block: () -> HttpClient,
    ): IdolColorsRepository = koinApplication {
        modules(
            Api.Module(block()) + module {
                single { mockAuthenticationClient }
                single { mockFirestoreClient }
            } + IdolColorsRepositories.Module
        )
    }.koin.get(IdolColorsRepository::class)

    init {
        listOf("ja", "en").forEach { lang ->
            test("#rand: when lang = '$lang' it should return idols at random") {
                val repository = mockApi { mockRandom(lang, 10) }

                repository.rand(10, lang) should containExactlyInAnyOrder(getRandomIdols(lang))
            }

            fun param(lang: String) = IdolName(if (lang == "ja") "久川" else "Hisakawa")
            fun expectsByName(lang: String) = listOf(
                IdolColor("Hisakawa_Nagi", if (lang == "ja") "久川凪" else "Nagi Hisakawa", HexColor("F7A1BA")),
                IdolColor("Hisakawa_Hayate", if (lang == "ja") "久川颯" else "Hayate Hisakawa", HexColor("7ADAD6")),
            )

            test("#search(by name): when lang = '$lang' it should return idols") {
                val repository = mockApi {
                    mockSearchByName(lang, param(lang), res = expectsByName(lang).toTypedArray())
                }

                repository.search(
                    name = param(lang),
                    brands = null,
                    types = setOf(),
                    lang = lang,
                ) should containExactlyInAnyOrder(expectsByName(lang))
            }

            fun expectsByBrand(lang: String) = listOf(
                IdolColor("Hidaka_Ai", if (lang == "ja") "日高愛" else "Ai Hidaka", HexColor("E85786")),
                IdolColor("Mizutani_Eri", if (lang == "ja") "水谷絵理" else "Eri Mizutani", HexColor("00ADB9")),
                IdolColor("Akizuki_Ryo_876", if (lang == "ja") "秋月涼" else "Ryo Akizuki", HexColor("B2D468")),
            )

            test("#search(by brand): when lang = '$lang' it should return idols") {
                val repository = mockApi {
                    mockSearchByName(lang, brands = Brands._876, res = expectsByBrand(lang).toTypedArray())
                }

                repository.search(
                    name = null,
                    brands = Brands._876,
                    types = setOf(),
                    lang = lang,
                ) should containExactlyInAnyOrder(expectsByBrand(lang))
            }

            fun expectsByBrandAndTypes(lang: String) = listOf(
                IdolColor("Amami_Haruka", if (lang == "ja") "天海春香" else "Haruka Amami", HexColor("E22B30")),
                IdolColor("Ganaha_Hibiki", if (lang == "ja") "我那覇響" else "Ganaha Hibiki", HexColor("01ADB9")),
                IdolColor("Kikuchi_Makoto", if (lang == "ja") "菊地真" else "Kikuchi Makoto", HexColor("515558")),
                IdolColor("Hagiwara_Yukiho", if (lang == "ja") "萩原雪歩" else "Hagiwara Yukiho", HexColor("D3DDE9")),
            )

            test("#search(by brand and types): when lang = '$lang' it should return idols") {
                val repository = mockApi {
                    mockSearchByName(lang, brands = Brands._765, types = setOf(Types.MillionLive.PRINCESS), res = expectsByBrandAndTypes(lang).toTypedArray())
                }

                repository.search(
                    name = null,
                    brands = Brands._765,
                    types = setOf(Types.MillionLive.PRINCESS),
                    lang = lang,
                ) should containExactlyInAnyOrder(expectsByBrandAndTypes(lang))
            }

            fun expectsByLive(lang: String) = listOf(
                IdolColor("Sakuragi_Mano", if (lang == "ja") "櫻木真乃" else "Mano Sakuragi", HexColor("FFBAD6")),
                IdolColor("Kazano_Hiori", if (lang == "ja") "風野灯織" else "Hirori Kazano", HexColor("144384")),
                IdolColor("Hachimiya_Meguru", if (lang == "ja") "八宮めぐる" else "Meguru Hachimiya", HexColor("FFE012")),
            )

            test("#search(by live): when lang = '$lang' it should return idols") {
                val live = LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1")
                val repository = mockApi {
                    mockSearchByLive(lang, LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"), res = expectsByLive(lang).toTypedArray())
                }

                repository.search(live, lang) should containExactlyInAnyOrder(expectsByLive(lang))
            }

            fun expectsById(lang: String) = listOf(
                IdolColor("Mitsumine_Yuika", if (lang == "ja") "三峰結華" else "Yuika Mitsumine", HexColor("3B91C4")),
                IdolColor("Hayami_Kanade", if (lang == "ja") "速水奏" else "Kanade Hayami", HexColor("0D386D")),
            )

            test("#search(by id): when lang = '$lang' it should return idols") {
                val ids = expectsById(lang).map(IdolColor::id)
                val repository = mockApi {
                    mockSearchById(lang, ids = ids, res = expectsById(lang).toTypedArray())
                }

                repository.search(ids = ids, lang = lang) should containExactlyInAnyOrder(expectsById(lang))
            }
        }
    }
}
