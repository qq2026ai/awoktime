package cn.jianyun.worktime.model.share

import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.ifv
import java.util.Date

object ShareUtil {

    fun toShareSalary(salary: TimeworkSalary): ShareSalaryDO {
        var target = ShareSalaryDO()
        target.type = salary.type


        //普通薪水
        if(salary.type == "normal"){
            target.valueStr = salary.value
            target.value =  MyDataTool.toDouble(salary.value, 0.0)
        }
        else{
            if(salary.overType == "times"){
                //基数
                target.ref = salary.refSalary
                target.overValueStr = salary.amount
                target.overValue = MyDataTool.toDouble(salary.amount, 0.0)
            }
            else{
                //固定工时费
                target.valueStr = salary.overValue
                target.value = MyDataTool.toDouble(salary.overValue, 0.0)
            }
        }

        target.calcType = ifv(salary.overType == "times", "times", "fixed")
        target.name = salary.name
        target.mark = salary.remark
        target.ordinal = salary.ordinal.toInt()
        target.shown = salary.shown
        target.uuid = salary.uuid
        target.projectUuid = ""
        return target
    }

    fun toShareAwardConfig(config: TimeworkAward): ShareAwardConfigDO{
        var target = ShareAwardConfigDO()
        target.name = config.name
        target.type = config.type
        target.defaultValue = MyDataTool.toDouble(config.defaultValue)
        target.award = config.type == "award"
        target.projectUuid = ""
        target.uuid = config.uuid
        return target
    }

    fun toShareAwardData(config: TimeworkAwardData): ShareAwardDO{
        var target = ShareAwardDO()
        target.awardUuid = config.awardUuid
        target.uuid = config.uuid
        target.value = MyDataTool.toDouble(config.awardValue)
        target.projectUuid = ""
        target.valueStr = config.awardValue
        target.day = MyDateTool.toSwiftTime(MyDateTool.parseDate(config.day)).toFloat()
        target.gmtCreate = MyDateTool.toSwiftTime(MyDateTool.parseDateTimeString(config.gmtCreate)).toFloat()
        return target
    }

    fun toWorkData(config: TimeworkData): ShareWorkTimeDO{
        var target = ShareWorkTimeDO()
        target.uuid = config.uuid
        target.day = MyDateTool.toSwiftTime(MyDateTool.parseDate(config.day)).toFloat()
        target.baseSalaryTime = config.baseSalaryTime
        target.overSalaryTime = config.overSalaryTime
        target.mode = config.mode
        target.mark = config.remark
        target.onlyOver = config.onlyOver
        target.overTime = config.overTime
        target.salaryUuid = config.salaryUuid
        target.overSalaryUuid = config.overSalaryUuid
        target.restTime = config.restTime
        target.beginTime = config.beginTime
        target.endTime = config.endTime
        target.amount = MyDataTool.toDouble(config.amount)
        target.projectUuid = ""
        target.gmtCreate = MyDateTool.toSwiftTime(MyDateTool.parseDateTimeString(config.gmtCreate)).toFloat()
        return target
    }

    fun toAppConfig(config: TimeworkAppConfigDTO): ShareAppConfig{
        return ShareAppConfig(
                beginDay= config.beginDay,
                showLunar =  config.showLunar,
                showFestival = config.showFestival,
                hourBg =  config.hourBg,
                restBg = config. restBg,
                moneyBg =  config.moneyBg,
                showHour =  config.showHour,
                showMoney =  config.showMoney,
                exportCenter = config.exportCenter,
                exportRemark = config.exportRemark,
                showHLine =config.showHLine
            )
    }
}

data class ShareSalaryDO(
    var amount: Double = 0.0,
    var autoName: String = "",
    var mark: String = "",
    var name: String = "",
    var ordinal: Int = 0,
    var overTime: Boolean = true,
    var overType: String = "",
    var overValue: Double = 0.0,
    var overValueStr: String = "",
    var projectUuid: String = "",
    var ref: String = "",
    var shown: Boolean = true,
    var type: String = "normal",
    var calcType: String = "fixed",
    var userUuid: String = "",
    var uuid: String = "",
    var value: Double = 0.0,
    var valueStr: String = "",
    var workType: String = ""
){

    fun toSalaryInfo(pro: String): TimeworkSalary {
        var uu = TimeworkSalary(
            uuid= pro + uuid,
            projectUuid = pro,
            name = name,
            value = value.toString(),
            monthValue = value.toString(),
            shown = shown,
            ordinal = ordinal.toLong(),
            type = type,
            amount = overValue.toString(),
            remark = mark,
            overType = ifv(calcType == "times", "times", "fix"),
            overValue = value.toString(),
            refSalary = pro + ref,
            overValueStr = overValue.toString(),
            workType = workType,
            gmtCreate = MyDateTool.toDateTimeString(Date())
        )

        if(type == "normal"){
            if(valueStr != ""){
                uu.value = valueStr
            }
        }
        else{
            if(calcType == "times"){
                if(overValueStr != ""){
                    uu.amount = overValueStr
                }
            }
            else{

            }
        }
        return uu
    }
}

