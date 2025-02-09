package cn.jianyun.worktime.module.timework.dto.share

import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData

data class TimeworkAwardShareDTO(
    var id: String = "",
    var uuid: String = "",
    var userUuid: String = "",
    var awardUuid: String = "",
    var value: String = "",
    var day: String = "",
    var mark: String = "",
    var gmtCreate: String = "",
    var gmtModify: String = ""
){

    fun toAwardData(prefix: String): TimeworkAwardData {
        return TimeworkAwardData(
            projectUuid = prefix + "import",
            uuid = prefix + uuid,
            day = day,
            awardValue = value,
            remark = mark,
            awardUuid = prefix + awardUuid,
            gmtCreate = gmtCreate
        )
    }
}