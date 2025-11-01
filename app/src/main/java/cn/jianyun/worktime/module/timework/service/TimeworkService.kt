package cn.jianyun.worktime.module.timework.service

import cn.jianyun.worktime.main.base.model.NotifyInfo
import cn.jianyun.worktime.main.base.service.BaseService
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.CloudFile
import cn.jianyun.worktime.module.base.dto.BaseBackupData
import cn.jianyun.worktime.module.timework.dao.TimeworkAppConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDefaultConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkProjectDao
import cn.jianyun.worktime.module.timework.dao.TimeworkSalaryDao
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.dto.share.TimeworkShareData
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkBackupData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig
import cn.jianyun.worktime.module.timework.model.TimeworkProject
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.util.CalendarReminderUtils
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.MyRandomTool
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.toMultiData
import cn.jianyun.worktime.util.uuid
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeworkService @Inject constructor(
    val baseRepository: BaseRepository,
    val dataDao: TimeworkDataDao,
    val salaryDao: TimeworkSalaryDao,
    val awardDao: TimeworkAwardDao,
    val defaultConfigDao: TimeworkDefaultConfigDao,
    val awardDataDao: TimeworkAwardDataDao,
    val appConfigDao: TimeworkAppConfigDao,
    val projectDao: TimeworkProjectDao
): BaseService {
    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun getBizName(): String {
        return "Timework"
    }

    override suspend fun notifyWidget() {
        TODO("Not yet implemented")
    }

    override suspend fun makeNotify() {

        try{
            val config = appConfigDao.get()

            //校验权限

            CalendarReminderUtils.cleanCalendarEvent(baseRepository.context, getNotifyBizName())
            if(!config.showNotice || config.noticeDays == "" || config.noticeTimes == ""){
                //清空
                return
            }
            //写入最近7天的通知

            var notifyInfos = mutableListOf<NotifyInfo>()

            var now = Date()
            for(i in 0..7){
                val day = MyDateTool.gapDay(now, i)
                val weekday = MyDateTool.getWeekday(day)
                if(config.noticeDays.toMultiData().contains("$weekday")){
                    //在指定时间内进行通知
                    config.noticeTimes.toMultiData().forEach{
                        val notifyTime = MyDateTool.toDateString(day) + " " + it + ":00:00"
                        notifyInfos.add(NotifyInfo(title="记工时啦", description = "再忙也不要忘记打卡工时喔", notifyTime=notifyTime))
                    }
                }
            }
            //先清空旧的，然后创建新的
            notifyInfos.forEach{
                CalendarReminderUtils.addCalendarEvent(baseRepository.context, it.copy(bizName = getNotifyBizName()))
            }
        }
        catch (e: Exception ){

        }
    }

    override suspend fun isEmpty(): Boolean {
        return dataDao.list().isEmpty()
    }




    override suspend fun prepareBackupData(): CloudFile {
        //加载全部数据
        val datalist = dataDao.list()
        val salaryList = salaryDao.list()
        val awardList = awardDao.list()
        val awardDataList = awardDataDao.list()
        val defaultConfigList = defaultConfigDao.list()
        val appConfig = appConfigDao.get()

        val data = TimeworkBackupData(datalist, salaryList, awardList, awardDataList, defaultConfigList, appConfig.toConfig())
        val cloudFile = CloudFile(content = data.encryptData())
        val count = datalist.size
        val size = cloudFile.fileSize()
        val title = "${count}_${size}_${System.currentTimeMillis()}.worktime.${getBizName().lowercase()}"
        return cloudFile.copy(name=title)
    }

    override suspend fun clearAll():CloudFile{
        //删除前，先备份
        val currentBackupData = prepareBackupData()
        dataDao.clearAll()
        defaultConfigDao.clearAll()
        appConfigDao.clearAll()
        awardDataDao.clearAll()
        salaryDao.clearAll()
        awardDao.clearAll()
        return currentBackupData
    }

    override suspend fun writeAll(backupData: BaseBackupData) {
        val data = backupData as TimeworkBackupData
        //批量写入
        data.salaryList.forEach {
            salaryDao.insert(it)
        }
        data.datalist.forEach {
            dataDao.insert(it)
        }
        data.awardList.forEach {
            awardDao.insert(it)
        }
        data.defaultConfigList.forEach {
            defaultConfigDao.insert(it)
        }
        data.awardDataList.forEach {
            awardDataDao.insert(it)
        }
        appConfigDao.set(data.appConfig)
    }

    suspend fun tryInitProject() {
        val k = projectDao.list()
        if(k.isEmpty()){
            //创建
            val project = TimeworkProject(uuid= "default", name = "默认项目")
            projectDao.insert(project)
            setProjectId("default")
        }
        else{
            val currentProjetUuid = getProjectId()
            if(k.find{it.uuid == currentProjetUuid} == null){
                setProjectId(k[0].uuid)
            }
        }
    }

    suspend fun listSalaryByProject(projectId: String): List<TimeworkSalary>{
        return salaryDao.listByProject(projectId)
    }

    suspend fun listAwardByProject(projectId: String): List<TimeworkAward>{
        return awardDao.listByProject(projectId)
    }
    suspend fun listAwardDataByProject(projectId: String): List<TimeworkAwardData>{
        return awardDataDao.listByProject(projectId)
    }
    suspend fun listDefaultConfigByProject(projectId: String): List<TimeworkDefaultConfig>{
        return defaultConfigDao.listByProject(projectId)
    }
    suspend fun listDataByProject(projectId: String): List<TimeworkData>{
        return dataDao.listByProject(projectId)
    }

    suspend fun getAppConfig(): TimeworkAppConfigDTO{
        return appConfigDao.get()
    }

    suspend fun listProject(): List<TimeworkProject> {
        return projectDao.list()
    }

    suspend fun makeWechatImport(data: TimeworkShareData) {

        var rr = MyRandomTool.random(4);
        var newProject = TimeworkProject(uuid="${rr}import", name = "小程序导入")
        projectDao.insert(newProject)
        var salarys = data.salarys.map{it.toSalaryInfo(rr)}

        salarys.forEach {
            salaryDao.insert(it.copy(showValue = it.makeShowValue(salarys)))
        }

        var awardTypes = mutableMapOf<String, String>()
        data.awardConfigs.map{it.toAward(rr)}.forEach {
            awardTypes.put(it.uuid, it.type)
            awardDao.insert(it)
        }

        data.awards.map { it.toAwardData(rr) }.forEach {
            it.awardType = awardTypes[it.uuid] ?: "award"
            awardDataDao.insert(it)
        }

        data.workTimes.map{it.toData(rr)}.forEach {
            dataDao.insert(it)
            mlog("add one", it)
        }

        setProjectId(newProject.uuid)


    }

}

