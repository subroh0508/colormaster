package net.subroh0508.ktor.client.mpp.sample.repository

external fun encodeURIComponent(s: String): String

actual object URLEncoder {
    actual fun encode(s: String) = encodeURIComponent(s)
}
