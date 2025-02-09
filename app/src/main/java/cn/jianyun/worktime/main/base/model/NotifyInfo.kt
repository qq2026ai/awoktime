package cn.jianyun.worktime.main.base.model

import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.MyEncryptTool
import cn.jianyun.worktime.util.mlog

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
        var t = System.currentTimeMillis() - 100
        if (notifyTime.length == "2021-01-21".length) {
            notifyTime = "$notifyTime 12:15:00"
        }
        try{
            if (notifyTime.length == "2021-01-21 10:00:22".length || notifyTime.length == "2021-01-21 8:00:22".length || notifyTime.length == "2021-01-21 8:0:22".length) {
                t = MyDateTool.parseDateTimeString(notifyTime).time
            } else if (notifyTime.length == "2021-01-21 10:00".length) {
                t = MyDateTool.parseShortDateTimeString(notifyTime).time
            }
        }
        catch (e: Exception){
            mlog("other time", notifyTime)
        }

        return t
    }

    fun uuid(): String {
        return bizName + getRealTime()
    }
}