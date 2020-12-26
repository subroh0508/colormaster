package net.subroh0508.colormaster.query

import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.query.internal.ESCAPED_ENDPOINT_RDFS_DETAIL

class SearchBySomeParamsQuery(
    lang: String,
    name: IdolName?, brands: Brands?, types: Set<Types>,
) : ImasparqlQuery() {
    override val rawQuery = """
        SELECT ?id ?name ?color WHERE {
          ?s imas:Color ?color;
            imas:Title ?title.
          OPTIONAL { ?s schema:name ?realName. FILTER(lang(?realName) = '$lang') }
          OPTIONAL { ?s schema:alternateName ?altName. FILTER(lang(?altName) = '$lang') }  
          BIND (COALESCE(?altName, ?realName) as ?name)
          OPTIONAL { ?s imas:Division ?division }
          OPTIONAL { ?s imas:Type ?type }
          OPTIONAL { ?s imas:Category ?category }
          BIND (COALESCE(?category, ?division, ?type) as ?attribute)
          ${name?.value?.let {"FILTER (regex(?name, '.*$it.*', 'i') && str(?title) != '1st Vision')." } ?: ""}
          ${brands?.queryStr?.let { "FILTER (str(?title) = '$it')." } ?: ""}
          ${types.regexStr?.let { "FILTER regex(?attribute, '$it', 'i')." } ?: "" }
          BIND (REPLACE(str(?s), '${ESCAPED_ENDPOINT_RDFS_DETAIL}', '') as ?id).
        }
        ORDER BY ?name
    """.trimIndentAndBr()

    private val Set<Types>.regexStr get() =
        mapNotNull { it.queryStr }.takeIf(List<String>::isNotEmpty)?.joinToString("|")?.let { "($it)" }

    private val Types.queryStr get() = when (this) {
        Types.CINDERELLA_GIRLS.CU -> "Cu"
        Types.CINDERELLA_GIRLS.CO -> "Co"
        Types.CINDERELLA_GIRLS.PA -> "Pa"
        Types.MILLION_LIVE.PRINCESS -> "Princess"
        Types.MILLION_LIVE.FAIRY -> "Fairy"
        Types.MILLION_LIVE.ANGEL -> "Angel"
        Types.SIDE_M.PHYSICAL -> "フィジカル"
        Types.SIDE_M.INTELLIGENT -> "インテリ"
        Types.SIDE_M.MENTAL -> "メンタル"
        else -> null
    }
}
