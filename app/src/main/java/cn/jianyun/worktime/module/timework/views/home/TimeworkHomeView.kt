package cn.jianyun.worktime.module.timework.views.home



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.views.biz.TimeworkCalendarView
import cn.jianyun.worktime.module.timework.views.style.BottomTimeworkConfigView
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.nav.MonthChooseView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.parseDate
import cn.jianyun.worktime.ui.component.form.CancelButton
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.TipDialog
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderIcon
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.SelfHeaderView
import cn.jianyun.worktime.ui.component.nav.SubAppHeaderView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import kotlinx.coroutines.launch

@Composable
fun TimeworkHomeView(navHostController: NavHostController) {

    var viewModel = hiltViewModel<TimeworkMasterViewModel>()
    viewModel.tryReload()

    Column {

//        SelfHeaderView(rightTool = {
//            HeaderIcon(icon = IconFont.settings){
//                viewModel.formType = FormType(type= "config")
//            }
//        }) {
//            Text(viewModel.currentProjectName, modifier=Modifier.tap {
//                viewModel.formType = FormType("chooseProject")
//            })
//        }
//

        Row(modifier = Modifier.height(48.dp).fillMaxWidth()
            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)  {

            MonthChooseView(value=viewModel.getCurrentDateStr(), fontSize = 18.sp, showNav = false, onChange = {
                viewModel.currentDate = it.parseDate()
                viewModel.doChange()
            })

            Row{
                HeaderIcon(icon = IconFont.list){
                    viewModel.formType = FormType("chooseProject")
                }
                HeaderIcon(icon = IconFont.settings){
                    viewModel.formType = FormType(type= "config")
                }
            }

        }


        Column(modifier= Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)) {

            TimeworkHeaderStatView(viewModel)

            Blank()
            TimeworkCalendarView(viewModel)

            TimeworkDataListView(viewModel = viewModel)

            TwoColumnView {
                VerticalRow(modifier = Modifier.clickable{
                    navHostController.navigate(TimeworkRouter.TimeworkDefaultManage.route)
                }) {
                    Text("快捷打卡", fontSize = 12.sp)
                    IconView(icon = IconFont.add)
                }
            }
            if(!viewModel.defaultConfigs.isEmpty()){
                Blank()
                FlowTagView(options = viewModel.defaultConfigs.map{it.toSelect()}, onClick = {
                    viewModel.makeDefaultSign(it)
                })
            }
            Blank()
            LongOkButton("${viewModel.getCurrentDateStr()}工时打卡") {
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
                CancelButton("补贴", modifier=Modifier.weight(1f)) {
                    if(viewModel.awards.filter{it.type == "award"}.isEmpty()){
                        viewModel.toast("请先去添加一个补贴项再来打卡")
                    }
                    else{
                        viewModel.editAwardItem = TimeworkAwardData(awardType = "award")
                        viewModel.formType = FormType("award")
                    }
                }
                Blank()
                CancelButton("扣款", modifier=Modifier.weight(1f)) {
                    if(viewModel.awards.filter{it.type == "fine"}.isEmpty()){
                        viewModel.toast("请先去添加一个扣款项再来打卡")
                    }
                    else{
                        viewModel.editAwardItem = TimeworkAwardData(awardType = "fine")
                        viewModel.formType = FormType("award")
                    }
                }
                Blank()
                CancelButton("请假", modifier=Modifier.weight(1f)) {
                    viewModel.editWorkItem = TimeworkData(mode = "leave")
                    viewModel.formType = FormType("rest")
                }
                Blank()
                CancelButton("休息", modifier=Modifier.weight(1f)) {
                    viewModel.editWorkItem = TimeworkData(mode = "rest")
                    viewModel.formType = FormType("rest")
                }
            }

            if(viewModel.formType.isForm("sign")) {
                MakeSignView(viewModel = viewModel)
            }
            if(viewModel.formType.isForm("award")) {
                MakeAwardView(viewModel = viewModel, navHostController)
            }
            if(viewModel.formType.isForm("rest")) {
                MakeRestView(viewModel = viewModel)
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

//            LoadingDialog()

            if(viewModel.formType.isForm("chooseProject")) {
                TimeworkProjectChooseView(navHostController, viewModel)
            }

            if(viewModel.formType.isForm("upgrade")) {
                if(!viewModel.baseRepository.isNetworkAvailable()) {
                    TipDialog(title = "网络断开", message = "您当前APP网络已断开，可能会影响正常使用, 快去开启手机网络吧!") {
                        if(viewModel.baseRepository.isNetworkAvailable()){
                            viewModel.formType = FormType()
                            viewModel.viewModelScope.launch {
                                var upgrade = viewModel.baseRepository.tryUpgrade()
                                if(upgrade){
                                    viewModel.formType = FormType("upgrade")
                                }
                            }
                        }
                        else{
                            viewModel.baseRepository.toast("快去开启手机网络吧")
                        }
                    }
                }
                else{
                    TipDialog(title = "升级提醒", message = "您当前版本过低，请前往手机应用市场升级") {
                        viewModel.baseRepository.toast("快去应用市场更新吧")
                    }
                }
            }
        }
    }

}