package cn.jianyun.worktime.module.timework.vm




import android.content.Intent
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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




    fun doExport() {


        val that = this
        viewModelScope.launch {
            baseRepository.loading(true)
            // 创建Excel工作簿
            // 创建Excel工作簿
            val workbook: Workbook = XSSFWorkbook() // 创建.xlsx文件

            var cnt = 0

            val data1: MutableList<Array<String>> = ArrayList()
            data1.add(arrayOf("日期", "正班工时", "正班薪水", "加班工时", "加班薪水", "合计工时", "合计薪水", "备注"))
            that.workHourList.forEach {
                val data = it
                data1.add(arrayOf(
                    data.day,
                    data.fetchBaseHourShownInfo(),
                    data.baseSalaryInfo,
                    data.fetchOverHourShownInfo(),
                    data.overSalaryInfo,
                    MyDataTool.getShownTime(MyDataTool.plusTime(data.fetchBaseHour(), data.fetchOverHour())),
                    MyDataTool.withUnit(MyDataTool.getShownPrice(data.totalSalaryPrice.toString(), 2), "元")
                    , data.remark))
            }

            if(data1.size > 1){
                val sheet1: Sheet = workbook.createSheet("按工时")
                cnt += 1

                // 写入数据
                for ((rowNum, rowData) in data1.withIndex()) {
                    val row: Row = sheet1.createRow(rowNum)
                    for ((colNum, field) in rowData.withIndex()) {
                        val cell: Cell = row.createCell(colNum)
                        cell.setCellValue(field)
                    }
                }


                sheet1.setColumnWidth(0, 256 * 10)
                sheet1.setColumnWidth(1, 256 * 12)
                sheet1.setColumnWidth(2, 256 * 20)
                sheet1.setColumnWidth(3, 256 * 12)
                sheet1.setColumnWidth(4, 256 * 20)
                sheet1.setColumnWidth(5, 256 * 12)
                sheet1.setColumnWidth(6, 256 * 10)
                sheet1.setColumnWidth(7, 256 * 20)


            }


            val data2: MutableList<Array<String>> = ArrayList()
            data2.add(arrayOf("日期",  "日结",  "时长", "备注"))
            that.workDayList.forEach {
                val data = it
                data2.add(arrayOf(
                    data.day,
                    MyDataTool.withUnit(data.amount, "元"),
                    MyDataTool.getShownTime(data.fetchBaseHour(), false),
                    data.remark))
            }

            if(data2.size > 1){
                val sheet2: Sheet = workbook.createSheet("按日结")
                cnt += 1


                // 写入数据
                for ((rowNum, rowData) in data2.withIndex()) {
                    val row: Row = sheet2.createRow(rowNum)
                    for ((colNum, field) in rowData.withIndex()) {
                        val cell: Cell = row.createCell(colNum)
                        cell.setCellValue(field)
                    }
                }

                sheet2.setColumnWidth(0, 256 * 10)
                sheet2.setColumnWidth(1, 256 * 10)
                sheet2.setColumnWidth(2, 256 * 20)

            }


            val data3: MutableList<Array<String>> = ArrayList()
            data3.add(arrayOf("日期", "上班时间", "下班时间", "休息时长", "合计工时", "上班薪水", "合计薪水", "备注"))
            that.workTimeList.forEach {
                val data = it
                data3.add(arrayOf(
                    data.day,
                    data.beginTime,
                    data.endTime,
                    MyDataTool.getShownTime(data.restTime),
                    MyDataTool.getShownTime(data.baseSalaryTime),
                    data.baseSalaryInfo,
                    MyDataTool.withUnit(MyDataTool.multipyWithString(MyDataTool.timeToDecimal(data.baseSalaryTime), data.baseSalaryPrice.toString()), "元")
                    , data.remark))
            }

            if(data3.size > 1){
                val sheet3: Sheet = workbook.createSheet("按时间")
                cnt += 1
                // 写入数据
                for ((rowNum, rowData) in data3.withIndex()) {
                    val row: Row = sheet3.createRow(rowNum)
                    for ((colNum, field) in rowData.withIndex()) {
                        val cell: Cell = row.createCell(colNum)
                        cell.setCellValue(field)
                    }
                }

                sheet3.setColumnWidth(0, 256 * 10)
                sheet3.setColumnWidth(1, 256 * 10)
                sheet3.setColumnWidth(2, 256 * 10)
                sheet3.setColumnWidth(3, 256 * 10)
                sheet3.setColumnWidth(4, 256 * 10)
                sheet3.setColumnWidth(5, 256 * 20)
                sheet3.setColumnWidth(6, 256 * 10)
                sheet3.setColumnWidth(7, 256 * 20)

            }

            val data4: MutableList<Array<String>> = ArrayList()
            data4.add(arrayOf("日期", "类型", "名称", "金额", "备注"))
            that.awardList.forEach {
                val data = it
                data4.add(arrayOf(
                    data.day,
                    data.typeName(),
                    data.awardName,
                    MyDataTool.withUnit(data.awardValue, "元"),
                    data.remark))
            }

            if(data4.size > 1){
                val sheet4: Sheet = workbook.createSheet("按补扣")
                cnt += 1
                // 写入数据
                for ((rowNum, rowData) in data4.withIndex()) {
                    val row: Row = sheet4.createRow(rowNum)
                    for ((colNum, field) in rowData.withIndex()) {
                        val cell: Cell = row.createCell(colNum)
                        cell.setCellValue(field)
                    }
                }

                sheet4.setColumnWidth(0, 256 * 10)
                sheet4.setColumnWidth(1, 256 * 10)
                sheet4.setColumnWidth(2, 256 * 10)
                sheet4.setColumnWidth(3, 256 * 10)
                sheet4.setColumnWidth(4, 256 * 20)
            }

            if(cnt == 0){
                toast("没有数据可导出")
                return@launch
            }

//            val exportDir =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//            if (!exportDir.exists()) {
//                exportDir.mkdirs()
//            }


            val exportDir = baseRepository.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            mlog("downloadFILE",exportDir)
            if (exportDir != null) {
                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }
            }


            val time = MyDateTool.format(Date(), "yyyyMMdd_HHmmss")
            val file = File(exportDir, "极简记工时" + time + ".xlsx")
            try {
                FileOutputStream(file).use { outputStream ->
                    workbook.write(outputStream)
                    workbook.close()

                    val context = baseRepository.context
                    val file = File(exportDir, "极简记工时" + time + ".xlsx") // 替换为你的 Excel 文件路径
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "application/vnd.ms-excel" // Excel 文件的 MIME 类型
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }

                    val intent = Intent.createChooser(shareIntent, "分享")
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                }



                toast("已导出到手机下载目录")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }



    }
}