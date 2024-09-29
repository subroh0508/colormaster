package net.subroh0508.colormaster.model

import kotlin.jvm.JvmInline

@JvmInline
value class IdolName(val value: String)
@JvmInline
value class LiveName(val value: String)
@JvmInline
value class UnitName(val value: String)
@JvmInline
value class SongName(val value: String)

fun String?.toIdolName() = this?.takeIf(String::isNotBlank)?.let(::IdolName)
fun String?.toLiveName() = this?.takeIf(String::isNotBlank)?.let(::LiveName)
