package net.subroh0508.colormaster.backend.cli.imasparql

import java.net.URLEncoder

object URLEncoder {
    fun encode(s: String): String = URLEncoder.encode(s, "UTF-8")
}
