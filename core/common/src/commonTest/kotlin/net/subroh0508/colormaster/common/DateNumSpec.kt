package net.subroh0508.colormaster.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.should
import net.subroh0508.colormaster.common.model.DateNum

data class Row(val num: Int, val expectRange: Pair<String, String>?) {
    val expectValidate get() = expectRange != null
}

class DateNumSpec : FunSpec({
    listOf(
        Row(-1, null),
        Row(0, null),
        Row(100, null),
        Row(2000, null),
        Row(2005, "2005-01-01" to "2005-12-31"),
        Row(2020, "2020-01-01" to "2020-12-31"),
        Row(20151, null),
        Row(201500, null),
        Row(201501, "2015-01-01" to "2015-01-31"),
        Row(201504, "2015-04-01" to "2015-04-30"),
        Row(201502, "2015-02-01" to "2015-02-28"),
        Row(201202, "2012-02-01" to "2012-02-29"),
        Row(201213, null),
        Row(2017051, null),
        Row(20170500, null),
        Row(20170501, "2017-05-01" to "2017-05-01"),
        Row(20170630, "2017-06-30" to "2017-06-30"),
        Row(20170931, null),
        Row(20170229, null),
    ).forEach { r ->
        test("#validate: when number = ${r.num} it should return ${r.expectValidate}") {
            DateNum(r.num).validate() should be(r.expectValidate)
        }

        test("#range: when number = ${r.num} it should return ${r.expectRange} ") {
            DateNum(r.num).range() should be(r.expectRange)
        }
    }
})