data class ShareWorkTimeDO(
    var day: Float = 0f,
    var mark: String = "",
    var onlyOver: Boolean = false,
    var overSalaryUuid: String = "",
    var overTime: Boolean = false,
    var projectUuid: String = "",
    var salaryUuid: String = "",
    var uuid: String = "",
    var workUuid: String = "",
    var baseSalaryTime: String = "",
    var overSalaryTime: String = "",
    var amount: Double = 0.0,
    var beginTime: String = "",
    var endTime: String = "",
    var restTime: String = "",
    var mode: String = "",
    var tag: String = "",
    var gmtCreate: Float = 0f
) {
    fun fetchDay(): String{
        return MyDateTool.parseSwiftDate(day)
    }

    fun fetchGmtCreate(): String{
        return MyDateTool.parseSwiftDate(gmtCreate)
    }

    fun getTwo(x: String): String {
        return ifv(x.length < 2, "0${x}" , "${x}")
    }

    fun toData(pro: String): TimeworkData {
        return TimeworkData(
            projectUuid = pro ,
            uuid = pro + uuid,
            day = fetchDay(),
            workUuid = workUuid,
            baseSalaryTime = baseSalaryTime,
            overSalaryTime = overSalaryTime,
            beginTime = beginTime,
            endTime = endTime,
            restTime =  restTime,
            amount = amount.toString(),
            salaryUuid = ifv(salaryUuid != "", pro + salaryUuid, ""),
            overSalaryUuid = ifv(overSalaryUuid != "", pro + overSalaryUuid, ""),
            onlyOver = onlyOver,
            overTime = overTime,
            mode = mode,
            remark = mark,
            gmtCreate = fetchGmtCreate()
        )
    }
}

data class ShareAwardConfigDO(
    var award: Boolean = true,
    var type: String = "",
    var defaultValue: Double = 0.0,
    var ordinal: Int = 0,
    var valueStr: String = "",
    var name: String = "",
    var projectUuid: String = "",
    var shown: Boolean = true,
    var userUuid: String = "",
    var uuid: String = ""
){
    fun toAward(pro: String): TimeworkAward {
        return TimeworkAward(
            projectUuid = pro,
            uuid = pro + uuid,
            name = name,
            defaultValue = defaultValue.toString(),
            type = ifv(award, "award", "fine")
        )
    }
}

data class ShareAwardDO(
    var awardUuid: String = "",
    var day: Float = 0f,
    var mark: String = "",
    var projectUuid: String = "",
    var uuid: String = "",
    var value: Double = 0.0,
    var type: String = "",
    var valueStr: String = "",
    var awardName: String = "",
    var gmtCreate: Float = 0f
) {

    fun fetchDay(): String{
        return MyDateTool.parseSwiftDate(day)
    }

    fun fetchGmtCreate(): String{
        return MyDateTool.parseSwiftDate(gmtCreate)
    }

    fun toAwardData(pro: String): TimeworkAwardData {
        return TimeworkAwardData(
            projectUuid = pro,
            uuid = pro + uuid,
            day = fetchDay(),
            awardValue = value.toString(),
            remark = mark,
            awardUuid = pro + awardUuid,
            gmtCreate = fetchGmtCreate()
        )
    }

}



data class ShareAppConfig(
    val beginDay: String = "monday",
    val showLunar: Boolean = true,
    val showFestival: Boolean = true,
    val showHour: Boolean = true,
    val showMoney: Boolean = false,
    val showHLine: Boolean = false,
    val showAward: Boolean = true,
    val showRemarkTag: Boolean = true,

    val hourBg: String = "#36B269",
    val moneyBg: String = "#EE3F4D",

    val restBg: String = "#525288",
    val leaveBg: String = "#525288",

    val showNotice: Boolean = true,
    val noticeTime: String = "20",

    val exportCenter: Boolean = true,
    val exportRemark: Boolean = true,

    val showProject: Boolean = false,

    val noticeDays: String = "",
    val noticeTimes: String = "",
    val defaultStatType: String = "",

    val smallDevice: Boolean = false,
    val showDecimal: Boolean = true,
    val closeRepeatAlert: Boolean = false,

    val initShowMode: Boolean = false,
    val showNormalTime: Boolean = false,
    val showOverTime: Boolean = false,
    val showTotalTime: Boolean = true,
    val showTotalSalary: Boolean = true,
    val showAwardSalary: Boolean = true,
    val showTotalMoney: Boolean = true,
    val showScrollView: Boolean = false,

    val showHomeSettleSalary: Boolean = false,
    val showSettleSalary: Boolean = false,
    val fontSize: String = "15"
) {

    fun toAppConfig(): TimeworkAppConfigDTO {
        return TimeworkAppConfigDTO(
            uuid = "sys",
            beginDay= beginDay,
            showLunar =  showLunar,
            showFestival =  showFestival,
            hourBg =  hourBg,
            restBg =  restBg,
            moneyBg =  moneyBg,
            showHour =  showHour,
            showMoney =  showMoney,

            exportCenter = exportCenter,
            exportRemark = exportRemark,
            showHLine = showHLine
        )
    }
}


data class ShareSettleData(
    //唯一id
    var uuid: String = "",
    var day: Float = 0f,
    var settle: Boolean = false,
    var gmtSettle: Float = 0f
)

data class BackupData (
    var appConfig: ShareAppConfig = ShareAppConfig(),
    var salarys: List<ShareSalaryDO> = listOf(),
    var workTimes: List<ShareWorkTimeDO> = listOf(),
    var awardConfigs: List<ShareAwardConfigDO> = listOf(),
    var awards: List<ShareAwardDO> =  listOf(),
    var settleDatas: List<ShareSettleData> =  listOf()
)