package cn.jianyun.worktime.module.timework.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.ui.component.model.kt.RangeDate
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.TimeworkPeriodTool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkBatchSettleViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val timeworkService: TimeworkService
) : BaseViewModel() {

    var appConfig by mutableStateOf(TimeworkAppConfigDTO())
    var salarys by mutableStateOf(listOf<TimeworkSalary>())
    var awards by mutableStateOf(listOf<TimeworkAward>())

    var workList by mutableStateOf(listOf<TimeworkData>())
    var awardList by mutableStateOf(listOf<TimeworkAwardData>())

    var rangeDate by mutableStateOf(RangeDate())
    var settleType by mutableStateOf("all")
    var salaryUuid by mutableStateOf("")
    var settleStatus by mutableStateOf("all")

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload() {
        viewModelScope.launch {
            baseRepository.loading()
            oldSid = baseRepository.sid
            currentProjectId = timeworkService.getProjectId()
            appConfig = timeworkService.getAppConfig()
            rangeDate = TimeworkPeriodTool.getPeriodRange(Date(), appConfig.statDay)
            salarys = timeworkService.listSalaryByProject(currentProjectId)
            awards = timeworkService.listAwardByProject(currentProjectId)
            reloadDataInternal()
            inited = true
            baseRepository.finish()
        }
    }

    override fun reloadData(dataChanged: Boolean) {
        viewModelScope.launch {
            reloadDataInternal()
        }
    }

    private suspend fun reloadDataInternal() {
        currentProjectId = timeworkService.getProjectId()
        val allWork = timeworkService.dataDao.listByPeriod(currentProjectId, rangeDate.beginDate, rangeDate.endDate)
        val allAward = timeworkService.awardDataDao.listByPeriod(currentProjectId, rangeDate.beginDate, rangeDate.endDate)

        workList = makeWorkDetail(allWork.filter { matchWorkFilter(it) })
        awardList = makeAwardDetail(allAward.filter { matchAwardFilter(it) })
    }

    fun salaryOptions(): List<SelectDO> {
        val options = mutableListOf(SelectDO("全部", ""))
        options.addAll(
            salarys
                .filter { it.shown || it.uuid == salaryUuid }
                .map { it.toSelect() }
        )
        return options
    }

    fun resultDayList(): List<String> {
        return (workList.map { it.day } + awardList.map { it.day })
            .distinct()
            .sorted()
            .filter { workListByDay(it).isNotEmpty() || awardListByDay(it).isNotEmpty() }
    }

    fun workListByDay(day: String): List<TimeworkData> {
        return workList.filter { it.day == day }
    }

    fun awardListByDay(day: String): List<TimeworkAwardData> {
        return awardList.filter { it.day == day }
    }

    fun dayHasSettled(day: String): Boolean {
        return workListByDay(day).any { it.isSettled() } || awardListByDay(day).any { it.isSettled() }
    }

    fun dayAllSettled(day: String): Boolean {
        val all = workListByDay(day).size + awardListByDay(day).size
        if(all == 0){
            return false
        }
        val settled = workListByDay(day).count { it.isSettled() } + awardListByDay(day).count { it.isSettled() }
        return settled == all
    }

    fun resultSize(): Int {
        return workList.size + awardList.size
    }

    fun doSettleAll(settled: Boolean) {
        if(resultSize() == 0){
            toast("当前没有可操作的数据")
            return
        }
        viewModelScope.launch {
            baseRepository.loading()
            workList.forEach {
                timeworkService.dataDao.update(it.settle(settled))
            }
            awardList.forEach {
                timeworkService.awardDataDao.update(it.settle(settled))
            }
            reloadData(true)
            baseRepository.reload()
            baseRepository.finish()
            toast(if (settled) "批量结算成功" else "取消结算成功")
        }
    }

    fun toggleWorkSettle(item: TimeworkData) {
        updateWorkSettle(item, !item.isSettled())
    }

    fun toggleAwardSettle(item: TimeworkAwardData) {
        updateAwardSettle(item, !item.isSettled())
    }

    private fun updateWorkSettle(item: TimeworkData, settled: Boolean) {
        viewModelScope.launch {
            val newItem = item.settle(settled)
            timeworkService.dataDao.update(newItem)
            workList = workList.map { if (it.uuid == newItem.uuid) newItem else it }
            baseRepository.reload()
            toast(if (settled) "已设为结算" else "已取消结算")
        }
    }

    private fun updateAwardSettle(item: TimeworkAwardData, settled: Boolean) {
        viewModelScope.launch {
            val newItem = item.settle(settled)
            timeworkService.awardDataDao.update(newItem)
            awardList = awardList.map { if (it.uuid == newItem.uuid) newItem else it }
            baseRepository.reload()
            toast(if (settled) "已设为结算" else "已取消结算")
        }
    }

    fun workIncome(item: TimeworkData): String {
        if(item.mode == "day"){
            return item.amount
        }
        if(item.mode == "hour"){
            var total = 0f
            if(!item.onlyOver){
                val salary = salarys.find { it.uuid == item.salaryUuid }?.fetchRealHourSalary(salarys) ?: 0f
                total += MyDataTool.getPriceWithFloat(item.fetchBaseMoney(salary), 2)
            }
            if(item.overTime){
                val salary = salarys.find { it.uuid == item.overSalaryUuid }?.fetchRealHourSalary(salarys) ?: 0f
                total += MyDataTool.getPriceWithFloat(item.fetchOverMoney(salary), 2)
            }
            return MyDataTool.getShownPrice(total.toString(), 2)
        }
        if(item.mode == "time"){
            val salary = salarys.find { it.uuid == item.salaryUuid }?.fetchRealHourSalary(salarys) ?: 0f
            return item.fetchBaseMoney(salary)
        }
        return ""
    }

    fun workSalaryLabel(item: TimeworkData): String {
        return when (item.mode) {
            "hour" -> {
                if(item.onlyOver){
                    salarys.find { it.uuid == item.overSalaryUuid }?.toSelect()?.label ?: "未设置"
                }
                else{
                    salarys.find { it.uuid == item.salaryUuid }?.toSelect()?.label ?: "未设置"
                }
            }
            "time" -> salarys.find { it.uuid == item.salaryUuid }?.toSelect()?.label ?: "未设置"
            else -> ""
        }
    }

    fun workTimeLabel(item: TimeworkData): String {
        return when (item.mode) {
            "hour" -> buildString {
                if(!item.onlyOver && item.fetchBaseHourShownInfo() != "无"){
                    append("正班 ${item.fetchBaseHourShownInfo()}")
                }
                if(item.overTime){
                    if(isNotEmpty()) append(" / ")
                    append("加班 ${item.fetchOverHourShownInfo()}")
                }
            }
            "time" -> "${item.beginTime}~${item.endTime}"
            "day" -> if(item.beginTime != "" && item.endTime != "") "${item.beginTime}~${item.endTime}" else "日结"
            "leave" -> "请假"
            "rest" -> "休息"
            else -> ""
        }
    }

    fun workDurationLabel(item: TimeworkData): String {
        return MyDataTool.getShownTime(item.fetchTotalHour(), true)
    }

    private fun matchSettleStatus(settled: Boolean): Boolean {
        return when (settleStatus) {
            "settled" -> settled
            "pending" -> !settled
            else -> true
        }
    }

    private fun matchWorkFilter(item: TimeworkData): Boolean {
        if(settleType == "award"){
            return false
        }
        if(item.mode == "leave" || item.mode == "rest"){
            return false
        }
        if(!item.canBatchSettle()){
            return false
        }
        if(!matchSettleStatus(item.isSettled())){
            return false
        }
        if(settleType == "work" || settleType == "all"){
            if(salaryUuid == ""){
                return true
            }
            return item.salaryUuid == salaryUuid || item.overSalaryUuid == salaryUuid
        }
        return false
    }

    private fun matchAwardFilter(item: TimeworkAwardData): Boolean {
        if(!item.canBatchSettle()){
            return false
        }
        if(!matchSettleStatus(item.isSettled())){
            return false
        }
        if(settleType == "work"){
            return false
        }
        if(settleType == "all"){
            return salaryUuid == ""
        }
        if(settleType == "award"){
            return salaryUuid == ""
        }
        return false
    }

    private fun makeWorkDetail(list: List<TimeworkData>): List<TimeworkData> {
        val resultList = mutableListOf<TimeworkData>()
        list.forEach{
            val newItem = it
            if(!newItem.canBatchSettle()){
                return@forEach
            }
            if(it.mode == "time" && it.endTime != ""){
                newItem.baseSalaryTime = MyDataTool.minusTime(
                    MyDataTool.minusTime(newItem.endTime, newItem.beginTime, true),
                    newItem.restTime,
                    false
                )
            }
            resultList.add(newItem)
        }
        resultList.sortWith { t1, t2 ->
            if(t1.day == t2.day){
                t1.gmtCreate.compareTo(t2.gmtCreate)
            }
            else{
                t1.day.compareTo(t2.day)
            }
        }
        return resultList
    }

    private fun TimeworkData.canBatchSettle(): Boolean {
        return when (mode) {
            "hour" -> (!onlyOver && baseSalaryTime != "" && salaryUuid != "") || (overTime && overSalaryTime != "" && overSalaryUuid != "")
            "time" -> beginTime != "" && endTime != "" && salaryUuid != ""
            "day" -> amount != ""
            else -> false
        }
    }

    private fun makeAwardDetail(list: List<TimeworkAwardData>): List<TimeworkAwardData> {
        val resultList = mutableListOf<TimeworkAwardData>()
        list.forEach{
            val newItem = it
            if(!newItem.canBatchSettle()){
                return@forEach
            }
            newItem.awardName = awards.find{ award -> award.uuid == newItem.awardUuid }?.name
                ?: if(newItem.awardName != "") newItem.awardName else newItem.name
            resultList.add(newItem)
        }
        resultList.sortWith { t1, t2 ->
            if(t1.day == t2.day){
                t1.gmtCreate.compareTo(t2.gmtCreate)
            }
            else{
                t1.day.compareTo(t2.day)
            }
        }
        return resultList
    }

    private fun TimeworkAwardData.canBatchSettle(): Boolean {
        return awardUuid != "" && awardType != "" && awardValue != ""
    }
}
