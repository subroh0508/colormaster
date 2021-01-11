package net.subroh0508.colormaster.repository.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.should
import io.ktor.client.*
import net.subroh0508.colormaster.api.di.Api
import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories
import net.subroh0508.colormaster.repository.mock.*
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class IdolColorsRepositorySpec : FunSpec() {
    private val mockDatabase: IdolColorsDatabase = object : IdolColorsDatabase {
        override suspend fun getFavorites() = setOf<String>()
        override suspend fun addFavorite(id: String) = Unit
        override suspend fun removeFavorite(id: String) = Unit
    }

    private fun mockApi(
        languages: Languages,
        block: () -> HttpClient,
    ): IdolColorsRepository = koinApplication {
        val appPreference: AppPreference = object : AppPreference {
            override val lang = languages
            override fun setLanguage(lang: Languages) = Unit
        }

        modules(
            Api.Module(block()) + module {
                single { mockDatabase }
                single { appPreference }
            } + IdolColorsRepositories.Module
        )
    }.koin.get(IdolColorsRepository::class)

    init {
        Languages.values().forEach { lang ->
            test("#rand: when lang = '${lang.code}' it should return idols at random") {
                val repository = mockApi(lang) { mockRandom(lang, 10) }

                repository.rand(10) should containExactlyInAnyOrder(getRandomIdols(lang))
            }

            fun param(lang: Languages) = IdolName(if (lang == Languages.JAPANESE) "久川" else "Hisakawa")
            fun expectsByName(lang: Languages) = listOf(
                IdolColor("Hisakawa_Nagi", if (lang == Languages.JAPANESE) "久川凪" else "Nagi Hisakawa", HexColor("F7A1BA")),
                IdolColor("Hisakawa_Hayate", if (lang == Languages.JAPANESE) "久川颯" else "Hayate Hisakawa", HexColor("7ADAD6")),
            )

            test("#search(by name): when lang = '${lang.code}' it should return idols") {
                val repository = mockApi(lang) {
                    mockSearchByName(lang, param(lang), res = expectsByName(lang).toTypedArray())
                }

                repository.search(
                    name = param(lang),
                    brands = null,
                    types = setOf(),
                ) should containExactlyInAnyOrder(expectsByName(lang))
            }

            fun expectsByBrand(lang: Languages) = listOf(
                IdolColor("Hidaka_Ai", if (lang == Languages.JAPANESE) "日高愛" else "Ai Hidaka", HexColor("E85786")),
                IdolColor("Mizutani_Eri", if (lang == Languages.JAPANESE) "水谷絵理" else "Eri Mizutani", HexColor("00ADB9")),
                IdolColor("Akizuki_Ryo_876", if (lang == Languages.JAPANESE) "秋月涼" else "Ryo Akizuki", HexColor("B2D468")),
            )

            test("#search(by brand): when lang = '${lang.code}' it should return idols") {
                val repository = mockApi(lang) {
                    mockSearchByName(lang, brands = Brands._876, res = expectsByBrand(lang).toTypedArray())
                }

                repository.search(
                    name = null,
                    brands = Brands._876,
                    types = setOf(),
                ) should containExactlyInAnyOrder(expectsByBrand(lang))
            }

            fun expectsByBrandAndTypes(lang: Languages) = listOf(
                IdolColor("Amami_Haruka", if (lang == Languages.JAPANESE) "天海春香" else "Haruka Amami", HexColor("E22B30")),
                IdolColor("Ganaha_Hibiki", if (lang == Languages.JAPANESE) "我那覇響" else "Ganaha Hibiki", HexColor("01ADB9")),
                IdolColor("Kikuchi_Makoto", if (lang == Languages.JAPANESE) "菊地真" else "Kikuchi Makoto", HexColor("515558")),
                IdolColor("Hagiwara_Yukiho", if (lang == Languages.JAPANESE) "萩原雪歩" else "Hagiwara Yukiho", HexColor("D3DDE9")),
            )

            test("#search(by brand and types): when lang = '${lang.code}' it should return idols") {
                val repository = mockApi(lang) {
                    mockSearchByName(lang, brands = Brands._765, types = setOf(Types.MILLION_LIVE.PRINCESS), res = expectsByBrandAndTypes(lang).toTypedArray())
                }

                repository.search(
                    name = null,
                    brands = Brands._765,
                    types = setOf(Types.MILLION_LIVE.PRINCESS),
                ) should containExactlyInAnyOrder(expectsByBrandAndTypes(lang))
            }

            fun expectsByLive(lang: Languages) = listOf(
                IdolColor("Sakuragi_Mano", if (lang == Languages.JAPANESE) "櫻木真乃" else "Mano Sakuragi", HexColor("FFBAD6")),
                IdolColor("Kazano_Hiori", if (lang == Languages.JAPANESE) "風野灯織" else "Hirori Kazano", HexColor("144384")),
                IdolColor("Hachimiya_Meguru", if (lang == Languages.JAPANESE) "八宮めぐる" else "Meguru Hachimiya", HexColor("FFE012")),
            )

            test("#search(by live): when lang = '${lang.code}' it should return idols") {
                val live = LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1")
                val repository = mockApi(lang) {
                    mockSearchByLive(lang, LiveName("THE IDOLM@STER SHINY COLORS MUSIC DAWN Day1"), res = expectsByLive(lang).toTypedArray())
                }

                repository.search(live) should containExactlyInAnyOrder(expectsByLive(lang))
            }

            fun expectsById(lang: Languages) = listOf(
                IdolColor("Mitsumine_Yuika", if (lang == Languages.JAPANESE) "三峰結華" else "Yuika Mitsumine", HexColor("3B91C4")),
                IdolColor("Hayami_Kanade", if (lang == Languages.JAPANESE) "速水奏" else "Kanade Hayami", HexColor("0D386D")),
            )

            test("#search(by id): when lang = '${lang.code}' it should return idols") {
                val ids = expectsById(lang).map(IdolColor::id)
                val repository = mockApi(lang) {
                    mockSearchById(lang, ids = ids, res = expectsById(lang).toTypedArray())
                }

                repository.search(ids = ids) should containExactlyInAnyOrder(expectsById(lang))
            }
        }
    }
}
