package net.subroh0508.colormaster.api.imasparql.query

class SuggestLiveQuery(
    dateRange: Pair<String, String>? = null,
    name: String? = null,
)  : ImasparqlQuery() {
    override val rawQuery = """
        SELECT ?name WHERE {
          ?live rdf:type imas:Live;
            schema:name ?name;
            schema:startDate ?startDate;
            schema:eventStatus ?eventStatus.
        FILTER (?eventStatus != schema:EventCancelled)
        ${name?.let { "FILTER (regex(?name, '.*$it.*', 'i'))" } ?: ""}
        ${dateRange?.let { (start, end) -> "FILTER (xsd:date('$start') <= ?startDate && ?startDate <= xsd:date('$end'))" } ?: ""}
        }
        ORDER BY ?startDate
        LIMIT 5
    """.trimIndentAndBr()
}
