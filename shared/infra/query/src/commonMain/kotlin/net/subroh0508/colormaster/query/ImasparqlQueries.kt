package net.subroh0508.colormaster.query

import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.Titles
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.query.internal.URLEncoder

object ImasparqlQueries {
    fun rand(limit: Int = 10) = buildQuery("""
        SELECT * WHERE {
          ?s rdfs:label ?name;
            imas:Color ?color;
            imas:Title ?title.
          FILTER (str(?title) != '1st Vision').
          BIND (REPLACE(str(?s), '$ESCAPED_ENDPOINT_RDFS_DETAIL', '') as ?id).
        }
        ORDER BY rand()
        LIMIT $limit
    """.trimIndentAndBr())

    fun search(name: IdolName?, titles: Titles?, types: Set<Types>) = buildQuery("""
        SELECT * WHERE {
          ?s rdfs:label ?name;
            imas:Color ?color;
            imas:Title ?title.
          OPTIONAL { ?s imas:Division ?division }
          OPTIONAL { ?s imas:Type ?type }
          OPTIONAL { ?s imas:Category ?category }
          BIND (COALESCE(?category, ?division, ?type) as ?attribute)
          ${name?.value?.let {"FILTER (regex(?name, '.*$it.*', 'i') && str(?title) != '1st Vision')." } ?: ""}
          ${titles?.queryStr?.let { "FILTER (str(?title) = '$it')." } ?: ""}
          ${types.regexStr?.let { "FILTER regex(?attribute, '$it', 'i')." } ?: "" }
          BIND (REPLACE(str(?s), '$ESCAPED_ENDPOINT_RDFS_DETAIL', '') as ?id).
        }
        ORDER BY ?name
    """.trimIndentAndBr())

    fun search(ids: List<String>) = buildQuery("""
        SELECT * WHERE {
          ?s rdfs:label ?name;
            imas:Color ?color;
            imas:Title ?title.
          FILTER (str(?title) != '1st Vision').
          BIND (REPLACE(str(?s), '$ESCAPED_ENDPOINT_RDFS_DETAIL', '') as ?id).  
          ${ids.takeIf(List<String>::isNotEmpty)?.let { "FILTER (regex(?id, '(${it.joinToString("|")})', 'i'))." }}
        }
    """.trimIndentAndBr())

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

    private fun buildQuery(query: String) = buildString {
        append(ENDPOINT_MAIN)
        append("?output=json")
        append("&query=")

        val plainQuery = buildString {
            append(PREFIX_IMAS)
            append(PREFIX_RDFS)

            append(query)
        }

        append(URLEncoder.encode(plainQuery))
    }

    private const val ENDPOINT_MAIN = "/spql/imas/query"
    private const val ESCAPED_ENDPOINT_RDFS_DETAIL = """https://$HOSTNAME/imasrdf/RDFs/detail/"""

    private const val PREFIX_IMAS = "PREFIX imas: <https://sparql.crssnky.xyz/imasrdf/URIs/imas-schema.ttl#>"
    private const val PREFIX_RDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"

    private fun String.trimIndentAndBr() = trimIndent().replace("[\n\r]", "")
}
