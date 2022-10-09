package net.subroh0508.colormaster.repository.mock

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import net.subroh0508.colormaster.api.imasparql.query.RandomQuery
import net.subroh0508.colormaster.api.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.api.imasparql.query.SearchByLiveQuery
import net.subroh0508.colormaster.api.imasparql.query.SearchByNameQuery
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
    IdolColor("Shimabara_Elena", if (lang == "ja") "島原エレナ" else "Elena Shimabara", HexColor("9BCE92")),
    IdolColor("Abe_nana", if (lang == "ja") "安部菜々" else "Nana Abe", HexColor("E64A79")),
    IdolColor("Kohinata_Miho", if (lang == "ja") "小日向美穂" else "Miho Kohinata", HexColor("C64796")),
    IdolColor("Sakuma_Mayu", if (lang == "ja") "佐久間まゆ" else "Mayu Sakuma", HexColor("D1197B")),
    IdolColor("Hidaka_Ai", if (lang == "ja") "日高愛" else "Ai Hidaka", HexColor("E85786")),
    IdolColor("Nakatani_Iku", if (lang == "ja") "中谷育" else "Iku Nakatani", HexColor("F7E78E")),
    IdolColor("Taiga_Takeru", if (lang == "ja") "大河タケル" else "Takeru Taiga", HexColor("0E0C9F")),
    IdolColor("Kuzunoha_Amehiko", if (lang == "ja") "葛之葉雨彦" else "Amehiko Kuzunoha", HexColor("111721")),
    IdolColor("Suou_Momoko", if (lang == "ja") "周防桃子" else "Momoko Suou", HexColor("EFB864")),
    IdolColor("Ichikawa_Hinana", if (lang == "ja") "市川雛菜" else "Hinana Ichikawa", HexColor("FFC639")),
)

private fun toJson(lang: String, res: List<IdolColor>) = resultJson {
    res.joinToString(",") { (id, name, hexColor) ->
        jsonIdolColor(lang, id, name, hexColor.value)
    }
}
