package net.subroh0508.colormaster.network.imasparql.query

import net.subroh0508.colormaster.network.imasparql.internal.ESCAPED_ENDPOINT_RDFS_DETAIL

class SearchByIdQuery(
    lang: String,
    ids: List<String>,
) : ImasparqlQuery() {
    override val rawQuery = """
        SELECT ?id ?name ?color WHERE {
          ?s imas:Color ?color;
            imas:Brand ?brand.
          OPTIONAL { ?s schema:name ?realName. FILTER(lang(?realName) = '$lang') }
          OPTIONAL { ?s schema:alternateName ?altName. FILTER(lang(?altName) = '$lang') }  
          OPTIONAL { ?s schema:givenName ?givenName. FILTER(lang(?givenName) = '$lang') }  
          BIND (COALESCE(?altName, ?realName, ?givenName) as ?name)
          FILTER (str(?brand) != '1stVision').
          BIND (REPLACE(str(?s), '${ESCAPED_ENDPOINT_RDFS_DETAIL}', '') as ?id).  
          ${ids.takeIf(List<String>::isNotEmpty)?.let { "FILTER (regex(?id, '(${it.joinToString("|")})', 'i'))." }}
        }
    """.trimIndentAndBr()
}
