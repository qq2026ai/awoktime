package cn.jianyun.worktime.module.timework.views.stat



import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.vm.TimeworkDetailDataViewModel
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.form.TipDialog
import cn.jianyun.worktime.ui.component.model.kt.RangeDate
import cn.jianyun.worktime.ui.component.nav.CenterColumn
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.DateRangeChooseView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.mainBg
import cn.jianyun.worktime.util.toVipPage
import com.alibaba.fastjson2.JSON


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkDetailDataView(navHostController: NavHostController, arguments: Bundle?) {
    val viewModel = hiltViewModel<TimeworkDetailDataViewModel>()

    LaunchedEffect(Unit){
        val model = arguments?.getString("model")
        val editInfo = JSON.parseObject(model, RangeDate::class.java)
        viewModel.initModel(editInfo)
        viewModel.tryReload()
    }

    Scaffold(content = {

        Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HeaderView(title = "工时数据明细及导出", backAction = {
                    goBack(navHostController)
                }, rightTool = {
                    Row(){
                        Text("帮助", modifier=Modifier.clickable {
                            viewModel.formType = FormType("tip")
                        })
                    }
                })
                CenterColumn {
                    DateRangeChooseView(value = viewModel.rangeDate){
                        viewModel.rangeDate = it
                        viewModel.reloadData()
                    }
                }
                CenterRow(padding=12.dp) {
                    SegmentPickerView(options = SelectUtil.WORK_DATA_TYPES, value=viewModel.currentMode, onChange = {
                        viewModel.currentMode = it
                    })
                }
                Box(modifier =Modifier, contentAlignment = Alignment.BottomCenter){
                    Box(modifier=Modifier.padding(bottom=80.dp)) {
                        if(viewModel.currentMode == "hour"){
                            Table(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .mainBg(6.dp)
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                columnCount = 8,
                                rowCount = viewModel.workHourList.size + 1,
                                afterRow = {
                                    Text("", modifier = Modifier
                                        .background(Color.Gray.copy(0.12f))
                                        .width(
                                            listOf(
                                                120,
                                                100,
                                                180,
                                                100,
                                                180,
                                                120,
                                                120,
                                                200
                                            ).sum().dp
                                        )
                                        .height(1.dp))
                                },
                                cellContent = { columnIndex, rowIndex ->
                                    if(rowIndex == 0){
                                        getHourHeader(x = columnIndex)
                                    }
                                    else{
                                        getHourContent(viewModel.workHourList[rowIndex-1], columnIndex)
                                    }
                                })
                        }
                        else if(viewModel.currentMode == "time"){
                            Table(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .mainBg(6.dp)
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                columnCount = 8,
                                rowCount = viewModel.workTimeList.size+ 1,
                                afterRow = {
                                    Text("", modifier = Modifier
                                        .background(Color.Gray.copy(0.12f))
                                        .width(
                                            listOf(
                                                120,
                                                80,
                                                80,
                                                100,
                                                100,
                                                150,
                                                100,
                                                200
                                            ).sum().dp
                                        )
                                        .height(1.dp))
                                },
                                cellContent = { columnIndex, rowIndex ->
                                    if(rowIndex == 0){
                                        getTimeHeader(x = columnIndex)
                                    }
                                    else{
                                        getTimeContent(viewModel.workTimeList[rowIndex-1], columnIndex)
                                    }
                                })
                        }
                        else if(viewModel.currentMode == "day"){
                            Table(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .mainBg(6.dp)
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                columnCount = 4,
                                rowCount = viewModel.workDayList.size + 1,
                                afterRow = {
                                    Text("", modifier = Modifier
                                        .background(Color.Gray.copy(0.12f))
                                        .width(listOf(120, 100, 220, 220).sum().dp)
                                        .height(1.dp))
                                },
                                cellContent = { columnIndex, rowIndex ->
                                    if(rowIndex == 0){
                                        getDayHeader(x = columnIndex)
                                    }
                                    else{
                                        getDayContent(viewModel.workDayList[rowIndex-1], columnIndex)
                                    }
                                })
                        }
                        else if(viewModel.currentMode == "award"){
                            Table(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .mainBg(6.dp)
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                columnCount = 5,
                                rowCount = viewModel.awardList.size + 1,
                                afterRow = {
                                    Text("", modifier = Modifier
                                        .background(Color.Gray.copy(0.12f))
                                        .width(listOf(120, 100, 120, 180, 200).sum().dp)
                                        .height(1.dp))
                                },
                                cellContent = { columnIndex, rowIndex ->
                                    if(rowIndex == 0){
                                        getAwardHeader(x = columnIndex)
                                    }
                                    else{
                                        getAwardContent(viewModel.awardList[rowIndex-1], columnIndex)
                                    }
                                })
                        }
                    }

                    GroupView(verticalPadding = 5.dp) {
                        LongOkButton(label = "导出") {
                            if(viewModel.baseRepository.isVip()){
                                viewModel.baseRepository.postEvent2("doExport")
                                viewModel.doExport()
                            }
                            else{
                                toVipPage(navHostController)
                            }
                        }
                    }
                }
            }
            LoadingDialog()

            if(viewModel.formType.isForm("tip")){
                TipDialog(title = "使用帮助", message = "支持自定义导出日期区间，导出成功后将自动保存至手机下载目录，并支持分享至微信等其他应用，如果不想分享，可以取消此操作即可（毕竟微信可以快速查看导出效果）") {
                    viewModel.resetForm()
                }
            }

        }

    })
}

