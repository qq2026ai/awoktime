package cn.jianyun.worktime.module.timework.dto.share

import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.util.ifv

data class TimeworkAwardConfigShareDTO(
    var id: String = "",
    var uuid: String = "",
    var userUuid: String = "",
    var name: String = "",
    var defaultValue: String = "",
    var award: String = "",
    var gmtCreate: String = "",
    var gmtModify: String = ""
) {
    fun toAward(prefix: String): TimeworkAward {

        return TimeworkAward(
            projectUuid = prefix + "import",
            uuid= prefix + uuid,
            name=name,
            defaultValue = defaultValue,
            type = ifv(award == "1", "award", "fine")
        )

    }
}