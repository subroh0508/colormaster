package net.subroh0508.colormaster.query.internal

import java.net.URLEncoder

internal actual object URLEncoder {
    actual fun encode(s: String): String = URLEncoder.encode(s, "UTF-8")
}
