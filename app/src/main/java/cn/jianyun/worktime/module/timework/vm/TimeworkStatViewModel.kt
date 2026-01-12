package cn.jianyun.worktime.module.timework.vm



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.dto.TimeworkStatData
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.ui.component.model.kt.RangeDate
import cn.jianyun.worktime.ui.graph.model.LineGraphItem
import cn.jianyun.worktime.ui.graph.model.LineGraphData
import cn.jianyun.worktime.ui.graph.model.PieGraphData
import cn.jianyun.worktime.ui.graph.model.PieGraphItem
import cn.jianyun.worktime.ui.graph.model.makeLineGraphStatData
import cn.jianyun.worktime.ui.graph.model.makePieStatData
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.pushMapValue
import cn.jianyun.worktime.util.timeToFloat
import cn.jianyun.worktime.util.toFloatData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkStatViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val timeworkService: TimeworkService
) : BaseViewModel() {

    var salarys by mutableStateOf(listOf<TimeworkSalary>())
    var awards by mutableStateOf(listOf<TimeworkAward>())
    var awardDatas by mutableStateOf(listOf<TimeworkAwardData>())
    var workDatas by mutableStateOf(listOf<TimeworkData>())


    var currentDate by mutableStateOf(Date())

    var totalStatData by mutableStateOf(TimeworkStatData())
    var appConfig by mutableStateOf(TimeworkAppConfigDTO())
    var salaryLineGraphData by mutableStateOf(LineGraphData())
    var timeLineGraphData by mutableStateOf(LineGraphData())

    var currentMode by mutableStateOf("month")

    var currentYear by mutableStateOf("${MyDateTool.getYear(Date())}")
    var rangeDate by mutableStateOf(RangeDate(beginDate = MyDateTool.getStartDayStringOfMonth(Date()), endDate = MyDateTool.getLastDayStringOfMonth(Date())))

    var salaryPieData by mutableStateOf(PieGraphData())
    var salaryHourPieData by mutableStateOf(PieGraphData())
    var gid by mutableStateOf(0)


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
        baseRepository.makeLoading(viewModelScope)

        viewModelScope.launch {
            oldSid = baseRepository.sid
            currentProjectId = timeworkService.getProjectId()
            salarys = timeworkService.listSalaryByProject(currentProjectId)
            awards = timeworkService.listAwardByProject(currentProjectId)
            appConfig = timeworkService.getAppConfig()
            justLoadData()
        }
    }

    private suspend fun justLoadData(){
        salaryPieData = PieGraphData()
        salaryHourPieData = PieGraphData()
        salaryLineGraphData = LineGraphData()
        timeLineGraphData = LineGraphData()
        var tempRangeDate = realRangeDate()
        awardDatas = timeworkService.awardDataDao.listByPeriod(currentProjectId, tempRangeDate.beginDate, tempRangeDate.endDate)
        workDatas = timeworkService.dataDao.listByPeriod(currentProjectId, tempRangeDate.beginDate, tempRangeDate.endDate)
        statData(workDatas, awardDatas)
        makeMonthGraphData(tempRangeDate.beginDate, tempRangeDate.endDate, workDatas, awardDatas)
        gid += 1
        baseRepository.finish()
    }

    override fun reloadData(dataChanged: Boolean) {
        baseRepository.makeLoading(viewModelScope)
        viewModelScope.launch {
           justLoadData()
        }
    }

    fun statData(workDatas: List<TimeworkData>, awardDatas: List<TimeworkAwardData>) {
        var statData = TimeworkStatData()

        var salaryMap = mutableMapOf<String, Float>()
        var salaryInfoMap = mutableMapOf<String, String>()
        var overSalarySet = mutableSetOf<String>()
        salarys.forEach{
            salaryMap.put(it.uuid, it.fetchRealHourSalary(salarys))
            salaryInfoMap.put(it.uuid, it.name + "(" + it.showValue + ")")
            if(it.type == "over"){
                overSalarySet.add(it.uuid)
            }
        }

        var normalDays = mutableSetOf<String>()
        var overDays = mutableSetOf<String>()
        var totalDays = mutableSetOf<String>()
        workDatas.forEach{
            var newItem = it
            if(it.mode == "hour"){
                if(!it.onlyOver){
                    statData.baseHour = MyDataTool.plusTime(statData.baseHour, it.fetchBaseHour())
                    newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                    newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f
                    statData.baseSalary = MyDataTool.plusPriceWithString(statData.baseSalary, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))

                    normalDays.add(it.day)
                    totalDays.add(it.day)
                }
                if(it.overTime){
                    statData.overHour =  MyDataTool.plusTime(statData.overHour, it.fetchOverHour())
                    newItem.overSalaryInfo = salaryInfoMap.get(it.overSalaryUuid) ?: ""
                    newItem.overSalaryPrice = salaryMap.get(it.overSalaryUuid) ?: 0f
                    statData.overSalary = MyDataTool.plusPriceWithString(statData.overSalary, it.fetchOverMoney(salaryMap.get(it.overSalaryUuid) ?: 0f))
                    overDays.add(it.day)
                    totalDays.add(it.day)
                }
            }
            if(it.mode == "time" && it.endTime != "") {
                newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f
                if(overSalarySet.contains(it.salaryUuid)){
                    overDays.add(it.day)
                    statData.overHour = MyDataTool.plusTime(statData.overHour, it.fetchBaseHour())
                    statData.overSalary = MyDataTool.plusPriceWithString(statData.overSalary, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))
                }
                else{
                    normalDays.add(it.day)
                    statData.baseHour = MyDataTool.plusTime(statData.baseHour, it.fetchBaseHour())
                    statData.baseSalary = MyDataTool.plusPriceWithString(statData.baseSalary, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))
                }
                totalDays.add(it.day)
            }
            if(it.mode == "day"){
                statData.dayCount = MyDataTool.plusNum(statData.dayCount, "1").toString();
                statData.dayMoney = MyDataTool.plusPriceWithString(statData.dayMoney, it.amount)
                statData.dayHour = MyDataTool.plusTime(statData.dayHour, it.fetchBaseHour())

                normalDays.add(it.day)
                totalDays.add(it.day)
            }
        }

        awardDatas.forEach{
            if(it.awardType == "award"){
                statData.awardMoney = MyDataTool.plusPriceWithString(statData.awardMoney, it.awardValue)
            }
            else{
                statData.fineMoney = MyDataTool.plusPriceWithString(statData.fineMoney, it.awardValue)
            }
        }

        statData.normalDay = normalDays.size
        statData.overDay = overDays.size
        statData.totalDay = totalDays.size

        totalStatData = statData
    }

    fun makeMonthGraphData(beginDay: String, endDay: String, workDatas: List<TimeworkData>, awardDatas: List<TimeworkAwardData>) {

        var resultList = mutableListOf<LineGraphItem>()
        var timeList = mutableListOf<LineGraphItem>()
        var salaryMap = mutableMapOf<String, Float>()
        var salaryInfoMap = mutableMapOf<String, String>()
        var salaryNameMap = mutableMapOf<String, String>()

        salarys.forEach{
            salaryMap.put(it.uuid, it.fetchRealHourSalary(salarys))
            salaryInfoMap.put(it.uuid, it.name + "(" + it.showValue + ")")
            salaryNameMap.put(it.uuid, it.name )
        }

        var tempSalaryDataMap = mutableMapOf<String, Float>()
        var tempSalaryTimeMap = mutableMapOf<String, Float>()

        var t = beginDay
        while(t <= endDay){
            var item = LineGraphItem(day = t)
            var timeItem = LineGraphItem(day = t)
            workDatas.filter{it.day == t}.forEach{
                var newItem = it
                if(it.mode == "hour"){
                    if(!it.onlyOver){
                        newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                        newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f
                        item.value = MyDataTool.plusPriceWithString(item.value, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))
                        pushMapValue(tempSalaryDataMap, it.salaryUuid, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f).toFloatData(2))


                        timeItem.value = MyDataTool.plusPriceWithString(timeItem.value, it.fetchBaseHour().timeToFloat(2).toString())
                        pushMapValue(tempSalaryTimeMap, it.salaryUuid, it.fetchBaseHour().timeToFloat(decimal = 2))

                    }
                    if(it.overTime){
                        newItem.overSalaryInfo = salaryInfoMap.get(it.overSalaryUuid) ?: ""
                        newItem.overSalaryPrice = salaryMap.get(it.overSalaryUuid) ?: 0f
                        item.value = MyDataTool.plusPriceWithString(item.value, it.fetchOverMoney(salaryMap.get(it.overSalaryUuid) ?: 0f))
                        pushMapValue(tempSalaryDataMap, it.overSalaryUuid, it.fetchOverMoney(salaryMap.get(it.overSalaryUuid) ?: 0f).toFloatData(2))


                        timeItem.value = MyDataTool.plusPriceWithString(timeItem.value, it.fetchOverHour().timeToFloat(2).toString())
                        pushMapValue(tempSalaryTimeMap, it.overSalaryUuid, it.fetchOverHour().timeToFloat(decimal = 2))
                    }
                }
                if(it.mode == "time" && it.endTime != "") {
                    newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                    newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f
                    item.value = MyDataTool.plusPriceWithString(item.value, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f))
                    pushMapValue(tempSalaryDataMap, it.salaryUuid, it.fetchBaseMoney(salaryMap.get(it.salaryUuid) ?: 0f).toFloatData(2))

                    timeItem.value = MyDataTool.plusPriceWithString(timeItem.value, it.fetchBaseHour().timeToFloat(2).toString())
                    pushMapValue(tempSalaryTimeMap, it.salaryUuid, it.fetchBaseHour().timeToFloat(decimal = 2))
                }
                if(it.mode == "day"){
                    item.value = MyDataTool.plusPriceWithString(item.value, it.amount)
                    pushMapValue(tempSalaryDataMap, "日结", it.amount.toFloatData(2))
                    pushMapValue(tempSalaryTimeMap, "日结", it.fetchBaseHour().timeToFloat(decimal = 2))
                    timeItem.value = MyDataTool.plusPriceWithString(timeItem.value, it.fetchBaseHour().timeToFloat(2).toString())
                }
            }

            awardDatas.filter{it.day == t}.forEach {
                if(it.awardType == "award"){
                    pushMapValue(tempSalaryDataMap, "补贴", it.awardValue.toFloatData(2))
                    item.value = MyDataTool.plusPriceWithString(item.value, it.awardValue)
                }
                else{
                    pushMapValue(tempSalaryDataMap, "扣款",  it.awardValue.toFloatData(2))
                    item.value = MyDataTool.minusPriceWithString(item.value, it.awardValue)
                }
            }
            timeList.add(timeItem)
            resultList.add(item)
            t = MyDateTool.nextDay(t);
        }

        var tempPieDataList = mutableListOf<PieGraphItem>()
        tempSalaryDataMap.forEach { k,v ->
            if(salaryNameMap.containsKey(k)){
                tempPieDataList.add(PieGraphItem(name = salaryNameMap.get(k) ?: "", value="${v}"))
            }
            else{
                tempPieDataList.add(PieGraphItem(name = k, value="${v}"))
            }
        }


        var tempSalaryTimePieDataList = mutableListOf<PieGraphItem>()
        tempSalaryTimeMap.forEach { k,v ->
            if(salaryNameMap.containsKey(k)){
                tempSalaryTimePieDataList.add(PieGraphItem(name = salaryNameMap.get(k) ?: "", value="${v}"))
            }
            else{
                tempSalaryTimePieDataList.add(PieGraphItem(name = k, value="${v}"))
            }
        }

        salaryPieData = makePieStatData("薪水分布", "元", decimal = 2, datalist = tempPieDataList)
        salaryHourPieData = makePieStatData("薪水工时分布", "小时", decimal = 2, datalist = tempSalaryTimePieDataList)
        salaryLineGraphData = makeLineGraphStatData(beginDay=beginDay, endDay=endDay, unit="元", datalist = resultList)
        timeLineGraphData = makeLineGraphStatData(beginDay=beginDay, endDay=endDay, unit="小时", datalist = timeList)

    }


    fun realRangeDate():RangeDate {
        var tempRangeDate = RangeDate()
        if(currentMode == "month"){
            tempRangeDate.beginDate = MyDateTool.getStartDayStringOfMonth(currentDate)
            tempRangeDate.endDate = MyDateTool.getLastDayStringOfMonth(currentDate)
        }
        else if(currentMode == "year"){
            tempRangeDate.beginDate = MyDateTool.getStartDayStringOfMonth(MyDateTool.make(currentYear, "1", "1"))
            tempRangeDate.endDate = MyDateTool.getLastDayStringOfMonth(MyDateTool.make(currentYear, "12", "1"))
        }
        else{
            tempRangeDate = rangeDate
        }
        return tempRangeDate
    }

}