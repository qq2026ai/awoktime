package cn.jianyun.worktime.util

import cn.jianyun.worktime.api.FestivalData
import cn.jianyun.worktime.model.MonthDateInfo
import cn.jianyun.worktime.ui.component.model.kt.RangeDate
import java.util.Date

object TimeworkPeriodTool {

    fun normalizeStatDay(statDay: String): Int {
        val day = statDay.toIntOrNull() ?: 1
        return day.coerceIn(1, 28)
    }

    fun isNaturalMonth(statDay: String): Boolean {
        return normalizeStatDay(statDay) == 1
    }

    fun getPeriodRange(anchorDate: Date, statDay: String): RangeDate {
        val normalized = normalizeStatDay(statDay)
        if (normalized == 1) {
            return RangeDate(
                beginDate = MyDateTool.getStartDayStringOfMonth(anchorDate),
                endDate = MyDateTool.getLastDayStringOfMonth(anchorDate)
            )
        }

        val currentDay = MyDateTool.getDay(anchorDate)
        val startDate = if (currentDay >= normalized) {
            MyDateTool.make(
                "${MyDateTool.getYear(anchorDate)}",
                "${MyDateTool.getMonth(anchorDate)}",
                "$normalized"
            )
        } else {
            val previousMonth = MyDateTool.gapMonth(anchorDate, -1)
            MyDateTool.make(
                "${MyDateTool.getYear(previousMonth)}",
                "${MyDateTool.getMonth(previousMonth)}",
                "$normalized"
            )
        }
        val endDate = MyDateTool.gapDay(MyDateTool.gapMonth(startDate, 1), -1)
        return RangeDate(
            beginDate = MyDateTool.toDateString(startDate),
            endDate = MyDateTool.toDateString(endDate)
        )
    }

    fun getPeriodStartDate(anchorDate: Date, statDay: String): Date {
        return getPeriodRange(anchorDate, statDay).beginDate.parseDate()
    }

    fun getMonthPickerBeginDay(statDay: String): String {
        return "${normalizeStatDay(statDay)}"
    }

    fun getPeriodInfo(
        beginDate: Date,
        endDate: Date,
        mondayFirst: Boolean,
        holidayMap: Map<String, FestivalData>
    ): List<List<MonthDateInfo>> {
        val all = mutableListOf<List<MonthDateInfo>>()
        val flat = mutableListOf<MonthDateInfo>()
        repeat(MyDateTool.getFirstDayPosition(beginDate, mondayFirst)) {
            flat.add(MonthDateInfo())
        }

        val today = MyDateTool.toDateString(Date())
        var cursor = beginDate
        while (cursor.time <= endDate.time) {
            val dateStr = MyDateTool.toDateString(cursor)
            val holidayInfo = holidayMap[dateStr]
            val lunarDay = if (holidayInfo != null) {
                holidayInfo.name
            } else {
                try {
                    LunarCalendar.getLunarDay(cursor)
                } catch (_: Exception) {
                    ""
                }
            }

            flat.add(
                MonthDateInfo(
                    date = cursor,
                    day = "${MyDateTool.getDay(cursor)}",
                    lunarDay = lunarDay,
                    today = today == dateStr,
                    empty = false,
                    holiday = holidayInfo?.holiday ?: false,
                    work = holidayInfo?.let { !it.holiday } ?: false
                )
            )
            cursor = MyDateTool.gapDay(cursor, 1)
        }

        var group = mutableListOf<MonthDateInfo>()
        flat.forEachIndexed { index, item ->
            group.add(item)
            if ((index + 1) % 7 == 0) {
                all.add(group)
                group = mutableListOf()
            }
        }
        if (group.isNotEmpty()) {
            repeat(7 - group.size) {
                group.add(MonthDateInfo())
            }
            all.add(group)
        }
        return all
    }
}
