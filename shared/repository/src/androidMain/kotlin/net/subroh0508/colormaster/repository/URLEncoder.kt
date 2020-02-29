package net.subroh0508.colormaster.repository

import java.net.URLEncoder

actual object URLEncoder {
    actual fun encode(s: String) = URLEncoder.encode(s, "UTF-8")
}
