package net.subroh0508.ktor.client.mpp.sample.repository

expect object URLEncoder {
    fun encode(s: String): String
}
