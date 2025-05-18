package net.subroh0508.colormaster.backend.cli.imasparql.query

import net.subroh0508.colormaster.backend.cli.imasparql.ESCAPED_ENDPOINT_RDFS_DETAIL

object FetchAllIdolsQuery : ImasparqlQuery() {
    private const val LANG_JA = "ja"
    private const val LANG_EN = "en"

    override val rawQuery = """
        SELECT ?id ?nameJa ?nameKanaJa ?nameEn ?color ?brandName WHERE {
          ?s imas:Color ?color;
            imas:Brand ?brand.

          OPTIONAL { ?s schema:name ?realName. FILTER(lang(?realName) = '$LANG_JA') }
          OPTIONAL { ?s schema:alternateName ?altName. FILTER(lang(?altName) = '$LANG_JA') }  
          OPTIONAL { ?s schema:givenName ?givenName. FILTER(lang(?givenName) = '$LANG_JA') }  
          BIND (COALESCE(?altName, ?realName, ?givenName) as ?nameJa)

          OPTIONAL { ?s imas:nameKana ?realNameKana. FILTER(lang(?realNameKana) = '$LANG_JA') }
          OPTIONAL { ?s imas:alternateNameKana ?altNameKana. FILTER(lang(?altNameKana) = '$LANG_JA') }  
          OPTIONAL { ?s imas:givenNameKana ?givenNameKana. FILTER(lang(?givenNameKana) = '$LANG_JA') }  
          BIND (COALESCE(?altNameKana, ?realNameKana, ?givenNameKana) as ?nameKanaJa)

          OPTIONAL { ?s schema:name ?realNameEn. FILTER(lang(?realNameEn) = '$LANG_EN') }
          OPTIONAL { ?s schema:alternateName ?altNameEn. FILTER(lang(?altNameEn) = '$LANG_EN') }  
          OPTIONAL { ?s schema:givenName ?givenNameEn. FILTER(lang(?givenNameEn) = '$LANG_EN') }  
          BIND (COALESCE(?altNameEn, ?realNameEn, ?givenNameEn) as ?nameEn)

          FILTER (str(?brand) != '1stVision').
          BIND (REPLACE(str(?s), '${ESCAPED_ENDPOINT_RDFS_DETAIL}', '') as ?id).
          BIND (REPLACE(str(?brand), '^.+[#/]([^#/]+)$', '$1') as ?brandName).
        }
    """.trimIndentAndBr()
}
