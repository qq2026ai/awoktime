package cn.jianyun.worktime.module.timework.views.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.nav.SwipeDeleteView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.component.nav.WithUnitView
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.ifv

@Composable
fun TimeworkDataListView(viewModel: TimeworkMasterViewModel, maskMoney: Boolean = false) {

    if(viewModel.workDataMap[viewModel.getFocusDateStr()] != null){
        viewModel.workDataMap[viewModel.getFocusDateStr()]!!.forEach{
            TimeworkDataItemView(viewModel, it, maskMoney)
        }
    }

    if(viewModel.awardDataMap[viewModel.getFocusDateStr()] != null){
        viewModel.awardDataMap[viewModel.getFocusDateStr()]!!.forEach{
            TimeworkAwardItemView(viewModel, it, maskMoney)
        }
    }

}

@Composable
fun TimeworkDataItemView(viewModel: TimeworkMasterViewModel, item: TimeworkData, maskMoney: Boolean = false) {
    SwipeDeleteView(sid=item.uuid, onEdit = {
        viewModel.editWorkItem = item
        if(item.mode == "leave" || item.mode == "rest"){
            viewModel.formType = FormType(type="rest")
        }
        else{
            viewModel.formType = FormType(type="sign")
        }
    }, onDelete = {
        viewModel.editWorkItem = item
        viewModel.doDeleteSign()
    }) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if(item.isSettled()){
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp)) {
                    TagView(tag = "已结算", color = ThemeColor)
                }
            }
            Column(modifier = Modifier.padding(top = if (item.isSettled()) 12.dp else 0.dp)){
                if(item.mode == "hour"){
                    Column(modifier=Modifier.padding(15.dp)) {
                        if(!item.onlyOver){
                            TwoColumnView {
                                Column {
                                    VerticalRow{
                                        Text("正班")
                                        Text(item.fetchBaseHourShownInfo())
                                    }
                                    SmallTipText(item.baseSalaryInfo)
                                }
                                WithUnitView(text = if(maskMoney) "**" else item.fetchBaseMoney(item.baseSalaryPrice), unit = if(maskMoney) "" else "元")
                            }
                            if(item.overTime){
                                Blank()
                            }
                        }
                        if(item.overTime){
                            TwoColumnView {
                                Column {
                                    VerticalRow{
                                        Text("加班")
                                        Text(item.fetchOverHourShownInfo())
                                    }
                                    SmallTipText(item.overSalaryInfo)
                                }
                                WithUnitView(text = if(maskMoney) "**" else item.fetchOverMoney(item.overSalaryPrice), unit = if(maskMoney) "" else "元")
                            }
                        }
                        if(item.remark != ""){
                            Blank()
                            SmallTipText("备注:" + item.remark)
                        }
                    }
                }
                if(item.mode == "day"){
                    TwoColumnView(padding = 15.dp) {
                        Column{
                            Column{
                                Text("日结打卡" + ifv(item.beginTime != "", ":" + item.fetchBaseHourShownInfo(), ""))
                                if(item.beginTime != ""){
                                    SmallTipText(item.beginTime + "~" + item.endTime)
                                }
                                if(item.remark != ""){
                                    SmallTipText("备注:" + item.remark)
                                }
                            }
                        }
                        WithUnitView(text = if(maskMoney) "**" else item.amount, unit = if(maskMoney) "" else "元")
                    }
                }
                if(item.mode == "time"){
                    TwoColumnView(padding = 15.dp) {
                        Column{
                            Text("时间打卡:" +  item.fetchBaseHourShownInfo() + "(" +  item.beginTime + "~" + item.endTime + ")")
                            SmallTipText(item.baseSalaryInfo)
                            if(item.remark != ""){
                                SmallTipText("备注:" + item.remark)
                            }
                        }
                        WithUnitView(text = if(maskMoney) "**" else item.fetchBaseMoney(item.baseSalaryPrice), unit = if(maskMoney) "" else "元")
                    }
                }
                if(item.mode == "leave"){
                    TwoColumnView(padding = 15.dp) {
                        Column{
                            Text("请假")
                            if(item.remark != ""){
                                SmallTipText("备注:" + item.remark)
                            }
                        }
                    }
                }
                if(item.mode == "rest"){
                    TwoColumnView(padding = 15.dp) {
                        Column{
                            Text("休息")
                            if(item.remark != ""){
                                SmallTipText("备注:" + item.remark)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun TimeworkAwardItemView(viewModel: TimeworkMasterViewModel, item: TimeworkAwardData, maskMoney: Boolean = false) {

    SwipeDeleteView(sid=item.uuid, onEdit = {
        viewModel.editAwardItem = item
        viewModel.formType = FormType(type="award")
    }, onDelete = {
        viewModel.editAwardItem = item
        viewModel.doDeleteAward()
    }) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if(item.isSettled()){
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp)) {
                    TagView(tag = "已结算", color = ThemeColor)
                }
            }
            TwoColumnView(padding=15.dp, modifier = Modifier.padding(top = if (item.isSettled()) 12.dp else 0.dp)) {
                Column{
                    VerticalRow {
                        Text(item.awardName)
                        Blank()
                        TagView(tag = item.typeName(), color = item.typeColor())
                    }
                    if(item.remark != ""){
                        SmallTipText("备注:" + item.remark)
                    }
                }
                WithUnitView(text = if(maskMoney) "**" else item.realAwardValue(), unit = if(maskMoney) "" else "元")
            }
        }
    }

}
