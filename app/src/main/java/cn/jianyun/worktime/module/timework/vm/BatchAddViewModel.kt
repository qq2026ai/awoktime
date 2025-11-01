package cn.jianyun.worktime.module.timework.vm


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.api.FestivalData
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.dao.TimeworkAppConfigDao
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.dto.TimeworkShownData
import cn.jianyun.worktime.module.timework.dto.TimeworkStatData
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig
import cn.jianyun.worktime.module.timework.model.TimeworkProject
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.uuid
import com.alibaba.fastjson2.JSON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class BatchAddViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    private val timeworkService: TimeworkService,
    val appConfigDao: TimeworkAppConfigDao
) : BaseViewModel() {

    var defaultConfigs by mutableStateOf(listOf<TimeworkDefaultConfig>())
    var salarys by mutableStateOf(listOf<TimeworkSalary>())
    var awards by mutableStateOf(listOf<TimeworkAward>())
    var awardDatas by mutableStateOf(listOf<TimeworkAwardData>())
    var workDatas by mutableStateOf(listOf<TimeworkData>())

    var pageType by mutableStateOf(FormType(type="home"))
    var deleteType by mutableStateOf(FormType(type=""))
    var currentProjectName by mutableStateOf("")

    var datalist by mutableStateOf(listOf<TimeworkData>())
    var projects by mutableStateOf(listOf<TimeworkProject>())
    var editWorkItem by mutableStateOf(TimeworkData())
    var editAwardItem by mutableStateOf(TimeworkAwardData())
    var homeEdit by mutableStateOf(false)

    var currentDate by mutableStateOf(Date())

    var shownDataMap by mutableStateOf(mapOf<String, TimeworkShownData>())
    var workDataMap by mutableStateOf(mapOf<String, List<TimeworkData>>())
    var awardDataMap by mutableStateOf(mapOf<String, List<TimeworkAwardData>>())

    var monthStatModel by mutableStateOf(TimeworkStatData())

    var appConfig by mutableStateOf(TimeworkAppConfigDTO())

    var holidayMap by mutableStateOf(mapOf<String, FestivalData>())

    fun getCurrentDateStr(): String {
        return MyDateTool.toDateString(currentDate)
    }


    init {
        reload()
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload(){
        viewModelScope.launch {
            baseRepository.loading(true)
            mlog("loading finish...")
            oldSid = baseRepository.sid
            timeworkService.tryInitProject()
            projects = timeworkService.projectDao.list()
            currentProjectId = timeworkService.getProjectId()
            currentProjectName = projects.find { it.uuid == currentProjectId}?.name ?: "我的工时"
            defaultConfigs = timeworkService.listDefaultConfigByProject(currentProjectId)
            salarys = timeworkService.listSalaryByProject(currentProjectId)
            awards = timeworkService.listAwardByProject(currentProjectId)
            appConfig = appConfigDao.get()
            reloadData(false)
            baseRepository.finish()
        }
    }

    fun checkSalarys(uuid: String): List<SelectDO>{
        if(salarys.find{it.uuid == uuid}?.shown == false) {
            return salarys.map{it.toSelect()}
        }
        return salarys.filter{it.shown}.map{it.toSelect()}
    }

    override fun reloadData(dataChanged: Boolean){
        //列出本月的数据

        val beginDay = MyDateTool.getStartDayStringOfMonth(currentDate)
        val endDay = MyDateTool.getLastDayStringOfMonth(currentDate)

        var salaryMap = mutableMapOf<String, Float>()
        var salaryInfoMap = mutableMapOf<String, String>()
        salarys.forEach{
            salaryMap.put(it.uuid, it.fetchRealHourSalary(salarys))
            salaryInfoMap.put(it.uuid, it.name + "(" + it.showValue + ")")
        }

        var monthStatResult = TimeworkStatData()

        var tempWorkMap = mutableMapOf<String, List<TimeworkData>>()
        var tempAwardMap = mutableMapOf<String, List<TimeworkAwardData>>()
        viewModelScope.launch {
            baseRepository.loading()
            withContext(Dispatchers.IO) {
                try{
                    holidayMap = baseRepository.fetchHoliday(MyDateTool.getYear(currentDate))
                }
                catch(e:Exception){
                    holidayMap = mapOf()
                }
            }
            awardDatas = timeworkService.awardDataDao.listByPeriod(currentProjectId, beginDay, endDay)
            workDatas = timeworkService.dataDao.listByPeriod(currentProjectId, beginDay, endDay)

            mlog("workDatas", workDatas)
            workDatas.forEach { mlog(it) }
            var result = mutableMapOf<String, TimeworkShownData>()
            //基于每一天计算工时数据
            var t = beginDay
            while(t <= endDay){
                var item = TimeworkShownData(
                    hourColor = appConfig.hourBg.color(),
                    moneyColor = appConfig.moneyBg.color(),
                    restColor = appConfig.restBg.color(),
                    showHour = appConfig.showHour,
                    showMoney = appConfig.showMoney,
                    leaveColor = appConfig.leaveBg.color(),
                    hourSize = appConfig.hourSize,
                    moneySize = appConfig.moneySize
                )
                workDatas.filter{it.day == t}.forEach{

                    var newItem = it

                    if(it.mode == "hour"){
                        item.hour = MyDataTool.plusTime(item.hour, it.fetchTotalHour())
                        if(!it.onlyOver){

                            newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                            newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f
                            item.money = MyDataTool.plusPriceWithString(item.money, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))

                            monthStatResult.baseHour = MyDataTool.plusTime(monthStatResult.baseHour, it.fetchBaseHour())
                            monthStatResult.baseSalary = MyDataTool.plusPriceWithString(monthStatResult.baseSalary, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))
                        }
                        if(it.overTime){

                            newItem.overSalaryInfo = salaryInfoMap.get(it.overSalaryUuid) ?: ""
                            newItem.overSalaryPrice = salaryMap.get(it.overSalaryUuid) ?: 0f
                            item.money = MyDataTool.plusPriceWithString(item.money, it.fetchOverMoney(salaryMap.get(it.overSalaryUuid) ?: 0f))

                            monthStatResult.overHour = MyDataTool.plusTime(monthStatResult.overHour, it.fetchOverHour())
                            monthStatResult.overSalary = MyDataTool.plusPriceWithString(monthStatResult.overSalary, it.fetchOverMoney(salaryMap.get(it.overSalaryUuid) ?: 0f))

                        }
                    }
                    if(it.mode == "time" && it.endTime != "") {


                        item.hour = MyDataTool.plusTime(item.hour, it.fetchBaseHour())
                        newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                        newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f
                        item.money = MyDataTool.plusPriceWithString(item.money, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))

                        monthStatResult.baseHour = MyDataTool.plusTime(monthStatResult.baseHour, it.fetchBaseHour())
                        monthStatResult.baseSalary = MyDataTool.plusPriceWithString(monthStatResult.baseSalary, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))

                    }
                    if(it.mode == "day"){
                        item.dayMoney = MyDataTool.plusPriceWithString(item.dayMoney, it.amount)
                        monthStatResult.dayCount = MyDataTool.plusNum(monthStatResult.dayCount, "1").toString()
                        monthStatResult.dayMoney = MyDataTool.plusPriceWithString(monthStatResult.dayMoney, it.amount)
                        monthStatResult.dayHour = MyDataTool.plusTime(monthStatResult.dayHour, it.fetchBaseHour())
                    }

                    if(it.mode == "leave"){
                        item.leave = true
                    }
                    if(it.mode == "rest"){
                        item.rest = true
                    }

                    var old = tempWorkMap.get(t)
                    if(old != null){
                        var s = old.toMutableList()
                        s.add(newItem)
                        tempWorkMap[t] = s
                    }
                    else{
                        tempWorkMap[t] = listOf(newItem)
                    }
                }

                awardDatas.filter{it.day == t}.forEach {

                    if(it.uuid.isEmpty()){
                        timeworkService.awardDataDao.delete(it)
                    }

                    var newItem = it
                    newItem.awardName = awards.find{it.uuid == newItem.awardUuid}?.name ?: ""
                    if(it.awardType == "award"){
                        monthStatResult.awardMoney = MyDataTool.plusPriceWithString(monthStatResult.awardMoney, newItem.awardValue)
                        item.awardValue = MyDataTool.plusPriceWithString(item.awardValue, it.awardValue)
                    }
                    else{
                        monthStatResult.awardMoney = MyDataTool.minusPriceWithString(monthStatResult.awardMoney, newItem.awardValue)
                        item.fineValue = MyDataTool.plusPriceWithString(item.fineValue, it.awardValue)
                    }

                    var old = tempAwardMap.get(t)
                    if(old != null){
                        var s = old.toMutableList()
                        s.add(newItem)
                        tempAwardMap[t] = s
                    }
                    else{
                        tempAwardMap[t] = listOf(newItem)
                    }
                }
                result[t] = item
                t = MyDateTool.nextDay(t);
            }
            shownDataMap = result
            workDataMap = tempWorkMap
            awardDataMap = tempAwardMap
            monthStatModel = monthStatResult

            if(dataChanged){
                baseRepository.reload()
            }
            baseRepository.finish()
        }
    }

    fun isHomePage():Boolean{
        return pageType.type == "home"
    }
    fun isSalaryPage():Boolean{
        return pageType.type == "salary"
    }
    fun isAwardPage():Boolean{
        return pageType.type == "award"
    }
    fun isStatPage():Boolean{
        return pageType.type == "stat"
    }
    fun isSettingPage():Boolean{
        return pageType.type == "setting"
    }

    fun finish(){
        homeEdit = false
    }

    fun makeDefaultSign(defaultConfigUuid: String) {
        var config = defaultConfigs.find{it.uuid == defaultConfigUuid}!!
        if(config.type == "sign"){
            makeDefaultHour(config.config)
        }
        else{
            makeDefaultAward(config.config)
        }
    }

    private fun makeDefaultHour(config: String) {
        var workItem = JSON.parseObject(config, TimeworkData::class.java)

        var todays = workDataMap[getCurrentDateStr()]
        //判断今天是否已经有此配置
        if(todays != null && todays.filter {
                it.mode == workItem.mode &&
                        it.baseSalaryTime == workItem.baseSalaryTime &&
                        it.salaryUuid == workItem.salaryUuid &&
                        it.overSalaryTime == workItem.overSalaryTime &&
                        it.overSalaryUuid == workItem.overSalaryUuid &&
                        it.amount == workItem.amount &&
                        it.beginTime == workItem.beginTime &&
                        it.endTime == workItem.endTime
            }.isNotEmpty()){
            deleteType = FormType("sameSign", workItem)
        }
        else{
            viewModelScope.launch {
                workItem.uuid = uuid()
                workItem.projectUuid = currentProjectId
                workItem.day = getCurrentDateStr()
                timeworkService.dataDao.insert(workItem)
                baseRepository.playTap()
                baseRepository.reload()
            }
        }
    }

    fun makeSign2(){
        val workItem = deleteType.editItem as TimeworkData
        viewModelScope.launch {
            workItem.uuid = uuid()
            workItem.day = getCurrentDateStr()
            workItem.projectUuid = currentProjectId
            timeworkService.dataDao.insert(workItem)
            baseRepository.playTap()
            baseRepository.reload()
            deleteType = FormType()
        }
    }

    private fun makeDefaultAward(config: String) {
        var awardItem = JSON.parseObject(config, TimeworkAwardData::class.java)
        var todays = awardDataMap[getCurrentDateStr()]
        //判断今天是否已经有此配置
        if(todays != null && todays.filter {
                it.awardUuid == awardItem.awardUuid
            }.isNotEmpty()){
            deleteType = FormType("sameAward", awardItem)
        }
        else{
            viewModelScope.launch {
                awardItem.uuid = uuid()
                awardItem.projectUuid = currentProjectId
                awardItem.day = getCurrentDateStr()
                timeworkService.awardDataDao.insert(awardItem)
                baseRepository.playTap()
                baseRepository.reload()
            }
        }
    }

    fun makeAward2(){
        val awardItem = deleteType.editItem as TimeworkAwardData
        viewModelScope.launch {
            awardItem.uuid = uuid()
            awardItem.projectUuid = currentProjectId
            awardItem.day = getCurrentDateStr()
            timeworkService.awardDataDao.insert(awardItem)
            baseRepository.playTap()
            baseRepository.reload()
            deleteType = FormType()
        }
    }

    fun doSaveWorkData() {
        editWorkItem.day = getCurrentDateStr()
        val msg = editWorkItem.isValid()
        if(!isOk(msg)){
            baseRepository.toast(msg)
            return
        }
        viewModelScope.launch {

            if(editWorkItem.onlyOver) {
                editWorkItem.salaryUuid = ""
                editWorkItem.baseSalaryTime = ""
            }

            if(editWorkItem.isAdd()){
                editWorkItem.uuid = uuid()
                editWorkItem.projectUuid = currentProjectId
                editWorkItem.gmtCreate = MyDateTool.toDateTimeString(Date())
                timeworkService.dataDao.insert(editWorkItem)
            }
            else{
                timeworkService.dataDao.update(editWorkItem)
            }
            baseRepository.reload()
            formType = FormType()
        }
    }

    fun doSaveAwardData() {
        editAwardItem.day = getCurrentDateStr()
        val msg = editAwardItem.isValid()
        if(!isOk(msg)){
            baseRepository.toast(msg)
            return
        }
        viewModelScope.launch {
            if(editAwardItem.isAdd()){
                editAwardItem.uuid = uuid()
                editAwardItem.projectUuid = currentProjectId
                timeworkService.awardDataDao.insert(editAwardItem)
            }
            else{
                timeworkService.awardDataDao.update(editAwardItem)
            }
            baseRepository.reload()
            formType = FormType()
        }
    }

    fun fetchShownData(date: Date): TimeworkShownData {
        val t = shownDataMap[MyDateTool.toDateString(date)]
        if(t != null){
            return t.copy(hourSize = appConfig.hourSize, moneySize = appConfig.moneySize)
        }
        else{
            return TimeworkShownData()
        }
    }

    fun doDeleteSign() {
        viewModelScope.launch {
            timeworkService.dataDao.delete(editWorkItem)
            deleteType = FormType()
            formType = FormType()
            baseRepository.reload()
        }
    }

    fun doDeleteAward() {
        viewModelScope.launch {
            timeworkService.awardDataDao.delete(editAwardItem)
            deleteType = FormType()
            formType = FormType()
            baseRepository.reload()
        }
    }

    fun doChange() {
        reloadData(false)
    }

    fun clearAll() {

        viewModelScope.launch {
            baseRepository.loading()
            timeworkService.clearAll()
            baseRepository.reload()
            baseRepository.finish()
        }

    }

    fun changeProject(it: TimeworkProject) {
        viewModelScope.launch {
            resetForm()
            timeworkService.setProjectId(it.uuid)
            currentProjectId = it.uuid
            currentProjectName = it.name
            baseRepository.reload()
        }
    }
}