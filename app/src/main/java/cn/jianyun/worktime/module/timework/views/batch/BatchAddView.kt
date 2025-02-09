package cn.jianyun.worktime.module.timework.views.batch


import android.provider.Settings.Panel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.views.biz.TimeworkCalendarView
import cn.jianyun.worktime.module.timework.views.home.MakeAwardView
import cn.jianyun.worktime.module.timework.views.home.MakeRestView
import cn.jianyun.worktime.module.timework.views.home.MakeSignView
import cn.jianyun.worktime.module.timework.views.home.TimeworkDataListView
import cn.jianyun.worktime.module.timework.views.home.TimeworkHeaderStatView
import cn.jianyun.worktime.module.timework.views.home.TimeworkProjectChooseView
import cn.jianyun.worktime.module.timework.views.style.BottomTimeworkConfigView
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.form.CancelButton
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.LongDeleteButton
import cn.jianyun.worktime.ui.component.form.LongOk2Button
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.component.nav.PanelView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.oneLine

@Composable
fun BatchAddView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkMasterViewModel>()
    LaunchedEffect(Unit){
        viewModel.multiMode = true
    }
    viewModel.tryReload("batchAdd")

    PageView(navHostController = navHostController, title = "批量设置工时") {

        Column {
            
            CenterRow(padding = 10.dp, paddingBottom = 10.dp) {
                SegmentPickerView(value=viewModel.editMode, options = SelectUtil.EDIT_TYPES, onChange = {
                    viewModel.editMode = it
                })
            }

            TimeworkHeaderStatView(viewModel)

            Blank()
            TimeworkCalendarView(viewModel)

            if(viewModel.editMode == "check"){
                TimeworkDataListView(viewModel = viewModel)
            }
            else{
                LeadingHintView("快速选择(基于当前月)")
                FlowTagView(options = SelectUtil.initValues("全选", "all", "反选", "reverse", "仅工作日","work",  "仅周末", "week", "仅周六", "weekday7", "仅周日", "weekday1", "清空所有", "clear"),singleLine = true, onClick = {
                    viewModel.batchChoose(it)
                })
            }

            Blank()

            TwoColumnView {
                VerticalRow(modifier = Modifier.clickable{
                    navHostController.navigate(TimeworkRouter.TimeworkDefaultManage.route)
                }) {
                    Text("快捷打卡", fontSize = 12.sp, color= Color.Gray)
                    IconView(icon = IconFont.add, color = Color.Gray)
                }
            }
            if(viewModel.defaultConfigs.isNotEmpty()){
                Blank()
                FlowTagView(options = viewModel.defaultConfigs.map{it.toSelect()}, onClick = {
                    viewModel.makeDefaultSign(it)
                })
            }
            Blank()
            LongOkButton("批量工时打卡") {
                if(viewModel.salarys.isEmpty()) {
                    viewModel.toast("请先去添加薪水再来打卡")
                }
                else{
                    viewModel.editWorkItem = TimeworkData(mode = "hour")
                    viewModel.formType = FormType("sign")
                }
            }
            Blank()
            TwoColumnView {
                CancelButton("补贴", modifier= Modifier.weight(1f)) {
                    if(viewModel.awards.filter{it.type == "award"}.isEmpty()){
                        viewModel.toast("请先去添加一个补贴项再来打卡")
                    }
                    else{
                        if(!viewModel.hasEmptyChooseDates()) {
                            viewModel.editAwardItem = TimeworkAwardData(awardType = "award")
                            viewModel.formType = FormType("award")
                        }
                    }
                }
                Blank()
                CancelButton("扣款", modifier= Modifier.weight(1f)) {
                    if(viewModel.awards.filter{it.type == "fine"}.isEmpty()){
                        viewModel.toast("请先去添加一个扣款项再来打卡")
                    }
                    else{
                        if(!viewModel.hasEmptyChooseDates()){
                            viewModel.editAwardItem = TimeworkAwardData(awardType = "fine")
                            viewModel.formType = FormType("award")
                        }

                    }
                }
                Blank()
                CancelButton("请假", modifier= Modifier.weight(1f)) {
                    if(!viewModel.hasEmptyChooseDates()){
                        viewModel.editWorkItem = TimeworkData(mode = "leave")
                        viewModel.formType = FormType("rest")
                    }

                }
                Blank()
                CancelButton("休息", modifier= Modifier.weight(1f)) {
                    if(!viewModel.hasEmptyChooseDates()){
                        viewModel.editWorkItem = TimeworkData(mode = "rest")
                        viewModel.formType = FormType("rest")
                    }
                }
            }
            Blank()


            if(viewModel.isMultiEditMode()){

                if(viewModel.chooseDates.isNotEmpty()){
                    LeadingHintView("批量操作")
                    LongOk2Button("批量删除打卡记录") {
                        viewModel.deleteType = FormType("batchDelete")
                    }
                }

                LeadingHintView("个性化设置")
                GroupView(verticalPadding = 0.dp){
                    SwitchItemView(label = "打卡完成后清空选中日期", value = viewModel.clearMode, onValueChange = {
                        viewModel.clearMode = it
                    })
                }
                LeadingHintView("批量设置问与答")


                PanelView(title="什么是批量设置工时?", """
                        如果你希望同时对不同日期有相同的工时记录进行一次性设置，就可以利用该特性来提升效率了。<br>
                        操作步骤如下：<br>
                        1.先批量选择具有相同工时的日期<br>
                        2.然后点击「批量工时打卡」按钮（或者直接点击创建好的快捷打卡）<br>
                        我们也支持批量设置补贴和扣款，甚至请假和休息，原理类似
                    """.oneLine())

                PanelView(title="可以批量替换吗？", """
                        您可以先执行批量删除，然后进行批量工时打卡
                    """.oneLine())

                PanelView(title="批量模式切换月份，之前选择的日期还会保留吗？", """
                        我们只支持在一个月内进行批量设置，所以在切换月份过后，之前选择的日期会被清空
                    """.oneLine())
            }

            Blank(40.dp)

            if(viewModel.formType.isForm("sign")) {
                MakeSignView(viewModel = viewModel)
            }
            if(viewModel.formType.isForm("award")) {
                MakeAwardView(viewModel = viewModel, navHostController)
            }
            if(viewModel.formType.isForm("rest")) {
                MakeRestView(viewModel = viewModel)
            }


            if(viewModel.deleteType.isForm("batchDelete")){
                DeleteDialog(title= "批量删除有风险，确定要继续吗？", okAction = {
                    viewModel.doBatchDelete()
                }) {
                    viewModel.deleteType = FormType()
                }
            }

            if(viewModel.deleteType.isForm("deleteSign")) {
                DeleteDialog(okAction = {
                    viewModel.doDeleteSign()
                }) {
                    viewModel.deleteType = FormType()
                }
            }
            if(viewModel.deleteType.isForm("deleteAward")) {
                DeleteDialog(okAction = {
                    viewModel.doDeleteAward()
                }) {
                    viewModel.deleteType = FormType()
                }
            }

            if(viewModel.deleteType.isForm("sameSign")) {
                DeleteDialog("今天貌似已经打过卡了，确定要再打一次卡？", okAction = {
                    viewModel.makeSign2()
                }) {
                    viewModel.deleteType = FormType()
                }
            }
            if(viewModel.deleteType.isForm("sameAward")) {
                DeleteDialog("今天貌似已经打过卡了，确定要再打一次卡？", okAction = {
                    viewModel.makeAward2()
                }) {
                    viewModel.deleteType = FormType()
                }
            }

            if(viewModel.formType.isForm("config")) {
                BottomTimeworkConfigView{
                    viewModel.formType = FormType()
                }
            }

            LoadingDialog()

            if(viewModel.formType.isForm("chooseProject")) {
                TimeworkProjectChooseView(navHostController, viewModel)
            }

        }



    }

}