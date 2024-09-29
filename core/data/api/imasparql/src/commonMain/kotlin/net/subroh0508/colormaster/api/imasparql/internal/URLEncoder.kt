package net.subroh0508.colormaster.api.imasparql.internal

internal expect object URLEncoder {
    fun encode(s: String): String
}
