package net.subroh0508.colormaster.repository.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.should
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.mockk.*
import net.subroh0508.colormaster.api.di.Api
import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories
import net.subroh0508.colormaster.repository.mock.getRandomIdols
import net.subroh0508.colormaster.repository.mock.mockRandom
import net.subroh0508.colormaster.repository.mock.mockSearchById
import net.subroh0508.colormaster.repository.mock.mockSearchByParams
import net.subroh0508.colormaster.test.runTest
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class IdolColorsRepositorySpec : FunSpec() {
    private val mockDatabase: IdolColorsDatabase = mockk {
        coEvery { getFavorites() } returns setOf()
        coEvery { addFavorite(any()) } just Runs
        coEvery { removeFavorite(any()) } just Runs
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
            Api.MockModule(block()) + module {
                single { mockDatabase }
                single { appPreference }
            } + IdolColorsRepositories.Module
        )
    }.koin.get(IdolColorsRepository::class)

    init {
        context("#rand") {
            Languages.values().forEach { lang ->
                context("when lang = '${lang.code}'") {
                    test("it should return idols at random") {
                        val repository = mockApi(lang) { mockRandom(lang, 10) }

                        runTest {
                            val idols = repository.rand(10)

                            idols should containExactlyInAnyOrder(getRandomIdols(lang))
                        }
                    }
                }
            }
        }

        context("#search") {
            Languages.values().forEach { lang ->
                context("when lang = '${lang.code}'") {
                    context("by name") {
                        fun param(lang: Languages) = IdolName(if (lang == Languages.JAPANESE) "久川" else "Hisakawa")
                        fun expects(lang: Languages) = listOf(
                            IdolColor("Hisakawa_Nagi", if (lang == Languages.JAPANESE) "久川凪" else "Nagi Hisakawa", HexColor("F7A1BA")),
                            IdolColor("Hisakawa_Hayate", if (lang == Languages.JAPANESE) "久川颯" else "Hayate Hisakawa", HexColor("7ADAD6")),
                        )

                        test("it should return idols") {
                            val repository = mockApi(lang) {
                                mockSearchByParams(lang, param(lang), res = expects(lang).toTypedArray())
                            }

                            runTest {
                                val idols = repository.search(name = param(lang), brands = null, types = setOf())

                                idols should containExactlyInAnyOrder(expects(lang))
                            }
                        }
                    }

                    context("by brand") {
                        fun expects(lang: Languages) = listOf(
                            IdolColor("Hidaka_Ai", if (lang == Languages.JAPANESE) "日高愛" else "Ai Hidaka", HexColor("E85786")),
                            IdolColor("Mizutani_Eri", if (lang == Languages.JAPANESE) "水谷絵理" else "Eri Mizutani", HexColor("00ADB9")),
                            IdolColor("Akizuki_Ryo_876", if (lang == Languages.JAPANESE) "秋月涼" else "Ryo Akizuki", HexColor("B2D468")),
                        )

                        test("it should return idols") {
                            val repository = mockApi(lang) {
                                mockSearchByParams(lang, brands = Brands._876, res = expects(lang).toTypedArray())
                            }

                            runTest {
                                val idols = repository.search(name = null, brands = Brands._876, types = setOf())

                                idols should containExactlyInAnyOrder(expects(lang))
                            }
                        }
                    }

                    context("by brand and types") {
                        fun expects(lang: Languages) = listOf(
                            IdolColor("Amami_Haruka", if (lang == Languages.JAPANESE) "天海春香" else "Haruka Amami", HexColor("E22B30")),
                            IdolColor("Ganaha_Hibiki", if (lang == Languages.JAPANESE) "我那覇響" else "Ganaha Hibiki", HexColor("01ADB9")),
                            IdolColor("Kikuchi_Makoto", if (lang == Languages.JAPANESE) "菊地真" else "Kikuchi Makoto", HexColor("515558")),
                            IdolColor("Hagiwara_Yukiho", if (lang == Languages.JAPANESE) "萩原雪歩" else "Hagiwara Yukiho", HexColor("D3DDE9")),
                        )

                        test("it should return idols") {
                            val repository = mockApi(lang) {
                                mockSearchByParams(lang, brands = Brands._765, types = setOf(Types.MILLION_LIVE.PRINCESS), res = expects(lang).toTypedArray())
                            }

                            runTest {
                                val idols = repository.search(name = null, brands = Brands._765, types = setOf(Types.MILLION_LIVE.PRINCESS))

                                idols should containExactlyInAnyOrder(expects(lang))
                            }
                        }
                    }

                    context("by id") {
                        fun expects(lang: Languages) = listOf(
                            IdolColor("Mitsumine_Yuika", if (lang == Languages.JAPANESE) "三峰結華" else "Yuika Mitsumine", HexColor("3B91C4")),
                            IdolColor("Hayami_Kanade", if (lang == Languages.JAPANESE) "速水奏" else "Kanade Hayami", HexColor("0D386D")),
                        )

                        test("it should return idols") {
                            val ids = expects(lang).map(IdolColor::id)
                            val repository = mockApi(lang) {
                                mockSearchById(lang, ids = ids, res = expects(lang).toTypedArray())
                            }

                            runTest {
                                val idols = repository.search(ids = ids)

                                idols should containExactlyInAnyOrder(expects(lang))
                            }
                        }
                    }
                }
            }
        }
    }
}
