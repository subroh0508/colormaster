package net.subroh0508.colormaster.repository

external fun encodeURIComponent(s: String): String

actual object URLEncoder {
    actual fun encode(s: String) = encodeURIComponent(s)
}
