package net.subroh0508.colormaster.api.imasparql.query

import net.subroh0508.colormaster.api.imasparql.internal.ESCAPED_ENDPOINT_RDFS_DETAIL

class RandomQuery(lang: String, limit: Int = 10) : ImasparqlQuery() {
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
        }
        ORDER BY rand()
        LIMIT $limit
    """.trimIndentAndBr()
}
