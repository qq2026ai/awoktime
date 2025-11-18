package cn.jianyun.worktime.module.timework.views.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.BuildConfig
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.main.setting.user.LoginDialog
import cn.jianyun.worktime.main.setting.vip.VipView
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkAppConfigViewModel
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LinkItemView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.SettingGroupView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.form.TipDialog
import cn.jianyun.worktime.ui.component.model.kt.RangeDate
import cn.jianyun.worktime.ui.component.nav.AppLogoView
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.GlobalSecretCheckView
import cn.jianyun.worktime.ui.component.nav.HeaderTitle
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.SecretCheckView
import cn.jianyun.worktime.ui.component.nav.SelfHeaderView
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.ui.theme.VipColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.toPage
import cn.jianyun.worktime.util.toVipPage
import cn.jianyun.worktime.vm.AppSettingViewModel
import com.alibaba.fastjson2.toJSONString
import kotlinx.coroutines.launch
import java.util.Date


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkSettingView(navHostController: NavHostController, activity: Activity) {


    var appViewModel = hiltViewModel<AppSettingViewModel>()
    var viewModel = hiltViewModel<TimeworkMasterViewModel>()
    var viewModel2 = hiltViewModel<TimeworkAppConfigViewModel>()
    viewModel2.tryReload()


    Column{
        SelfHeaderView {
            HeaderTitle(title = "工时设置")
        }

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
        ) {

            SettingGroupView(modifier = Modifier.clickable {
                if(appViewModel.isLogin()){
                    navHostController.navigate(Router.UserDetail.route)
                }
                else{
                    appViewModel.formType = FormType("login")
                }
            }) {
                if(appViewModel.baseRepository.isLogin()){
                    VerticalRow(vpadding = 10.dp) {
                        AppLogoView(size=40.dp)
                        Blank()
                        Column {
                            Text(appViewModel.baseRepository.loginUser.showName(), fontSize = 14.sp, lineHeight = 13.sp)
                            Blank(2.dp)
                            Row {
                                TagView(tag = ifv(appViewModel.isVip(), appViewModel.loginUser.vipName, "普通用户"), color= ifv(appViewModel.isVip(), VipColor, androidx.compose.ui.graphics.Color.Gray), hollow = true)
                                if(appViewModel.isVip()){
                                    Blank(2.dp)
                                    TagView(tag = "有效期:" + appViewModel.loginUser.vipDate, color= VipColor, hollow = true)
                                }
                            }
                        }
                    }
                }
                else{
                    CenterRow(padding=10.dp) {
                        Text("登录或注册账号")
                    }
                }

                if(!appViewModel.baseRepository.isVip() && appViewModel.baseRepository.appTipInfo.discount){
                    Blank()
                    GroupView(modifier=Modifier.clickable {
                        var type = appViewModel.baseRepository.appTipInfo.newPage
                        if(type == "web" && appViewModel.baseRepository.appTipInfo.url != ""){
                            appViewModel.baseRepository.openUrl(appViewModel.baseRepository.appTipInfo.url)
                        }
                    }) {
                        Text("温馨提示:${appViewModel.baseRepository.appTipInfo.message}",
                            modifier=Modifier.padding(end = 10.dp),
                            fontSize = 12.sp, color = appViewModel.baseRepository.appTipInfo.showColor())
                    }
                }
            }

            if(!appViewModel.baseRepository.isVip() && appViewModel.baseRepository.isLogin()){
                SettingGroupView(modifier=Modifier.clickable {
                    if(!appViewModel.loginUser.isLogin()) {
                        appViewModel.formType = FormType("login")
                    }
                    else{
                        appViewModel.formType = FormType("vip")
                    }
                }) {
                    Text("购买会员，获取更多APP使用特权，您的支持能够让极简记工时更好地发展下去", fontSize= 13.sp, color= ThemeColor, modifier= Modifier.padding(6.dp))
                }
            }
            else{

            }

            SettingGroupView {
                LinkItemView(label = "工时项目管理") {
                    navHostController.navigate(TimeworkRouter.TimeworkProjectManage.route)
                }
                LinkItemView(label = "快捷打卡管理") {
                    navHostController.navigate(TimeworkRouter.TimeworkDefaultManage.route)
                }
                LinkItemView(label = "日历显示设置") {
                    navHostController.navigate(TimeworkRouter.TimeworkAppStyle.route)
                }
//                SelectItemView(label = "设置考勤周期", value = viewModel2.editItem.statDay, onValueChange = {
//                    viewModel2.editItem.statDay = it
//                    viewModel2.justSave()
//                }, options = SelectUtil.getFromDays())
            }

            LeadingHintView("个性化")
            SettingGroupView {
                SelectItemView(label = "APP显示风格", value = appViewModel.appConfig.appTheme, onValueChange = {
                    appViewModel.appConfig = appViewModel.appConfig.copy(appTheme = it)
                    appViewModel.makeChanged()
                }, options = SelectUtil.THEMES)


                SwitchItemView(label = "打卡精确到每分钟", value = viewModel.appConfig.needEveryMinute, onValueChange = {
                    viewModel.appConfig = viewModel.appConfig.copy(needEveryMinute = it)
                    viewModel.viewModelScope.launch {
                        viewModel.appConfigDao.set(viewModel.appConfig.toConfig())
                    }
                })

                SwitchItemView(label = "是否需要秘钥", value = appViewModel.appConfig.needSecret, onValueChange = {
                    if(!it){
                        //校验后方可执行
                        if(appViewModel.appConfig.secret != ""){
                            appViewModel.formType = FormType("closeSecret")
                        }
                    }
                    else{
                        if(!appViewModel.baseRepository.isVip()){
                            toVipPage(navHostController)
                            return@SwitchItemView
                        }
                        appViewModel.appConfig = appViewModel.appConfig.copy(needSecret = it)
                        appViewModel.makeChanged()
                    }
                })
                if(appViewModel.appConfig.needSecret) {
                    LinkItemView(label = ifv(appViewModel.appConfig.secret == "", "设置秘钥", "更新秘钥")) {
                        appViewModel.formType = FormType(ifv(appViewModel.appConfig.secret == "", "set", "reset"))
                    }
                }

                LinkItemView(label = "本地备份设置") {
                    navHostController.navigate(Router.LocalBackup.route)
                }
//
                LinkItemView(label = "数据备份及恢复") {
                    navHostController.navigate(TimeworkRouter.TimeworkCloudManage.route)
                }

                LinkItemView(label = "分享和导入工时") {
                    navHostController.navigate(TimeworkRouter.TimeworkShare.route)
                }
//                LinkItemView(label = "通知权限设置") {
//                    navHostController.navigate(Router.NotifySetting.route)
//                }
            }


            Blank()
//            SettingGroupView {
//                SwitchItemView(label = "开启打卡提醒", value = viewModel2.editItem.showNotice, onValueChange = {
//                    viewModel2.editItem.showNotice = it
//                    viewModel2.justSave()
//                })
//                if(viewModel2.editItem.showNotice){
//                    MultiSelectItemView(label = "哪些天提醒", columnCount = 2, maxCount = 0, value = viewModel2.editItem.noticeDays, onValueChange = {
//                        viewModel2.editItem.noticeDays = it
//                        viewModel2.justSave()
//                    }, options = SelectUtil.WEEKDAYS)
//
//                    MultiSelectItemView(label = "哪些时间提醒", columnCount = 2, value = viewModel2.editItem.noticeTimes, onValueChange = {
//                        viewModel2.editItem.noticeTimes = it
//                        viewModel2.justSave()
//                    }, options = SelectUtil.initWithUnit("", ":00", 0, 23))
//                }
//            }
//            Blank(10.dp)

            LeadingHintView("辅助工具")
            SettingGroupView {

                LinkItemView(label = "批量设置工时") {
                    navHostController.navigate(Router.BatchAdd.route)
                }
                LinkItemView(label = "导出工时数据") {


                    var tempRangeDate = RangeDate()
                    tempRangeDate.beginDate = MyDateTool.getStartDayStringOfMonth(Date())
                    tempRangeDate.endDate = MyDateTool.getLastDayStringOfMonth(Date())

                    toPage(navHostController,TimeworkRouter.TimeworkDetailData.route, bundleOf("model" to tempRangeDate.toJSONString()))
                }
//                LinkItemView(label = "批量删除数据") {
//                    navHostController.navigate(Router.Batch.route)
//                }
                LinkItemView(label = "导入小程序工时") {
                    navHostController.navigate(Router.ImportData.route)
                }
            }

            LeadingHintView("关于我们")
            SettingGroupView {
                LinkItemView(label = "常见问题解答") {
                    navHostController.navigate(Router.Question.route)
                }

                LinkItemView(label = "用户在线反馈") {
                    viewModel.baseRepository.openUrl("https://support.qq.com/products/328258")
                }
                LinkItemView(label = "用户使用协议") {
                    navHostController.navigate(Router.UserPolicy.route)
                }
                LinkItemView(label = "应用隐私政策") {
                    navHostController.navigate(Router.PrivatePolicy.route)
                }
                if(viewModel.baseRepository.isRealVip()){
                    LinkItemView(label = "进入会员QQ群") {
                        viewModel.baseRepository.copyData("1037038247", true)
                    }
                }
//                LinkItemView(label = "当前版本更新内容") {
//                    viewModel.formType = FormType("upgradeInfo")
//                }

//                LinkItemView(label = "清空所有数据") {
//                    viewModel.clearAll()
//                }
            }

//            if(BuildConfig.IS_DEV) {
//                Text("${viewModel.baseRepository.appTipInfo}")
//                Text("${viewModel.baseRepository.registDay}天", modifier=Modifier.clickable {
//                   viewModel.baseRepository.registDay += 1
//
//                   viewModel.viewModelScope.launch {
//                       viewModel.baseRepository.cacheLong("registDay", System.currentTimeMillis() - 1000L * 32 * 24 * 3600L)
//                   }
//               })
//            }

        }


        if(appViewModel.formType.isForm("set") || appViewModel.formType.isForm("check") || appViewModel.formType.isForm("reset")) {
            BottomDialogView(title = "", height = 0.dp, cancelable = false, onDismiss = {
                appViewModel.formType = FormType()
            }) {
                SecretCheckView(type = appViewModel.formType.type, dismiss = {
                    appViewModel.formType = FormType()
                }){
                    appViewModel.formType = FormType()
                }
            }
        }

        if(appViewModel.formType.isForm("closeSecret")){
            GlobalSecretCheckView(dismiss = {
                appViewModel.formType = FormType()
            }){
                appViewModel.appConfig = appViewModel.appConfig.copy(needSecret = false)
                appViewModel.makeChanged()
                appViewModel.formType = FormType()
            }
        }

        if(appViewModel.formType.isForm("checkSecret")){
            GlobalSecretCheckView(dismiss = {
                appViewModel.formType = FormType()
            }){
                appViewModel.appConfig = appViewModel.appConfig.copy(needSecretApps = viewModel.formType.editItem as String)
                appViewModel.makeChanged()
                appViewModel.formType = FormType()
            }
        }

        if(appViewModel.formType.isForm("login")){
            LoginDialog(appViewModel)
        }

        if(appViewModel.formType.isForm("vip")){
            VipView(appViewModel, activity)
        }

        if(viewModel.formType.isForm("upgradeInfo")){
            TipDialog(title = "更新说明", message = "1.支持分享和导入工时\n2.增加坚果云绑定说明\n3.日结金额支持小数点\n4.日结支持选择时长\n5.时长支持选择到每一分钟\n6.支持显示0薪水工时\n7.修复薪水计算不准确问题\n" +
                    "8.支持选择24小时\n\n(首页右上角增加若干个性化设置，请注意查看）") {
                viewModel.formType = FormType()
            }
        }

        LoadingDialog()
    }

}