package cn.jianyun.worktime.module.timework.dto.share

import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.toIntData
import cn.jianyun.worktime.util.toLongData


data class TimeworkSalaryShareDTO(
    var id: String = "",
    var uuid: String = "",
    var userUuid: String = "",
    var name: String = "",
    var value: String = "",
    var shown: String = "",
    var ordinal: String = "",
    var type: String = "",
    var amount: String = "",
    var mark: String = "",
    var multiple: String = "",
    var overtype: String = "",
    var overtime: String = "",
    var ref: String = "",
    var overvalue: String = "",
    var autoName: String = "",
    var workType: String = "",
    var gmtCreate: String = "",
    var gmtModify: String = ""
) {


    fun toSalaryInfo(prefix: String): TimeworkSalary {


        return TimeworkSalary(
            uuid= prefix + uuid,
            projectUuid = prefix + "import",
            name = name,
            value = value,
            monthValue = value,
            shown = shown == "1",
            ordinal = ordinal.toLongData(),
            type = ifv(overtime == "0", "normal", "over"),
            calcType = ifv(type == "fixed", "fix", "month"),
            amount = overvalue,
            remark = mark,
            overType = ifv(overtype == "multiple", "times", "fix"),
            overValue = overvalue,
            refSalary = prefix + ref,
            overValueStr = overvalue,
            workType = workType,
            gmtCreate = gmtCreate
        )
    }


}