package cn.jianyun.worktime.main.base.model

import cn.jianyun.worktime.util.mlog
import java.text.SimpleDateFormat
import java.util.Locale

data class NotifyInfo(

    //业务名
    var bizName: String = "",
    //通知标题
    var title: String = "",
    //通知描述
    var description: String = "",
    //通知时间
    var notifyTime: String = "",
    //是否提醒（有声音的提醒）
    var alarm: Boolean = false,
    //提前多久提醒
    var beforeMinute: Int = 0
){

    fun getRealTime(): Long {
        val value = notifyTime.trim()
        try{
            if (value.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
                return parseStrictTime("$value 12:15:00", "yyyy-MM-dd HH:mm:ss")
            }
            if (value.matches(Regex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$"))) {
                return parseStrictTime(value, "yyyy-MM-dd HH:mm:ss")
            }
            if (value.matches(Regex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$"))) {
                return parseStrictTime(value, "yyyy-MM-dd HH:mm")
            }
        }
        catch (e: Exception){
            mlog("other time", notifyTime)
        }

        return System.currentTimeMillis() - 100
    }

    fun uuid(): String {
        return bizName + getRealTime()
    }

    private fun parseStrictTime(value: String, pattern: String): Long {
        val formatter = SimpleDateFormat(pattern, Locale.CHINA)
        formatter.isLenient = false
        return formatter.parse(value)?.time ?: throw IllegalArgumentException("invalid notify time")
    }
}
