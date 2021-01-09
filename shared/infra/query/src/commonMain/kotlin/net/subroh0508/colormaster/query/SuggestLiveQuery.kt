package net.subroh0508.colormaster.query

class SuggestLiveQuery(
    dateRange: Pair<String, String>? = null,
    name: String? = null,
)  : ImasparqlQuery() {
    override val rawQuery = """
        SELECT ?liveName WHERE {
          ?live rdf:type imas:Live;
            schema:name ?liveName;
            schema:startDate ?startDate;
            schema:eventStatus ?eventStatus.
        FILTER (?eventStatus != schema:EventCancelled)
        ${name?.let { "FILTER { regex(?liveName, '.*$it.*', 'i') }" } ?: ""}
        ${dateRange?.let { (start, end) -> "FILTER { xsd:date('$start') <= ?startDate && ?startDate <= xsd:date('$end') }" } ?: ""}
        }
        ORDER BY ?startDate
        LIMIT 5
    """.trimIndentAndBr()
}
