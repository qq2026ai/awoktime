package cn.jianyun.worktime.module.timework.dto

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import cn.jianyun.worktime.util.ifv
import com.alibaba.fastjson2.JSON


data class TimeworkAppConfigDTO(
    var uuid:String = "",

    var config: String = "",

    var beginDay: String = "monday", //开始日期
    var showLunar: Boolean = true, //显示农历
    var showFestival: Boolean = true, //显示节假日
    var showHour: Boolean = true, //显示工时
    var showMoney: Boolean = true, //显示金额
    var showHLine: Boolean = false, //显示分割线
    var showAward: Boolean = true, //显示补贴

    var hourBg: String = "#2177B8", //工时背景色
    var moneyBg: String = "#1BA784", //金额背景色

    var restBg: String = "#B7AE8F", //休息背景色
    var leaveBg: String = "#495C69", //请假背景色

    var showNotice: Boolean = true, //是否需要打卡通知
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

    var statDay: String = "1", //统计日期

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
        var base = 32
        if(showMoney) {
            base += moneySize
        }
        if(showHour){
            base += hourSize
        }
        if(showMoney && showHour) {
            base += 20
        }
        else if(showMoney || showHour){
            base += 10
        }
        return base.dp
    }
}