package net.subroh0508.colormaster.repository.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.be
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.maps.haveSize
import io.kotest.matchers.should
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.mockk.*
import net.subroh0508.colormaster.api.di.Api
import net.subroh0508.colormaster.api.internal.ContentType
import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.query.RandomQuery
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.di.IdolColorsRepositories
import net.subroh0508.colormaster.repository.json.rand
import net.subroh0508.colormaster.test.runTest
import org.koin.core.context.startKoin
import org.koin.dsl.module

class IdolColorsRepositorySpec : FunSpec() {
    private val mockApi = HttpClient(MockEngine) {
        engine {
            addHandler { req ->
                val headers = headersOf(
                    "Content-Type" to listOf(ContentType.Application.SparqlJson.toString())
                )

                when (req.url.parameters["query"]) {
                    RandomQuery("ja").plainQuery -> respond(rand, headers = headers)
                    else -> respondBadRequest()
                }
            }
        }
    }

    private val mockDatabase: IdolColorsDatabase = mockk {
        coEvery { getFavorites() } returns setOf()
        coEvery { addFavorite(any()) } just Runs
        coEvery { removeFavorite(any()) } just Runs
    }
    private val mockAppPreference: AppPreference = object : AppPreference {
        override val lang = Languages.JAPANESE

        override fun setLanguage(lang: Languages) = Unit
    }

    private val repository: IdolColorsRepository = startKoin {
        modules(
            Api.MockModule(mockApi) + module {
                single { mockDatabase }
                single { mockAppPreference }
            } + IdolColorsRepositories.Module
        )
    }.koin.get(IdolColorsRepository::class)

    init {
        context("#rand") {
            test("when limit 10 it should return idols at random") {
                runTest {
                    val idols = repository.rand(10)

                    idols.size should be(1)
                }
            }

            test("hoge") {
                runTest {
                    val idols = repository.rand(10)

                    idols.size should be(2)
                }
            }
        }
    }
}
