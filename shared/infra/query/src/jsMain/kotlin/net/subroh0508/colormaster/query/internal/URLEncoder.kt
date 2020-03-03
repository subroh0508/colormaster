package net.subroh0508.colormaster.query.internal

internal external fun encodeURIComponent(s: String): String

internal actual object URLEncoder {
    actual fun encode(s: String): String = encodeURIComponent(s)
}
