package net.subroh0508.ktor.client.mpp.sample.repository

import java.net.URLEncoder

actual object URLEncoder {
    actual fun encode(s: String) = URLEncoder.encode(s, "UTF-8")
}
