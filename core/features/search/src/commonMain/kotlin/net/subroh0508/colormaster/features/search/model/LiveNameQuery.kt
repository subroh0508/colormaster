package net.subroh0508.colormaster.features.search.model

import net.subroh0508.colormaster.components.core.model.DateNum
import net.subroh0508.colormaster.model.LiveName

data class LiveNameQuery(
    val query: String? = null,
    val isSettled: Boolean = false,
) {
    companion object {
        private const val DATE_NUMBER_PATTERN = """^[0-9]+$"""
    }

    fun change(query: String?) = if (isSettled) this else copy(query = query?.takeIf(String::isNotBlank))
    fun settle(value: LiveName) = LiveNameQuery(value.value, true)
    fun clear() = LiveNameQuery()

    fun isNumber() = query?.let { DATE_NUMBER_PATTERN.toRegex().matches(it) } ?: false
    fun toDateNum() = query?.toIntOrNull()?.let(::DateNum)
    fun toLiveName() = takeIf { isSettled }?.query?.let(::LiveName)
}
