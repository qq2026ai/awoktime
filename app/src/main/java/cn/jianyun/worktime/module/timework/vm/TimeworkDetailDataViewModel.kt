package cn.jianyun.worktime.module.timework.vm




import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.dao.TimeworkAppConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDefaultConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkSalaryDao
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.ui.component.model.kt.RangeDate
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkDetailDataViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    var timeworkService: TimeworkService
) : BaseViewModel() {

    private var salarys by mutableStateOf(listOf<TimeworkSalary>())
    private var awards by mutableStateOf(listOf<TimeworkAward>())

    private var workDatas by mutableStateOf(listOf<TimeworkData>())
    private var awardDatas by mutableStateOf(listOf<TimeworkAwardData>())

    var workList by mutableStateOf(listOf<TimeworkData>())
    var workHourList by mutableStateOf(listOf<TimeworkData>())
    var workDayList by mutableStateOf(listOf<TimeworkData>())
    var workTimeList by mutableStateOf(listOf<TimeworkData>())
    var awardList by mutableStateOf(listOf<TimeworkAwardData>())

    var currentDate by mutableStateOf(Date())

    var appConfig by mutableStateOf(TimeworkAppConfigDTO())
    var currentMode by mutableStateOf("hour")

    var rangeDate by mutableStateOf(RangeDate(beginDate = MyDateTool.getStartDayStringOfMonth(Date()), endDate = MyDateTool.getLastDayStringOfMonth(Date())))


    fun getCurrentDateStr(): String {
        return MyDateTool.toDateString(currentDate)
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    init {
        reload()
    }

    override fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
            currentProjectId = timeworkService.getProjectId()
            salarys = timeworkService.salaryDao.list()
            awards = timeworkService.awardDao.list()
            appConfig = timeworkService.appConfigDao.get()
            reloadData()
        }
    }

    override fun reloadData(dataChanged: Boolean) {
        //列出本月的数据
        val beginDay = rangeDate.beginDate
        val endDay = rangeDate.endDate

        viewModelScope.launch {
            workDatas = timeworkService.dataDao.listByPeriod(currentProjectId, beginDay, endDay)
            awardDatas = timeworkService.awardDataDao.listByPeriod(currentProjectId, beginDay, endDay)

            makeWorkDetail(workDatas)
            makeAwardDetail(awardDatas)
        }
    }

    private fun makeAwardDetail(tempData: List<TimeworkAwardData>) {
        var resultList = mutableListOf<TimeworkAwardData>()
        tempData.forEach{
            var newItem = it
            newItem.awardName = awards.find{it.uuid == newItem.awardUuid}?.name ?: "未知"
            resultList.add(newItem)
        }
        awardList = resultList
    }

    fun makeWorkDetail(workDatas: List<TimeworkData>) {

        var salaryMap = mutableMapOf<String, Float>()
        var salaryInfoMap = mutableMapOf<String, String>()
        salarys.forEach{
            salaryMap.put(it.uuid, it.fetchRealHourSalary(salarys))
            salaryInfoMap.put(it.uuid, it.name + "(" + it.showValue + ")")
        }

        var resultList = mutableListOf<TimeworkData>()

        workDatas.forEach{
            var newItem = it
            if(it.mode == "hour"){
                if(!it.onlyOver){
                    newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                    newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f

                    newItem.totalSalaryPrice = MyDataTool.getPriceWithFloat(newItem.fetchBaseMoney(newItem.baseSalaryPrice), 2)

                }
                if(it.overTime){
                    newItem.overSalaryInfo = salaryInfoMap.get(it.overSalaryUuid) ?: ""
                    newItem.overSalaryPrice = salaryMap.get(it.overSalaryUuid) ?: 0f

                    var overPrice = MyDataTool.getPriceWithFloat(newItem.fetchOverMoney(newItem.overSalaryPrice), 2)
                    newItem.totalSalaryPrice = newItem.totalSalaryPrice + overPrice
                }
            }
            if(it.mode == "time" && it.endTime != "") {
                newItem.baseSalaryInfo = salaryInfoMap.get(it.salaryUuid) ?: ""
                newItem.baseSalaryPrice = salaryMap.get(it.salaryUuid) ?: 0f
                newItem.baseSalaryTime =MyDataTool.minusTime(MyDataTool.minusTime(newItem.endTime, newItem.beginTime, true), newItem.restTime, false)
            }
            resultList.add(newItem)
        }
        resultList.sortWith{t1, t2 -> ifv(t1.day < t2.day, -1, 1)}
        workList = resultList
        workHourList = resultList.filter { it.mode == "hour" }
        workDayList = resultList.filter { it.mode == "day" }
        workTimeList = resultList.filter { it.mode == "time" }
    }
    fun initModel(editInfo: RangeDate) {
        rangeDate = editInfo
    }
}