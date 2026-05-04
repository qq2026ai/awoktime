package cn.jianyun.worktime.module.timework.dto

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.TimeworkPeriodTool
import com.alibaba.fastjson2.JSON


data class TimeworkAppConfigDTO(
    var uuid:String = "",

    var config: String = "",

    var beginDay: String = "monday", //开始日期
    var showLunar: Boolean = true, //显示农历
    var swipeCalendar: Boolean = false, //显示农历
    var showFestival: Boolean = true, //显示节假日
    var showHour: Boolean = true, //显示工时
    var showMoney: Boolean = true, //显示金额
    var showHLine: Boolean = false, //显示分割线
    var showAward: Boolean = true, //显示补贴
    var showDateTag: Boolean = false, //显示日期标记

    var hourBg: String = "#2177B8", //工时背景色
    var moneyBg: String = "#1BA784", //金额背景色

    var restBg: String = "#B7AE8F", //休息背景色
    var leaveBg: String = "#495C69", //请假背景色

    var showNotice: Boolean = false, //是否需要打卡通知
    var noticeTime: String = "20", //通知时间

    var exportCenter: Boolean = true, //
    var exportRemark: Boolean = true, //

    var showProject: Boolean = false, //

    var noticeDays: String = "",
    var noticeTimes: String = "",

    var smallDevice: Boolean = false,

    var auth: Boolean = false,

    var showVoice: Boolean = false,
    var lines: Int = 2,


    var hourSize: Int = 12, //文字颜色
    var moneySize: Int = 12, //金额字体
    var homeStatSize: Int = 15, //首页顶部统计字号
    var homeStatSingleLine: Boolean = false, //首页顶部统计是否单行显示

    var statDay: String = "1", //考勤周期开始日
    var homeStatFields: String = "baseHour^overHour^awardMoney^totalMoney",

    var projectUuid:String = "", //项目
    var remark: String = "", //描述
    var gmtCreate: String = "", //创建时间

    var needEveryMinute: Boolean = false, //日结时间
    var needDayTime: Boolean = true, //日结时间

){
    fun isAdd():Boolean{
        return true
    }

    fun isValid(): String {
        return "ok"
    }

    fun fetchMinuteStep(): Int {
        return ifv(needEveryMinute, 1, 5)
    }

    fun normalizedStatDay(): String {
        return "${TimeworkPeriodTool.normalizeStatDay(statDay)}"
    }

    fun normalizedHomeStatFields(): String {
        val validValues = linkedSetOf(
            "baseHour",
            "overHour",
            "totalHour",
            "dayHour",
            "awardMoney",
            "pureAwardMoney",
            "fineMoney",
            "dayMoney",
            "dayCount",
            "totalDay",
            "totalMoney",
            "settledMoney",
            "unSettledMoney"
        )
        val selected = homeStatFields
            .split("^")
            .filter { validValues.contains(it) }
            .distinct()
        if(selected.isNotEmpty()){
            return selected.joinToString("^")
        }
        return "baseHour^overHour^awardMoney^totalMoney"
    }

    fun normalizedNoticeDays(): String {
        val validValues = listOf("2", "3", "4", "5", "6", "7", "1")
        val selected = noticeDays.split("^")
        return validValues.filter { selected.contains(it) }.joinToString("^")
    }

    fun noticeTimeList(): List<String> {
        val pattern = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
        return noticeTimes
            .split("^")
            .filter { pattern.matches(it) }
            .distinct()
            .sorted()
            .take(5)
    }

    fun normalizedNoticeTimes(): String {
        return noticeTimeList().joinToString("^")
    }

    fun normalizedHomeStatSize(): Int {
        return homeStatSize.coerceIn(12, 30)
    }

    fun isMondayFirst(): Boolean{
        return "monday" == beginDay
    }

    fun toConfig(): TimeworkAppConfig {
        var one = TimeworkAppConfig()
        one.uuid = "only"
        one.config = JSON.toJSONString(this)
        return one
    }

    fun allSize(): Dp {
        var base = 36
        if(showMoney) {
            base += moneySize
        }
        if(showHour){
            base += hourSize
        }
        if(showMoney && showHour) {
            base += 22
        }
        else if(showMoney || showHour){
            base += 12
        }
        if(showDateTag){
            base += 6
        }
        return base.dp
    }
}
