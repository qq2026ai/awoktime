package cn.jianyun.worktime.module.timework.views.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.main.setting.user.LoginDialog
import cn.jianyun.worktime.main.setting.vip.VipView
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.util.TimeworkVersionNote
import cn.jianyun.worktime.module.timework.vm.TimeworkAppConfigViewModel
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LinkItemView
import cn.jianyun.worktime.ui.component.form.LabelItemView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.MultiSelectItemView
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.SettingGroupView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.form.TimePickerItemView3
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
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.util.toPage
import cn.jianyun.worktime.util.toVipPage
import cn.jianyun.worktime.vm.AppSettingViewModel
import com.alibaba.fastjson2.toJSONString
import kotlinx.coroutines.launch
import java.util.Date

private val CALENDAR_PERMISSIONS = arrayOf(
    Manifest.permission.READ_CALENDAR,
    Manifest.permission.WRITE_CALENDAR
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkSettingView(navHostController: NavHostController, activity: Activity) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var appViewModel = hiltViewModel<AppSettingViewModel>()
    var viewModel = hiltViewModel<TimeworkMasterViewModel>()
    var viewModel2 = hiltViewModel<TimeworkAppConfigViewModel>()
    viewModel2.tryReload()
    var calendarPermissionGranted by remember {
        mutableStateOf(hasCalendarPermission(context))
    }
    var enableNoticeAfterGrant by remember {
        mutableStateOf(false)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        calendarPermissionGranted = granted
        if (granted) {
            if (enableNoticeAfterGrant) {
                viewModel2.editItem = viewModel2.editItem.copy(showNotice = true)
                viewModel2.justSave()
                viewModel2.baseRepository.toast("已开启打卡提醒")
            } else {
                viewModel2.baseRepository.toast("日历权限已授权")
            }
        } else if (enableNoticeAfterGrant) {
            viewModel2.baseRepository.toast("需要授予日历权限后才能开启打卡提醒")
        }
        enableNoticeAfterGrant = false
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                calendarPermissionGranted = hasCalendarPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


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

                if (appViewModel.baseRepository.shouldShowDiscountHint()) {
                    Blank()
                    GroupView(modifier = Modifier.clickable {
                        var type = appViewModel.baseRepository.appTipInfo.newPage
                        if (type == "web" && appViewModel.baseRepository.appTipInfo.url != "") {
                            appViewModel.baseRepository.openUrl(appViewModel.baseRepository.appTipInfo.url)
                        }
                    }) {
                        Text(
                            "温馨提示:${appViewModel.baseRepository.appTipInfo.message}",
                            modifier = Modifier.padding(end = 10.dp),
                            fontSize = 12.sp,
                            color = appViewModel.baseRepository.appTipInfo.showColor()
                        )
                    }
                }
            }

            if(appViewModel.baseRepository.shouldShowVipPromotion()){
                SettingGroupView(modifier=Modifier.clickable {
                    appViewModel.formType = FormType("vip")
                }) {
                    Text("购买会员，获取更多APP使用特权，您的支持会让极简记工时更好地发展下去", fontSize= 13.sp, color= ThemeColor, modifier= Modifier.padding(6.dp))
                }
            }

            SettingGroupView {
                LinkItemView(label = "工时项目管理") {
                    navHostController.navigate(TimeworkRouter.TimeworkProjectManage.route)
                }
                LinkItemView(label = "快捷打卡管理") {
                    navHostController.navigate(TimeworkRouter.TimeworkDefaultManage.route)
                }
                SelectItemView(label = "考勤周期", value = viewModel2.editItem.normalizedStatDay(), onValueChange = {
                    viewModel2.editItem = viewModel2.editItem.copy(statDay = it)
                    viewModel2.justSave()
                }, options = SelectUtil.getFromDays())
                LinkItemView(label = "日历显示设置") {
                    navHostController.navigate(TimeworkRouter.TimeworkAppStyle.route)
                }
            }

            LeadingHintView("个性化")
            SettingGroupView {
                SelectItemView(label = "APP显示风格", value = appViewModel.appConfig.appTheme, onValueChange = {
                    appViewModel.appConfig = appViewModel.appConfig.copy(appTheme = it)
                    appViewModel.makeChanged()
                }, options = SelectUtil.THEMES)

                LinkItemView(label = "APP主题色") {
                    navHostController.navigate(TimeworkRouter.TimeworkAppTheme.route)
                }

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
            LeadingHintView("辅助工具")
            SettingGroupView {

                LinkItemView(label = "批量设置工时") {
                    navHostController.navigate(Router.BatchAdd.route)
                }
                LinkItemView(label = "批量结算数据") {
                    navHostController.navigate(TimeworkRouter.TimeworkBatchSettle.route)
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
            Blank()
            TimeworkReminderGroup(
                viewModel = viewModel2,
                calendarPermissionGranted = calendarPermissionGranted,
                onEnableRequest = {
                    enableNoticeAfterGrant = true
                    permissionLauncher.launch(CALENDAR_PERMISSIONS)
                },
                onPermissionRequest = {
                    enableNoticeAfterGrant = false
                    permissionLauncher.launch(CALENDAR_PERMISSIONS)
                },
                onOpenPermissionSetting = {
                    openAppPermissionSetting(context)
                }
            )
            Blank(10.dp)

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
                LinkItemView(label = "新版本功能说明") {
                    viewModel.formType = FormType("upgradeInfo")
                }

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
            TipDialog(
                title = TimeworkVersionNote.TITLE,
                message = TimeworkVersionNote.MESSAGE,
                widthFraction = 0.94f
            ) {
                viewModel.formType = FormType()
            }
        }

        LoadingDialog()
    }

}

@Composable
private fun TimeworkReminderGroup(
    viewModel: TimeworkAppConfigViewModel,
    calendarPermissionGranted: Boolean,
    onEnableRequest: () -> Unit,
    onPermissionRequest: () -> Unit,
    onOpenPermissionSetting: () -> Unit
) {
    SettingGroupView {
        SwitchItemView(label = "开启打卡提醒", value = viewModel.editItem.showNotice, onValueChange = {
            if (it) {
                if (calendarPermissionGranted) {
                    viewModel.editItem = viewModel.editItem.copy(showNotice = true)
                    viewModel.justSave()
                } else {
                    onEnableRequest()
                }
            } else {
                viewModel.editItem = viewModel.editItem.copy(showNotice = false)
                viewModel.justSave()
            }
        })

        if (!calendarPermissionGranted) {
            GroupView(dialog = true, bottom = 0.dp, verticalPadding = 8.dp) {
                Text(
                    "打卡提醒会写入系统日历，需要先授权日历权限后才能生效",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Blank(6.dp)
                Row {
                    Text(
                        "申请权限",
                        color = ThemeColor,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            onPermissionRequest()
                        }
                    )
                    Blank(14.dp)
                    Text(
                        "打开系统设置",
                        color = ThemeColor,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            onOpenPermissionSetting()
                        }
                    )
                }
            }
        }

        if (viewModel.editItem.showNotice) {
            MultiSelectItemView(
                label = "提醒星期",
                columnCount = 2,
                maxCount = 0,
                value = viewModel.editItem.normalizedNoticeDays(),
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(noticeDays = it)
                    viewModel.justSave()
                },
                options = SelectUtil.WEEKDAYS
            )
            ReminderTimeEditor(viewModel)
            Text(
                "最多设置5个时间，提醒会自动写入未来7天的系统日历",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReminderTimeEditor(viewModel: TimeworkAppConfigViewModel) {
    val noticeTimes = viewModel.editItem.noticeTimeList()

    Column {
        if (noticeTimes.size < 5) {
            TimePickerItemView3(
                label = "提醒时间",
                minuteStep = 5,
                placeholder = "点击添加",
                value = "",
                onValueChange = { newTime ->
                    if (newTime == "") {
                        return@TimePickerItemView3
                    }
                    if (noticeTimes.contains(newTime)) {
                        viewModel.baseRepository.toast("这个提醒时间已经添加过了")
                        return@TimePickerItemView3
                    }
                    val nextTimes = (noticeTimes + newTime).sorted().joinToString("^")
                    viewModel.editItem = viewModel.editItem.copy(noticeTimes = nextTimes)
                    viewModel.justSave()
                }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabelItemView("提醒时间")
                Text("已达5个上限", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
            }
        }

        if (noticeTimes.isNotEmpty()) {
            Text(
                "已选时间",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 2.dp)
            )
            Blank(8.dp)
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                noticeTimes.forEach { one ->
                    Text(
                        text = "$one ×",
                        modifier = Modifier
                            .padding(end = 8.dp, bottom = 8.dp)
                            .radius(16.dp)
                            .clickable {
                                val nextTimes = noticeTimes.filter { it != one }.joinToString("^")
                                viewModel.editItem = viewModel.editItem.copy(noticeTimes = nextTimes)
                                viewModel.justSave()
                            }
                            .background(ThemeColor.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 12.sp,
                        color = ThemeColor
                    )
                }
            }
        } else {
            Text(
                "还没有设置提醒时间",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun hasCalendarPermission(context: Context): Boolean {
    val readGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CALENDAR
    ) == PackageManager.PERMISSION_GRANTED
    val writeGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_CALENDAR
    ) == PackageManager.PERMISSION_GRANTED
    return readGranted && writeGranted
}

private fun openAppPermissionSetting(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
