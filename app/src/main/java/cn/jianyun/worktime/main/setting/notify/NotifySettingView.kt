package cn.jianyun.worktime.main.setting.notify

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.APPS
import cn.jianyun.worktime.main.MainActivity
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.CalendarReminderUtils
import cn.jianyun.worktime.util.MyPermissionTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.oneLine
import cn.jianyun.worktime.vm.AppSettingViewModel
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.ShownItemView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.nav.OnlySmallTipText
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.component.nav.PanelView
import cn.jianyun.worktime.ui.theme.ThemeColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotifySettingView(navHostController: NavHostController){

    var viewModel = hiltViewModel<AppSettingViewModel>()

    var calendarPermission = rememberMultiplePermissionsState(permissions = listOf(
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.READ_CALENDAR,
    ))

    val context = LocalContext.current

    var testInfo by remember { mutableStateOf(mapOf<String,String>()) }

    PageView(navHostController = navHostController, title = "通知权限设置") {

        PanelView("关于APP通知？", content="""
            <bb>由于安卓手机品牌众多，不同手机厂商定制的安卓系统各有差异,这就会出现部分手机系统对第三方应用的服务做了一定的限制。
            <bb>经过开发者多次试验，如果应用被完全退出，或者设备锁屏了，
            我们App设置的本地通知大概率都不会触发（据说是为了省电），这样会导致我们设置的通知显得非常鸡肋，甚至影响用户正常工作，比如上班忘记打卡被公司扣钱。<br>
            <bb>但是系统日历设置的通知不会出现上面的问题，这是受系统保护的，它会准时触发。
            所以计划师所有模块的提醒将通过系统日历来实现通知，首次使用之前，
            请点击当前页面按钮授权计划师APP访问您的日历。
            虽然计划师APP会访问您的日历，但是我们仅会访问计划师范围内的通知数据，
            不会读取用户任何隐私，这个您可以在日历授权的时候可以看到的
            """.oneLine())

        if(calendarPermission.permissions.all{it.status == PermissionStatus.Granted}) {

            GroupView {
                SelectItemView(label = "手机品牌", value = viewModel.brand, onValueChange = {
                     viewModel.changeBrand(it)
                }, options = SelectUtil.initSingleValues("华为", "小米", "oppo", "vivo", "荣耀"));
            }
            Blank()

            LongOkButton("验证日历普通提醒") {
                testInfo = CalendarReminderUtils.getTestEventData(viewModel.baseRepository.context)
            }

            Blank()

            LongOkButton("检测系统是否支持闹钟提醒") {
                testInfo = CalendarReminderUtils.getTestAlarmEventData(viewModel.baseRepository.context, false)
            }
            Blank()

            LongOkButton("检测系统是否支持闹钟提醒") {
                testInfo = CalendarReminderUtils.getTestAlarmEventData(viewModel.baseRepository.context, true)
            }
            Blank()

            LongOkButton("检测系统是否支持闹钟提醒") {
                testInfo = CalendarReminderUtils.getTestAlarmEventData2(viewModel.baseRepository.context)
            }
            Blank()

            if(!testInfo.isEmpty()){
                GroupView {
                    testInfo.keys.sorted().forEach{
                        Column {
                            Text(it)
                            OnlySmallTipText(text = testInfo[it] ?: "", color= ThemeColor)
                        }
                    }
                }
            }

            Blank()
            GroupView {
                
                SwitchItemView(label = "开启通知", value = viewModel.isOpenNotify(), onValueChange = {
                    viewModel.changeNotifyStatus("all", it)
                })


                if(viewModel.isOpenNotify()){
                    APPS.forEach{
                        SwitchItemView(label = "${it.label}", value = viewModel.notifyStatus2[it.value] ?: false, onValueChange = {v ->
                            viewModel.changeNotifyStatus(it.value, v)
                        })
                    }
                }
            }
        }
        else{
            Column {
                if(calendarPermission.permissions.get(0).status is PermissionStatus.Denied) {
                    LongOkButton("授权计划师操作您的日历（更新数据）") {
                        if(calendarPermission.permissions[0].status.shouldShowRationale) {
                            calendarPermission.permissions[0].launchPermissionRequest()
                        }
                        else{
                            viewModel.baseRepository.toast("请前往系统设置页面允许计划师读写您的日历")
                            MyPermissionTool.toAppSettingPage(context)
                        }
                    }
                }

                Blank()

                if(calendarPermission.permissions.get(1).status is PermissionStatus.Denied) {
                    LongOkButton("授权计划师查询您的日历") {
                        if(calendarPermission.permissions[1].status.shouldShowRationale) {
                            calendarPermission.permissions[1].launchPermissionRequest()
                        }
                        else{
                            viewModel.baseRepository.toast("请前往系统设置页面允许计划师访问您的日历")
                            MyPermissionTool.toAppSettingPage(context)
                        }
                    }
                }
            }
        }
    }
}


