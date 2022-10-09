package net.subroh0508.colormaster.api.imasparql.internal

private external fun encodeURIComponent(s: String): String

internal actual object URLEncoder {
    actual fun encode(s: String): String = encodeURIComponent(s)
}
