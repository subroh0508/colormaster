package net.subroh0508.colormaster.repository.mock

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import net.subroh0508.colormaster.network.imasparql.query.RandomQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByLiveQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByNameQuery
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.test.jsonIdolColor
import net.subroh0508.colormaster.test.resultJson
import net.subroh0508.colormaster.test.mockApi

fun mockRandom(lang: String, limit: Int) = mockApi { req ->
    if (req.url.parameters["query"] == RandomQuery(lang, limit).plainQuery) {
        return@mockApi respond(toJson(lang, getRandomIdols(lang)), headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSearchByName(
    lang: String, name: IdolName? = null, brands: Brands? = null, types: Set<Types> = setOf(),
    vararg res: IdolColor,
) = mockApi { req ->
    if (req.url.parameters["query"] == SearchByNameQuery(lang, name?.value, brands?.queryStr, types.map(Types::queryStr)).plainQuery) {
        return@mockApi respond(toJson(lang, res.toList()), headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSearchByLive(
    lang: String, liveName: LiveName?,
    vararg res: IdolColor,
) = mockApi { req ->
    if (req.url.parameters["query"] == SearchByLiveQuery(lang, liveName?.value).plainQuery) {
        return@mockApi respond(toJson(lang, res.toList()), headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSearchById(
    lang: String, ids: List<String>,
    vararg res: IdolColor,
) = mockApi { req ->
    if (req.url.parameters["query"] == SearchByIdQuery(lang, ids).plainQuery) {
        return@mockApi respond(toJson(lang, res.toList()), headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun getRandomIdols(lang: String) = listOf(
    IdolColor("Shimabara_Elena", if (lang == "ja") "島原エレナ" else "Elena Shimabara", Triple(155, 206, 146)),
    IdolColor("Abe_nana", if (lang == "ja") "安部菜々" else "Nana Abe", Triple(230, 74, 121)),
    IdolColor("Kohinata_Miho", if (lang == "ja") "小日向美穂" else "Miho Kohinata", Triple(198, 71, 150)),
    IdolColor("Sakuma_Mayu", if (lang == "ja") "佐久間まゆ" else "Mayu Sakuma", Triple(209, 25, 123)),
    IdolColor("Hidaka_Ai", if (lang == "ja") "日高愛" else "Ai Hidaka", Triple(232, 87, 134)),
    IdolColor("Nakatani_Iku", if (lang == "ja") "中谷育" else "Iku Nakatani", Triple(247, 231, 142)),
    IdolColor("Taiga_Takeru", if (lang == "ja") "大河タケル" else "Takeru Taiga", Triple(14, 12, 159)),
    IdolColor("Kuzunoha_Amehiko", if (lang == "ja") "葛之葉雨彦" else "Amehiko Kuzunoha", Triple(17, 23, 33)),
    IdolColor("Suou_Momoko", if (lang == "ja") "周防桃子" else "Momoko Suou", Triple(239, 184, 100)),
    IdolColor("Ichikawa_Hinana", if (lang == "ja") "市川雛菜" else "Hinana Ichikawa", Triple(255, 198, 57)),
)

private fun toJson(lang: String, res: List<IdolColor>) = resultJson {
    res.joinToString(",") {
        jsonIdolColor(lang, it.id, it.name, it.color.replace("#", ""))
    }
}
