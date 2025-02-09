package cn.jianyun.worktime.module.timework.dto.share

import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.util.ifv

data class TimeworkDataShareDTO(
    var id: String = "",
    var uuid: String = "",
    var userUuid: String = "",
    var day: String = "",
    var workUuid: String = "",
    var hour: String = "",
    var minute: String = "",
    var value: String = "",
    var salaryUuid: String = "",
    var overtime: String = "",
    var overType: String = "",
    var gmtStart: String = "",
    var gmtEnd: String = "",
    var amount: String = "",
    var mark: String = "",
    var baseDuration: String = "",
    var overDuration: String = "",
    var sumDuration: String = "",
    var baseSalary: String = "",
    var overSalary: String = "",
    var sumSalary: String = "",
    var overHour: String = "",
    var overMinute: String = "",
    var overSalaryUuid: String = "",
    var onlyOver: String = "",
    var gmtCreate: String = "",
    var gmtModify: String = ""
) {

    fun getTwo(x: String): String {
       return ifv(x.length < 2, "0${x}" , "${x}")
    }

    fun toData(prefix: String): TimeworkData {
        return TimeworkData(
            projectUuid = prefix + "import",
            uuid = prefix + uuid,
            day = day,
            workUuid = workUuid,
            baseSalaryTime = getTwo(hour) + ":" + getTwo(minute),
            overSalaryTime = getTwo(overHour) + ":" + getTwo(overMinute),
            salaryUuid = ifv(salaryUuid != "", prefix + salaryUuid, ""),
            overSalaryUuid = ifv(overSalaryUuid != "", prefix + overSalaryUuid, ""),
            onlyOver = onlyOver == "1",
            overTime = overtime == "1",
            mode = "hour",
            remark = mark
        )
    }
}