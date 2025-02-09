package cn.jianyun.worktime.module.timework.views.style



import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.timework.vm.TimeworkAppConfigViewModel
import cn.jianyun.worktime.ui.component.form.AdderView
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.ColorSelectItemView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SegmentItemView
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.form.ZeroGroupView
import cn.jianyun.worktime.ui.component.nav.HeaderView

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkAppConfigView(navHostController: NavHostController) {
    Scaffold(content = {
        Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HeaderView(title = "日历显示设置", backAction = {
                    goBack(navHostController)
                })

                Column(
                    modifier= Modifier
                        .padding(10.dp, 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    TimeworkConfigView(padding = 10.dp){
                        goBack(navHostController)
                    }
                }
            }
        }
    })
}

@Composable
fun BottomTimeworkConfigView(onDismiss: () -> Unit){
    BottomDialogView(title = "日历显示设置", cancelable = true, onDismiss = {
        onDismiss()
    }) {
        TimeworkConfigView(onDismiss=onDismiss)
    }
}

@Composable
fun TimeworkConfigView(modifier:Modifier = Modifier, padding: Dp = 0.dp, onDismiss: () -> Unit){
    val viewModel = hiltViewModel<TimeworkAppConfigViewModel>()
    viewModel.tryReload()


    Column {
        ZeroGroupView(horizonPadding = padding) {

            SegmentItemView(label = "一周开始日", value = viewModel.editItem.beginDay, options = SelectUtil.MONDAY_OR_SUNDAY,  onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(beginDay = it)
            })

            SelectItemView(label = "考勤周期", value = viewModel.editItem.statDay, options = SelectUtil.getFromDays(),  onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(statDay = it)
            })

            SwitchItemView(
                label = "显示中国农历",
                value = viewModel.editItem.showLunar,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(showLunar = it)
                }
            )

            SwitchItemView(
                label = "显示中国节假日",
                value = viewModel.editItem.showFestival,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(showFestival = it)
                }
            )

            SwitchItemView(
                label = "显示工时",
                value = viewModel.editItem.showHour,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(showHour = it)
                }
            )

            SwitchItemView(
                label = "显示金额",
                value = viewModel.editItem.showMoney,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(showMoney = it)
                }
            )

            ColorSelectItemView(
                label = "工时背景色",
                value = viewModel.editItem.hourBg,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(hourBg = it)
                }
            )

            ColorSelectItemView(
                label = "金额背景色",
                value = viewModel.editItem.moneyBg,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(moneyBg = it)
                }
            )

            AdderView(label = "工时字体大小", value = viewModel.editItem.hourSize, minValue = 8, maxValue = 18, onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(hourSize = it)
            })

            AdderView(label = "金额字体大小", value = viewModel.editItem.moneySize, minValue = 8, maxValue = 18, onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(moneySize = it)
            })

            ColorSelectItemView(
                label = "请假背景色",
                value = viewModel.editItem.leaveBg,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(leaveBg = it)
                }
            )

            ColorSelectItemView(
                label = "休息背景色",
                value = viewModel.editItem.restBg,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(restBg = it)
                }
            )

            SwitchItemView(
                label = "日历显示分割线",
                value = viewModel.editItem.showHLine,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(showHLine = it)
                }
            )

            SwitchItemView(
                label = "播放打卡音效",
                value = viewModel.editItem.showNotice,
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(showNotice = it)
                }
            )
        }

        LongOkButton("保存") {
            viewModel.save(onDismiss)

        }
    }


}