package net.subroh0508.colormaster.api.imasparql.query

import net.subroh0508.colormaster.api.imasparql.internal.ESCAPED_ENDPOINT_RDFS_DETAIL

class SearchByNameQuery(
    lang: String,
    idolName: String?, brandsQueryStr: String?, typesQueryStr: List<String>,
) : ImasparqlQuery() {
    override val rawQuery = """
        SELECT ?id ?name ?color WHERE {
          ?s imas:Color ?color;
            imas:Brand ?brand.
          OPTIONAL { ?s schema:name ?realName. FILTER(lang(?realName) = '$lang') }
          OPTIONAL { ?s schema:alternateName ?altName. FILTER(lang(?altName) = '$lang') }  
          OPTIONAL { ?s schema:givenName ?givenName. FILTER(lang(?givenName) = '$lang') }  
          BIND (COALESCE(?altName, ?realName, ?givenName) as ?name)
          OPTIONAL { ?s imas:Division ?division }
          OPTIONAL { ?s imas:Type ?type }
          OPTIONAL { ?s imas:Category ?category }
          BIND (COALESCE(?category, ?division, ?type) as ?attribute)
          ${idolName?.let {"FILTER (regex(?name, '.*$it.*', 'i') && str(?brand) != '1stVision')." } ?: ""}
          ${brandsQueryStr?.let { "FILTER (str(?brand) = '$it')." } ?: ""}
          ${typesQueryStr.regex?.let { "FILTER regex(?attribute, '$it', 'i')." } ?: "" }
          BIND (REPLACE(str(?s), '${ESCAPED_ENDPOINT_RDFS_DETAIL}', '') as ?id).
        }
        ORDER BY ?name
    """.trimIndentAndBr()

    private val List<String>.regex get() =
        takeIf(List<String>::isNotEmpty)?.joinToString("|")?.let { "($it)" }
}
