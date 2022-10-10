package net.subroh0508.colormaster.components.core.model

import kotlin.jvm.JvmInline
import kotlin.math.log10

@JvmInline
value class DateNum(private val value: Int) {
    fun validate(): Boolean {
        val year = year().takeIf { it != INVALID } ?: return false
        val month = month(year).takeIf { it != INVALID } ?: return false

        return date(year, month) != INVALID
    }

    fun range(): Pair<String, String>? {
        if (!validate()) {
            return null
        }

        val year = year()
        val month = month(year).takeIf { it != NONE }
            ?: return "$year-01-01" to "$year-12-31"
        val paddingMonth = month.toString().padStart(2, '0')

        val date = date(year, month).takeIf { it != NONE }
            ?: return "$year-$paddingMonth-01" to "$year-$paddingMonth-${endOfMonth(year, month)}"
        val paddingDate = date.toString().padStart(2, '0')

        return "$year-$paddingMonth-$paddingDate" to "$year-$paddingMonth-$paddingDate"
    }

    private fun year(): Int {
        val year = value.toFloat().let {
            when {
                it <= 0.0F -> return INVALID
                it.isDigitYear -> value
                it.isDigitYearMonth -> value / 100
                it.isDigitYearMonthDate -> value / 10000
                else -> return INVALID
            }
        }

        return if (IMAS_START_YEAR <= year) year else INVALID
    }

    private fun month(year: Int): Int {
        val month = value.toFloat().let {
            when {
                it <= 0.0F -> return NONE
                it.isDigitYear -> return NONE
                it.isDigitYearMonth -> value - (year * 100)
                it.isDigitYearMonthDate -> (value - (year * 10000)) / 100
                else -> return INVALID
            }
        }

        return if (month in 1..12) month else INVALID
    }

    private fun date(year: Int, month: Int): Int {
        val date = value.toFloat().let {
            when {
                it <= 0.0F -> return NONE
                it.isDigitYear -> return NONE
                it.isDigitYearMonth -> return NONE
                it.isDigitYearMonthDate -> value - (year * 10000) - (month * 100)
                else -> return INVALID
            }
        }

        return if (date in 1..endOfMonth(year, month)) date else INVALID
    }

    private fun endOfMonth(year: Int, month: Int) = when {
        month == 2 && !isLeap(year) -> 28
        month == 2 && isLeap(year) -> 29
        listOf(4, 6, 9, 11).contains(month) -> 30
        else -> 31
    }

    private fun isLeap(year: Int) = (year % 400 == 0) || (year % 100 != 0 && year % 4 == 0)
}

private const val INVALID = -1
private const val NONE = 0

private const val IMAS_START_YEAR = 2005

private val Float.isDigitYear get() = log10(this).let { DIGIT_YEAR_MIN <= it && it < DIGIT_YEAR_MAX }
private val Float.isDigitYearMonth get() = log10(this).let { DIGIT_YEAR_MONTH_MIN  <= it && it < DIGIT_YEAR_MONTH_MAX }
private val Float.isDigitYearMonthDate get() = log10(this).let { DIGIT_YEAR_MONTH_DATE_MIN  <= it && it < DIGIT_YEAR_MONTH_DATE_MAX }

private const val DIGIT_YEAR_MIN = 3.0F
private const val DIGIT_YEAR_MAX = 4.0F
private const val DIGIT_YEAR_MONTH_MIN = 5.0F
private const val DIGIT_YEAR_MONTH_MAX = 6.0F
private const val DIGIT_YEAR_MONTH_DATE_MIN = 7.0F
private const val DIGIT_YEAR_MONTH_DATE_MAX = 8.0F
