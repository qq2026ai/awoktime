package cn.jianyun.worktime.module.timework.views.home



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.Router
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
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.TipDialog
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderIcon
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.LinkText
import cn.jianyun.worktime.util.toVipPage
import kotlinx.coroutines.launch

@Composable
fun TimeworkHomeView(navHostController: NavHostController) {

    var viewModel = hiltViewModel<TimeworkMasterViewModel>()
    viewModel.tryReload("home")

    Column {

        Row(modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)  {

            MonthChooseView(value=viewModel.getCurrentDateStr(), fontSize = 18.sp, showNav = false, onChange = {
                viewModel.currentDate = it.parseDate()
                viewModel.doChange()
            })

            Row(verticalAlignment = Alignment.CenterVertically){
                if(!viewModel.baseRepository.isVip() && viewModel.baseRepository.appTipInfo.showPurchase){
                    LinkText("去广告", fontSize = 12.sp){
                        toVipPage(navHostController)
                    }
                }

                HeaderIcon(icon = IconFont.batchAdd){
                    navHostController.navigate(Router.BatchAdd.route)
                }
                HeaderIcon(icon = IconFont.box_none){
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

            if(viewModel.baseRepository.registDay < 1){
                GroupView(modifier = Modifier.clickable {
                    viewModel.baseRepository.openUrl("https://v.douyin.com/iP3RxkbP/")
                }) {
                    Text("观看新手视频教程，带你高效记工时", fontSize = 12.sp, color = ThemeColor)
                }
            }

            if(viewModel.baseRepository.appTipInfo.isShow && viewModel.baseRepository.showTip){
                GroupView(modifier=Modifier.clickable {
                    var type = viewModel.baseRepository.appTipInfo.newPage
                    if(type == "web" && viewModel.baseRepository.appTipInfo.url != ""){
                        viewModel.baseRepository.openUrl(viewModel.baseRepository.appTipInfo.url)
                    }
                    else{

                    }
                }) {
                    Box(modifier=Modifier.fillMaxWidth()) {
                        Text("温馨提示:${viewModel.baseRepository.appTipInfo.message}",
                            modifier=Modifier.padding(end = 10.dp),
                            fontSize = 12.sp, color = viewModel.baseRepository.appTipInfo.showColor())
                        Text("×", fontSize = 16.sp, color= Color.Gray, modifier=Modifier.align(Alignment.TopEnd).tap{
                            viewModel.baseRepository.appTipInfo.isShow = false
                            viewModel.baseRepository.showTip = false
                            viewModel.viewModelScope.launch {
                                viewModel.baseRepository.cacheBoolean(viewModel.baseRepository.appTipInfo.uid(), true)
                            }
                        })
                    }
                }
            }

//            if(viewModel.baseRepository.readVersion < viewModel.baseRepository.curVersion){
//                GroupView(modifier=Modifier.clickable {
//                    viewModel.formType = FormType(viewModel.baseRepository.curVersion)
//                }) {
//                    Text("新版本功能说明", fontSize = 12.sp, color = ThemeColor)
//                }
//            }

            TimeworkHeaderStatView(viewModel)

            Blank()
            TimeworkCalendarView(viewModel)

            TimeworkDataListView(viewModel = viewModel)
            if(viewModel.baseRepository.registDay < 3){
                GroupView {
                    Text("提示：点击已打卡记录可以修改或删除", fontSize = 12.sp, color = ThemeColor, modifier = Modifier.clickable {

                    })
                }
            }

            TwoColumnView {
                VerticalRow(modifier = Modifier.clickable{
                    navHostController.navigate(TimeworkRouter.TimeworkDefaultManage.route)
                }) {
                    Text("快捷打卡", fontSize = 14.sp)
                    IconView(icon = IconFont.add)
                }

                Text("了解快捷打卡?", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp, modifier = Modifier.clickable {
                    viewModel.formType = FormType("showDefaultSign")
                })
            }
            if(!viewModel.defaultConfigs.isEmpty()){
                Blank()
                FlowTagView(options = viewModel.defaultConfigs.map{it.toSelect()}, big = true, onClick = {
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
                MakeSignView(viewModel = viewModel, navHostController = navHostController)
            }
            if(viewModel.formType.isForm("award")) {
                MakeAwardView(viewModel = viewModel, navHostController)
            }
            if(viewModel.formType.isForm("rest")) {
                MakeRestView(viewModel = viewModel, navHostController = navHostController)
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

            if(viewModel.formType.isForm("showDefaultSign")){
                TipDialog(title = "了解快捷打卡", message = "提前设置常用打卡记录，后续一键完成打卡，省去重复填写的麻烦") {
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

            if(viewModel.formType.isForm(viewModel.baseRepository.curVersion)){
                TipDialog(title = "更新说明", message = "1.支持分享和导入工时\n2.增加坚果云绑定说明\n3.日结金额支持小数点\n4.日结支持选择时长\n5.时长支持选择到每一分钟\n6.支持显示0薪水工时\n7.修复薪水计算不准确问题\n" +
                        "8.支持选择24小时\n注意：首页右上角增加若干个性化设置") {
                    viewModel.formType = FormType()
                    viewModel.baseRepository.readVersion = viewModel.baseRepository.curVersion
                    viewModel.viewModelScope.launch {
                        viewModel.baseRepository.cache("readVersion", viewModel.baseRepository.curVersion)
                    }
                }
            }
        }
    }

}