@Composable
fun getAwardContent(data: TimeworkAwardData, column: Int) {
    var info = ""
    when(column){
        0 -> info = data.day
        1 -> info = data.typeName()
        2 -> info = data.awardName
        3 -> info = MyDataTool.withUnit(data.awardValue, "元")
        4 -> info = data.remark
    }
    return Text(info, fontSize = 14.sp, modifier = Modifier.padding(0.dp, 2.dp))
}

@Composable
fun getHourContent(data: TimeworkData, column: Int){
    var info = ""
    when(column){
        0 -> info = data.day
        1 -> info = data.fetchBaseHourShownInfo()
        2 -> info = data.baseSalaryInfo
        3 -> info = data.fetchOverHourShownInfo()
        4 -> info = data.overSalaryInfo
        5 -> info = MyDataTool.getShownTime(MyDataTool.plusTime(data.fetchBaseHour(), data.fetchOverHour()))
        6 -> info = MyDataTool.withUnit(MyDataTool.getShownPrice(data.totalSalaryPrice.toString(), 2), "元")
        7 -> info = data.remark
    }
    return Text(info, fontSize = 14.sp, modifier = Modifier.padding(0.dp, 2.dp))
}

@Composable
fun getHourHeader(x: Int) {
    val titles = listOf("日期", "正班工时", "正班薪水", "加班工时", "加班薪水", "合计工时", "合计薪水", "备注")
    val size = listOf(120, 100, 180, 100, 180, 120, 120, 200)
    return Text(titles[x], fontSize = 14.sp, modifier = Modifier.width(size[x].dp))
}

@Composable
fun getAwardHeader(x: Int) {
    val titles = listOf("日期", "类型", "名称", "金额", "备注")
    val size = listOf(120, 100,120,180,200)
    return Text(titles[x], fontSize = 14.sp, modifier = Modifier.width(size[x].dp))
}

@Composable
fun getTimeHeader(x: Int) {
    val titles = listOf("日期", "上班时间", "下班时间", "休息时长", "合计工时", "上班薪水", "合计薪水", "备注")
    val size = listOf(120, 80, 80, 100, 100, 150,100, 200)
    return Text(titles[x], fontSize = 14.sp, modifier = Modifier.width(size[x].dp))
}
@Composable
fun getDayHeader(x: Int) {
    val titles = listOf("日期",  "日结",  "工作时长", "备注")
    val size = listOf(120, 100,  200, 220)
    return Text(titles[x], fontSize = 14.sp, modifier = Modifier.width(size[x].dp))
}


@Composable
fun getTimeContent(data: TimeworkData, column: Int){
    var info = ""
    when(column){
        0 -> info = data.day
        1 -> info = data.beginTime
        2 -> info = data.endTime
        3 -> info = MyDataTool.getShownTime(data.restTime)
        4 -> info = MyDataTool.getShownTime(data.baseSalaryTime)
        5 -> info = data.baseSalaryInfo
        6 -> info = MyDataTool.withUnit(MyDataTool.multipyWithString(MyDataTool.timeToDecimal(data.baseSalaryTime), data.baseSalaryPrice.toString()), "元")
        6 -> info = data.remark
    }
    return Text(info, fontSize = 14.sp, modifier = Modifier.padding(0.dp, 2.dp))

}

@Composable
fun getDayContent(data: TimeworkData, column: Int){
    var info = ""
    when(column){
        0 -> info = data.day
        1 -> info = MyDataTool.withUnit(data.amount, "元")
        2 -> info = MyDataTool.getShownTime(data.fetchBaseHour(), false)
        3 -> info = data.remark
    }
    return Text(info, fontSize = 14.sp, modifier = Modifier.padding(0.dp, 2.dp))
}

@Composable
fun Table(
    modifier: Modifier = Modifier,
    rowModifier: Modifier = Modifier,
    verticalLazyListState: LazyListState = rememberLazyListState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    columnCount: Int,
    rowCount: Int,
    beforeRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    afterRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    cellContent: @Composable (columnIndex: Int, rowIndex: Int) -> Unit
) {
    val columnWidths = remember { mutableStateMapOf<Int, Int>() }

    Box(modifier = modifier.then(Modifier.horizontalScroll(horizontalScrollState))) {
        LazyColumn(state = verticalLazyListState) {
            items(rowCount) { rowIndex ->
                Column {
                    beforeRow?.invoke(rowIndex)

                    Row(modifier = rowModifier) {
                        (0 until columnCount).forEach { columnIndex ->
                            Box(modifier = Modifier.layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)

                                val existingWidth = columnWidths[columnIndex] ?: 0
                                val maxWidth = maxOf(existingWidth, placeable.width)

                                if (maxWidth > existingWidth) {
                                    columnWidths[columnIndex] = maxWidth
                                }

                                layout(width = maxWidth, height = placeable.height) {
                                    placeable.placeRelative(0, 0)
                                }
                            }) {
                                cellContent(columnIndex, rowIndex)
                            }
                        }
                    }

                    afterRow?.invoke(rowIndex)
                }
            }
        }
    }
}