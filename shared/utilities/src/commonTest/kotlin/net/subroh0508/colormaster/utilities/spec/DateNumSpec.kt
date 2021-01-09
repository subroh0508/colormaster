package net.subroh0508.colormaster.utilities.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.row
import io.kotest.matchers.be
import io.kotest.matchers.should
import net.subroh0508.colormaster.utilities.DateNum

class DateNumSpec : FunSpec({
    listOf(
        row(-1, false, null),
        row(0, false, null),
        row(100, false, null),
        row(2000, false, null),
        row(2005, true, "2005-01-01" to "2005-12-31"),
        row(2020, true, "2020-01-01" to "2020-12-31"),
        row(20151, false, null),
        row(201500, false, null),
        row(201501, true, "2015-01-01" to "2015-01-31"),
        row(201504, true, "2015-04-01" to "2015-04-30"),
        row(201502, true, "2015-02-01" to "2015-02-28"),
        row(201202, true, "2012-02-01" to "2012-02-29"),
        row(201213, false, null),
        row(2017051, false, null),
        row(20170500, false, null),
        row(20170501, true, "2017-05-01" to "2017-05-01"),
        row(20170630, true, "2017-06-30" to "2017-06-30"),
        row(20170931, false, null),
        row(20170229, false, null)
    ).forEach { (num, expectValidate, expectRange) ->
        test("#validate: when number = $num it should return $expectValidate") {
            DateNum(num).validate() should be(expectValidate)
        }

        test("#range: when number = $num it should return $expectRange") {
            DateNum(num).range() should be(expectRange)
        }
    }
})
