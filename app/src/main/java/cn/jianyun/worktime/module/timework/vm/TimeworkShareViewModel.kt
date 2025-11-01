package cn.jianyun.worktime.module.timework.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.trace
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.api.ApiResult
import cn.jianyun.worktime.api.TraceApi
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.share.BackupData
import cn.jianyun.worktime.model.share.ShareSalaryDO
import cn.jianyun.worktime.model.share.ShareUtil
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.model.TimeworkProject
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.MyRandomTool
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.withApi
import com.alibaba.fastjson2.JSON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class TimeworkShareViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val timeworkService: TimeworkService,
    val traceApi: TraceApi
) : BaseViewModel() {


    var curPage by mutableStateOf("share")
    var shared by mutableStateOf(false)
    var shareId by mutableStateOf("")
    var importSecret by mutableStateOf("")

    var samples by mutableStateOf("na1vh1\n" +
            "fqsufx\n" +
            "bhhd4f\n" +
            "k3bs0n\n" +
            "o6qals\n" +
            "l9v3wu\n" +
            "cwzbze\n" +
            "85izo7\n" +
            "82jgab\n" +
            "jurwa7\n" +
            "4nextj\n" +
            "i8k29o\n" +
            "68qcj9\n" +
            "h2wua3\n" +
            "xpsgkn\n" +
            "nljkb5\n" +
            "ah4qcp\n" +
            "geyi18\n" +
            "l4usb2\n" +
            "9n0djp\n" +
            "bxbmvh\n" +
            "4jvb0i\n" +
            "y3ubyf\n" +
            "y3rykt\n" +
            "yuxyz3\n" +
            "mkgvfg\n" +
            "quuo8b\n" +
            "j0ajxl\n" +
            "twmc11\n" +
            "ah07qu")

    override fun getRepository(): BaseRepository {
       return baseRepository
    }

    override fun reload() {

    }

    override fun reloadData(dataChanged: Boolean) {

    }

    fun doShare() {


        var that = this

        viewModelScope.launch {

            baseRepository.loading(true)

            val pro = timeworkService.getProjectId()
            val salarys = timeworkService.listSalaryByProject(pro)
            val awardConfigs = timeworkService.listAwardByProject(pro)
            val awardDatas = timeworkService.listAwardDataByProject(pro)
            val workDatas = timeworkService.listDataByProject(pro)

            val awardUuids = awardConfigs.filter{it.type == "award"}.map{it.uuid}

            val backupData = BackupData()
            backupData.salarys = salarys.map{ ShareUtil.toShareSalary(it).copy(amount = it.fetchRealHourSalary(salarys).toDouble()) }
            backupData.awardConfigs = awardConfigs.map { ShareUtil.toShareAwardConfig(it) }
            backupData.awards = awardDatas.map { ShareUtil.toShareAwardData(it).copy(type = ifv(awardUuids.contains(it.awardUuid), "award", "fine")) }
            backupData.workTimes = workDatas.map { ShareUtil.toWorkData(it) }
            backupData.appConfig = ShareUtil.toAppConfig(timeworkService.getAppConfig())



            var shareData = ShareDataDO()

            shareId = MyRandomTool.random(6)

            shareData.uuid = MyRandomTool.uuids()
            shareData.shareId = shareId
            shareData.content = JSON.toJSONString(backupData)
            shareData.userUuid = baseRepository.getUid()
            shareData.gmtCreate = MyDateTool.toDateTimeString(Date())
            shareData.name = "安卓记工时"
            shareData.appId = "jgs"
            shareData.appCode = "ajgs"
            shareData.remark =   "${backupData.workTimes.size}条工时记录"
            val shareRst = withApi {
                traceApi.share(shareData)
            }
            if(shareRst.success){
                shared = true
                baseRepository.toast("分享成功")
            }
            else{
                baseRepository.toast("分享失败，请保持有网络或者联系开发者")
            }
        }
    }

    fun doImport(navHostController: NavHostController) {

        val shareId = this.importSecret
        if(shareId == "" ) {
            toast("秘钥不能为空")
            return
        }
        if(shareId.length != 6){
            toast("秘钥错误")
            return
        }
        viewModelScope.launch {
            baseRepository.loading(true)
            val rst:ApiResult<ShareDataDO> = withApi {
                traceApi.fetch(shareId)
            }
            if(rst.success){
                val shareData:ShareDataDO = rst.result!!
                val backupData = JSON.parseObject(shareData.content, BackupData::class.java)

                //创建一个新的项目
                var rr = MyRandomTool.random(8);
                var newProject = TimeworkProject(uuid="${rr}", name = "导入工时")
                timeworkService.projectDao.insert(newProject)
                var salarys = backupData.salarys.map{it.toSalaryInfo(rr)}

                salarys.forEach {
                    timeworkService.salaryDao.insert(it.copy(showValue = it.makeShowValue(salarys)))
                }
                var awardTypes = mutableMapOf<String, String>()
                backupData.awardConfigs.map{it.toAward(rr)}.forEach {
                    awardTypes.put(it.uuid, it.type)
                    timeworkService.awardDao.insert(it)
                }
                backupData.awards.map { it.toAwardData(rr) }.forEach {
                    it.awardType = awardTypes[it.uuid] ?: "award"
                    timeworkService.awardDataDao.insert(it)
                }
                backupData.workTimes.map{it.toData(rr)}.forEach {
                    timeworkService.dataDao.insert(it)
                    mlog("add one", it)
                }

                var config = backupData.appConfig.toAppConfig()
                config.showMoney = true
                config.showHour = true
                timeworkService.appConfigDao.set(config.toConfig())
                timeworkService.setProjectId(newProject.uuid)
                baseRepository.page = "home"
                baseRepository.toast("导入成功")
                goBack(navHostController)
            } else {
                toast("请确认秘钥正确，或者试试重新导入")
            }
        }

    }
}


class ShareDataDO {

    /**
     * 唯一编号
     */
    var uuid: String = ""

    /**
     * 应用id
     */
    var appId: String = ""

    /**
     * 应用标识
     */
    var appCode: String = ""

    /**
     * 分享标识
     */
    var shareId: String = ""

    /**
     * 压缩
     */
    var compress: Boolean = false

    /**
     * 名称
     */
    var name: String = ""

    /**
     * 备注
     */
    var remark: String = ""

    /**
     * 内容
     */
    var content: String = ""

    /**
     * 分享用户
     */
    var userUuid: String = ""

    /**
     * 审核是否通过
     */
    var checked: Boolean = false

    /**
     * 是否公共
     */
    var common: Boolean = false

    /**
     * 分组id
     */
    var groupUuid: String = ""

    /**
     * 分组id
     */
    var gmtCreate: String = ""


}
