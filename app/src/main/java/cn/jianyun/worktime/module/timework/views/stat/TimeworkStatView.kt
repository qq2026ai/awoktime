package cn.jianyun.worktime.module.timework.views.stat



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkStatViewModel
import cn.jianyun.worktime.ui.component.nav.DateRangeChooseView
import cn.jianyun.worktime.ui.component.nav.MonthChooseView
import cn.jianyun.worktime.ui.component.nav.YearChooseView
import cn.jianyun.worktime.ui.graph.GraphLineView
import cn.jianyun.worktime.ui.graph.PieGraph
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.ui.component.form.GroupTitleView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.nav.CenterColumn
import cn.jianyun.worktime.ui.component.nav.FullRow
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.toPage
import com.alibaba.fastjson2.toJSONString

@Composable
fun TimeworkStatView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkStatViewModel>()
    LaunchedEffect(Unit){
        viewModel.tryReload()
    }

    val statData by remember {
        derivedStateOf {
            viewModel.totalStatData
        }
    }
    Column {

        CenterColumn(paddingBottom = 10.dp) {
            SegmentPickerView(value = viewModel.currentMode, padding = 10.dp, width=60.dp, options = SelectUtil.DATE_CHOOSE_TYPES, onChange = {
                viewModel.currentMode = it
                viewModel.reloadData()
            })

            if(viewModel.currentMode == "month"){
                MonthChooseView(value=viewModel.getCurrentDateStr())  {
                    viewModel.currentDate = MyDateTool.parseDateString(it)
                    viewModel.reloadData()
                }
            }
            else if(viewModel.currentMode == "year"){
                YearChooseView(value=viewModel.currentYear)  {
                    viewModel.currentYear = it
                    viewModel.reloadData()
                }
            }
            else{
                DateRangeChooseView(value=viewModel.rangeDate) {
                    viewModel.rangeDate = it
                    viewModel.reloadData()
                }
            }
        }

        Column(modifier= Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)) {

            GroupTitleView(title = "数据汇总", tool = { /*TODO*/ }) {
                if(statData.dayCount != ""){
                    FullRow{
                        statNumView(title = "日结次数", value = MyDataTool.withUnit(statData.dayCount, "次"), modifier=Modifier.weight(1f))
                        statNumView(title = "日结工时", value = MyDataTool.getShownTime(statData.dayHour, true), modifier=Modifier.weight(1f))
                        statNumView(title = "日结收入", value =  MyDataTool.withUnit(statData.dayMoney, 2,"元"), modifier=Modifier.weight(1f))
                    }
                }

                if(statData.dayCount == "" || statData.baseHour != "" || statData.overHour != ""){
                    Blank()
                    FullRow{
                        statNumView(title = "正班工时", value = MyDataTool.getShownTime(statData.baseHour, true), modifier=Modifier.weight(1f))
                        statNumView(title = "加班工时", value = MyDataTool.getShownTime(statData.overHour, true), modifier=Modifier.weight(1f))
                        statNumView(title = "总工时", value = MyDataTool.getShownTime(statData.fetchTotalHour(), true), modifier=Modifier.weight(1f))
                    }
                    Blank()
                    FullRow{
                        statNumView(title = "正班收入", value = MyDataTool.withUnit(statData.baseSalary, 2,"元"), modifier=Modifier.weight(1f))
                        statNumView(title = "加班收入", value =  MyDataTool.withUnit(statData.overSalary, 2,"元"), modifier=Modifier.weight(1f))
                        statNumView(title = "时薪收入", value = MyDataTool.withUnit(statData.fetchTotalSalary(),2, "元"), modifier=Modifier.weight(1f))
                    }
                }
                Blank()
                FullRow{
                    statNumView(title = "正班天数", value = MyDataTool.withUnit(statData.normalDay.toString(),0,  "天"), modifier=Modifier.weight(1f))
                    statNumView(title = "加班天数", value = MyDataTool.withUnit(statData.overDay.toString(), 0, "天"), modifier=Modifier.weight(1f))
                    statNumView(title = "出勤天数", value = MyDataTool.withUnit(statData.totalDay.toString(), 0, "天"), modifier=Modifier.weight(1f))
                }
                Blank()
                FullRow{
                    statNumView(title = "补贴金额", value = MyDataTool.withUnit(statData.awardMoney,2,  "元"), modifier=Modifier.weight(1f))
                    statNumView(title = "扣款金额", value = MyDataTool.withUnit(statData.fineMoney, 2, "元"), modifier=Modifier.weight(1f))
                    statNumView(title = "总收入", value = MyDataTool.withUnit(statData.fetchTotalMoney(), 2, "元"), modifier=Modifier.weight(1f))
                }
            }

            GroupTitleView(title = "薪水分布", tool = { /*TODO*/ }) {
                PieGraph(viewModel.gid, viewModel.salaryPieData)
            }

            GroupTitleView(title = "薪水工时分布", tool = { /*TODO*/ }) {
                PieGraph(viewModel.gid + 1, viewModel.salaryHourPieData)
            }

            GroupTitleView(title = "收入走势", horizonPadding = 10.dp, verticalPadding = 0.dp, tool = { /*TODO*/ }) {
                GraphLineView(monthStatData = viewModel.salaryLineGraphData)
                Blank()
            }

            GroupTitleView(title = "工时走势", horizonPadding = 10.dp, verticalPadding = 0.dp, tool = { /*TODO*/ }) {
                GraphLineView(monthStatData = viewModel.timeLineGraphData)
                Blank()
            }

            LongCancelButton("查看数据明细及导出") {
                toPage(navHostController,TimeworkRouter.TimeworkDetailData.route, bundleOf("model" to viewModel.realRangeDate().toJSONString()))
            }

        }

        LoadingDialog()
    }
}

@Composable
private fun statNumView(title: String, value: String, modifier: Modifier=Modifier){
    Column(modifier=modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color= Color.Gray, fontSize = 12.sp)
        Text(value, color= ThemeColor, fontSize = 15.sp, fontWeight = FontWeight. Medium)
    }
}