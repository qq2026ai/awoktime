package cn.jianyun.worktime.module.timework.views.tool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.vm.TimeworkBatchSettleViewModel
import cn.jianyun.worktime.ui.component.form.CancelButton
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.GroupTitleView
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.OkButton
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.nav.DateRangeChooseView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.component.nav.WithUnitView
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.goBack

@Composable
fun TimeworkBatchSettleView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkBatchSettleViewModel>()
    val reloadSid = viewModel.baseRepository.sid

    LaunchedEffect(reloadSid) {
        if(viewModel.inited){
            viewModel.tryReload("batchSettle")
        }
        else{
            viewModel.reload()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)
    ) {
        HeaderView(title = "批量结算工时和补扣", backAction = {
            goBack(navHostController)
        }, rightTool = {
            Text("帮助", color = ThemeColor, modifier = Modifier.clickable {
                viewModel.formType = FormType("tip")
            })
        })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp)
            ) {
            item {
                GroupTitleView(title = "筛选条件", horizonPadding = 12.dp) {
                    BatchSettleFilterRow(label = "结算区间") {
                        DateRangeChooseView(value = viewModel.rangeDate) {
                            viewModel.rangeDate = it
                            viewModel.reloadData()
                        }
                    }

                    BatchSettleFilterRow(label = "打卡类型") {
                        SegmentPickerView(
                            value = viewModel.settleType,
                            width = 70.dp,
                            options = SelectUtil.initValues("全部", "all", "工时", "work", "补扣", "award"),
                            onChange = {
                                viewModel.settleType = it
                                if(it == "award"){
                                    viewModel.salaryUuid = ""
                                }
                                viewModel.reloadData()
                            }
                        )
                    }

                    if(viewModel.settleType != "award"){
                        BatchSettleFilterRow(label = "选择薪水") {
                            BatchSettleSalaryPicker(
                                readonly = false,
                                value = viewModel.salaryUuid,
                                onValueChange = {
                                    viewModel.salaryUuid = it
                                    viewModel.reloadData()
                                },
                                options = viewModel.salaryOptions()
                            )
                        }
                    }

                    BatchSettleFilterRow(label = "结算状态") {
                        SegmentPickerView(
                            value = viewModel.settleStatus,
                            width = 70.dp,
                            options = SelectUtil.initValues("全部", "all", "待结算", "pending", "已结算", "settled"),
                            onChange = {
                                viewModel.settleStatus = it
                                viewModel.reloadData()
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CancelButton(label = "取消结算", modifier = Modifier.weight(1f)) {
                        viewModel.formType = FormType("cancelSettle")
                    }
                    OkButton(label = "全部结算", modifier = Modifier.weight(1f)) {
                        viewModel.doSettleAll(true)
                    }
                }

                if(viewModel.inited && viewModel.resultSize() == 0){
                    GroupTitleView(title = "查询结果") {
                        Text("当前筛选条件下暂无数据", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(10.dp))
                    }
                }
                else if(viewModel.resultSize() > 0){
                    Text("查询结果", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp, bottom = 6.dp))
                }
            }

            items(viewModel.resultDayList()) { day ->
                val workItems = viewModel.workListByDay(day)
                val awardItems = viewModel.awardListByDay(day)

                if(workItems.isNotEmpty() || awardItems.isNotEmpty()){
                    Column {
                        Text(day, color = Color.Gray, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

                        workItems.forEach {
                            BatchSettleWorkCard(viewModel, it)
                        }
                        awardItems.forEach {
                            BatchSettleAwardCard(viewModel, it)
                        }
                    }
                }
            }
            }
        }
    }

    LoadingDialog()

    if(viewModel.formType.isForm("tip")){
        cn.jianyun.worktime.ui.component.form.TipDialog(
            title = "使用帮助",
            message = "可以先按日期范围、工时或补扣类型、薪水条件筛选记录，再执行全部结算或取消结算。工时和补扣都会一起参与结算标记。"
        ) {
            viewModel.resetForm()
        }
    }

    if(viewModel.formType.isForm("cancelSettle")){
        DeleteDialog(title = "确定要取消当前筛选结果的结算标记吗？", okAction = {
            viewModel.doSettleAll(false)
            viewModel.resetForm()
        }) {
            viewModel.resetForm()
        }
    }
}

@Composable
private fun BatchSettleWorkCard(viewModel: TimeworkBatchSettleViewModel, item: TimeworkData) {
    GroupView(
        modifier = Modifier.clickable {
            viewModel.toggleWorkSettle(item)
        },
        horizonPadding = 0.dp,
        verticalPadding = 0.dp,
        bottom = 12.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, top = 18.dp, bottom = 14.dp)
            ) {
                TwoColumnView {
                    Text("工作时间", fontSize = 13.sp)
                    Text(viewModel.workTimeLabel(item), fontSize = 13.sp)
                }
                Blank(6.dp)
                TwoColumnView {
                    Text("工作时长", fontSize = 13.sp)
                    Text(viewModel.workDurationLabel(item), fontSize = 13.sp)
                }
                if(viewModel.workSalaryLabel(item) != ""){
                    Blank(6.dp)
                    TwoColumnView {
                        Text("薪水标准", fontSize = 13.sp)
                        Text(viewModel.workSalaryLabel(item), fontSize = 13.sp)
                    }
                }
                if(viewModel.workIncome(item) != ""){
                    Blank(6.dp)
                    TwoColumnView {
                        Text("收入", fontSize = 13.sp)
                        WithUnitView(text = viewModel.workIncome(item), unit = "元")
                    }
                }
                if(item.remark != ""){
                    Blank(6.dp)
                    Text("备注：${item.remark}", color = Color.Gray, fontSize = 12.sp)
                }
                Blank(6.dp)
                Text(
                    if (item.isSettled()) "点击取消结算" else "点击设为结算",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 12.sp
                )
            }
            if(item.isSettled()){
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                ) {
                    TagView(tag = "已结算", color = ThemeColor)
                }
            }
        }
    }
}

@Composable
private fun BatchSettleAwardCard(viewModel: TimeworkBatchSettleViewModel, item: TimeworkAwardData) {
    GroupView(
        modifier = Modifier.clickable {
            viewModel.toggleAwardSettle(item)
        },
        horizonPadding = 0.dp,
        verticalPadding = 0.dp,
        bottom = 12.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, top = 18.dp, bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerticalRow {
                        Text(item.awardName, fontSize = 14.sp)
                        Blank(4.dp)
                        TagView(tag = item.typeName(), color = item.typeColor())
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        WithUnitView(text = item.realAwardValue(), unit = "元")
                    }
                }
                if(item.remark != ""){
                    Blank(8.dp)
                    Text("备注：${item.remark}", color = Color.Gray, fontSize = 12.sp)
                }
                Blank(6.dp)
                Text(
                    if (item.isSettled()) "点击取消结算" else "点击设为结算",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 12.sp
                )
            }
            if(item.isSettled()){
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp)
                ) {
                    TagView(tag = "已结算", color = ThemeColor)
                }
            }
        }
    }
}

@Composable
private fun BatchSettleFilterRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .width(82.dp)
                .padding(end = 12.dp)
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            content()
        }
    }
}

@Composable
private fun BatchSettleSalaryPicker(
    readonly: Boolean,
    value: String,
    options: List<cn.jianyun.worktime.util.SelectDO>,
    onValueChange: (String) -> Unit
) {
    cn.jianyun.worktime.ui.component.form.SelectItemView(
        label = "",
        readonly = readonly,
        value = value,
        onValueChange = onValueChange,
        options = options
    )
}
