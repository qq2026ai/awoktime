package cn.jianyun.worktime.module.timework.model

import cn.jianyun.worktime.module.base.dto.BaseBackupData
import cn.jianyun.worktime.util.MyEncryptTool
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.toJSONString

data class TimeworkBackupData(
    var projectList:List<TimeworkProject> = listOf(),
    var datalist: List<TimeworkData> = listOf(),
    var salaryList: List<TimeworkSalary> = listOf(),
    var awardList: List<TimeworkAward> = listOf(),
    var awardDataList: List<TimeworkAwardData> = listOf(),
    var defaultConfigList: List<TimeworkDefaultConfig> = listOf(),
    var appConfig: TimeworkAppConfig = TimeworkAppConfig()
): BaseBackupData() {

    fun encryptData(): String {
        return MyEncryptTool.encrypt(this.toJSONString())
    }

    companion object {
        fun parse(data: Any): TimeworkBackupData {
            val originData = MyEncryptTool.decrypt(data as String)
            return JSON.parseObject(originData, TimeworkBackupData::class.java)
        }
    }
}