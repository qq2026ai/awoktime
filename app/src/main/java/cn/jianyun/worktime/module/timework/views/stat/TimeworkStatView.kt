package cn.jianyun.worktime.module.timework.views.stat



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.module.timework.dto.TimeworkSettleSummaryData
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkStatViewModel
import cn.jianyun.worktime.ui.component.form.GroupTitleView
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.nav.CenterColumn
import cn.jianyun.worktime.ui.component.nav.DateRangeChooseView
import cn.jianyun.worktime.ui.component.nav.FullRow
import cn.jianyun.worktime.ui.component.nav.MonthChooseView
import cn.jianyun.worktime.ui.component.nav.YearChooseView
import cn.jianyun.worktime.ui.graph.GraphLineView
import cn.jianyun.worktime.ui.graph.PieGraph
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.toPage
import com.alibaba.fastjson2.toJSONString

@Composable
fun TimeworkStatView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkStatViewModel>()
    val reloadSid = viewModel.baseRepository.sid

    LaunchedEffect(reloadSid){
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
                MonthChooseView(value=viewModel.getCurrentDateStr(), beginDay = viewModel.getMonthPickerBeginDay())  {
                    viewModel.changeCurrentPeriod(MyDateTool.parseDateString(it))
                    viewModel.reloadData()
                }
                if(viewModel.shouldShowMonthRangeHint()){
                    Text(
                        text = viewModel.getMonthRangeHint(),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
                Blank()
                FullRow{
                    statNumView(title = "待结算", value = MyDataTool.withUnit(statData.unSettledMoney, 2, "元"), modifier=Modifier.weight(1f))
                    statNumView(title = "已结算", value = MyDataTool.withUnit(statData.settledMoney, 2, "元"), modifier=Modifier.weight(1f))
                    statNumView(title = "", value = "", modifier=Modifier.weight(1f))
                }
            }

            GroupTitleView(title = "薪水分布", tool = { /*TODO*/ }) {
                PieGraph(viewModel.gid, viewModel.salaryPieData)
            }

            GroupTitleView(title = "薪水工时分布", tool = { /*TODO*/ }) {
                PieGraph(viewModel.gid + 1, viewModel.salaryHourPieData)
            }

            GroupTitleView(title = "结算汇总", horizonPadding = 10.dp, verticalPadding = 6.dp, tool = { /*TODO*/ }) {
                if(viewModel.settleSummaryList.isEmpty()){
                    Text("当前区间暂无可结算数据", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(10.dp))
                }
                else{
                    Column {
                        viewModel.settleSummaryList.forEachIndexed { index, item ->
                            SettleSummaryItemView(
                                item = item,
                                showDivider = index != viewModel.settleSummaryList.lastIndex
                            )
                        }
                    }
                }
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

@Composable
private fun SettleSummaryItemView(item: TimeworkSettleSummaryData, showDivider: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.percentText(),
                color = ThemeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ThemeColor.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Blank(4.dp)

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "已${item.showMoney(item.settledMoney)}",
                    color = ThemeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Blank(8.dp)
                Text(
                    text = "待${item.showMoney(item.unSettledMoney())}",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            Text(
                text = "${item.settledCount}/${item.totalCount}次",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        if(showDivider){
            Blank(6.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}
