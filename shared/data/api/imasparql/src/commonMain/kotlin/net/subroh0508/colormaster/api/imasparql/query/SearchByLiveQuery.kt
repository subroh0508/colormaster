package net.subroh0508.colormaster.api.imasparql.query

import net.subroh0508.colormaster.api.imasparql.internal.ESCAPED_ENDPOINT_RDFS_DETAIL

class SearchByLiveQuery(
    lang: String,
    liveName: String?,
) : ImasparqlQuery() {
    override val rawQuery = """
        SELECT ?id ?name ?color WHERE {
          ?live rdf:type imas:Live;
            schema:name ?liveName;
            schema:actor ?actor;
            schema:eventStatus ?eventStatus.
          FILTER(?eventStatus != schema:EventCancelled)
          ${liveName?.let { "FILTER (str(?liveName) = '$it')." } ?: ""}
          ?s imas:Color ?color;
            imas:Brand ?brand;
            imas:cv ?actor.
          OPTIONAL { ?s schema:name ?realName. FILTER(lang(?realName) = '$lang') }
          OPTIONAL { ?s schema:alternateName ?altName. FILTER(lang(?altName) = '$lang') }  
          OPTIONAL { ?s schema:givenName ?givenName. FILTER(lang(?givenName) = '$lang') }  
          BIND (COALESCE(?altName, ?realName, ?givenName) as ?name)
          FILTER (str(?brand) != '1stVision').
          BIND (REPLACE(str(?s), '${ESCAPED_ENDPOINT_RDFS_DETAIL}', '') as ?id).
        }
        ORDER BY ?name
    """.trimIndentAndBr()
}
