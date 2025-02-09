package cn.jianyun.worktime.module.timework.dto.share


data class TimeworkShareData(
    var salarys: List<TimeworkSalaryShareDTO> = listOf(),
    var awardConfigs: List<TimeworkAwardConfigShareDTO> = listOf(),
    var awards: List<TimeworkAwardShareDTO> = listOf(),
    var workTimes: List<TimeworkDataShareDTO> = listOf()
)