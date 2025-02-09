package cn.jianyun.worktime.util

import cn.jianyun.worktime.util.MyDateTool
import java.util.Date


fun Date.gapDay(gap: Int = 1): Date{
    return MyDateTool.gapDay(this, gap)
}

fun Date.dateStr(): String {
    return MyDateTool.toDateString(this)
}

fun Date.datetimeStr(): String {
    return MyDateTool.toDateTimeString(this)
}

fun String.parseDate(): Date {
    return MyDateTool.parseDate(this)
}

fun String.parseChineseMonth(): Date {
    return MyDateTool.parse(this, MyDateTool.PATTERN_MONTH_CHINESE)
}


