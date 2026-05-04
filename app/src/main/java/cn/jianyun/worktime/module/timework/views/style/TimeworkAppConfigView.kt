package cn.jianyun.worktime.module.timework.views.style



import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.timework.vm.TimeworkAppConfigViewModel
import cn.jianyun.worktime.ui.component.form.AdderView
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.ColorSelectItemView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.MultiSelectItemView
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.form.SegmentItemView
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.form.ZeroGroupView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkAppConfigView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkAppConfigViewModel>()
    viewModel.tryReload()

    Scaffold(content = {
        Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HeaderView(title = "日历显示设置", backAction = {
                    viewModel.resetChanges()
                    goBack(navHostController)
                })

                Column(
                    modifier= Modifier
                        .padding(10.dp, 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    TimeworkConfigView(viewModel = viewModel, padding = 10.dp){
                        goBack(navHostController)
                    }
                }
            }
        }
    })
}

@Composable
fun BottomTimeworkConfigView(onDismiss: () -> Unit){
    BottomTimeworkConfigPanel(initialTab = "style", onDismiss = onDismiss)
}

@Composable
fun BottomHomeStatConfigView(onDismiss: () -> Unit){
    BottomTimeworkConfigPanel(initialTab = "stat", onDismiss = onDismiss)
}

@Composable
private fun BottomTimeworkConfigPanel(initialTab: String, onDismiss: () -> Unit){
    val viewModel = hiltViewModel<TimeworkAppConfigViewModel>()
    viewModel.tryReload()
    val dialogHeight = (LocalConfiguration.current.screenHeightDp * 0.82f).dp
    var currentTab by remember(initialTab) {
        mutableStateOf(ifv(initialTab == "stat", "stat", "style"))
    }
    val dismissAction = {
        viewModel.resetChanges()
        onDismiss()
    }

    BottomDialogView(
        title = "个性化设置",
        cancelable = true,
        height = dialogHeight,
        rightTool = {
            VerticalRow {
                SmallLinkText(text = "取消") {
                    dismissAction()
                }
                Blank(6.dp)
                SmallLinkText(text = "保存") {
                    viewModel.save(onDismiss)
                }
            }
        },
        onDismiss = {
            dismissAction()
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            SegmentPickerView(
                value = currentTab,
                width = 84.dp,
                options = SelectUtil.initValues("日历样式", "style", "统计指标", "stat"),
                onChange = {
                    currentTab = it
                }
            )
        }
        Blank(12.dp)
        if(currentTab == "stat"){
            HomeStatConfigContent(viewModel = viewModel)
        }
        else{
            TimeworkStyleConfigContent(viewModel = viewModel)
        }
    }
}

@Composable
fun TimeworkConfigView(
    modifier:Modifier = Modifier,
    padding: Dp = 0.dp,
    showFooterSave: Boolean = true,
    viewModel: TimeworkAppConfigViewModel = hiltViewModel(),
    onDismiss: () -> Unit
){
    viewModel.tryReload()


    Column(modifier = modifier) {
        TimeworkStyleConfigContent(viewModel = viewModel, padding = padding)
        Blank(6.dp)
        HomeStatConfigContent(viewModel = viewModel, padding = padding)

        if(showFooterSave){
            LongOkButton("保存") {
                viewModel.save(onDismiss)
            }
        }
    }


}

@Composable
private fun TimeworkStyleConfigContent(
    viewModel: TimeworkAppConfigViewModel,
    padding: Dp = 0.dp
) {
    ZeroGroupView(horizonPadding = padding) {
        SegmentItemView(label = "一周开始日", width = 60.dp, value = viewModel.editItem.beginDay, options = SelectUtil.MONDAY_OR_SUNDAY,  onValueChange = {
            viewModel.editItem = viewModel.editItem.copy(beginDay = it)
        })

        SelectItemView(label = "考勤周期", value = viewModel.editItem.normalizedStatDay(), options = SelectUtil.getFromDays(),  onValueChange = {
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

        SwitchItemView(
            label = "显示日期标记",
            value = viewModel.editItem.showDateTag,
            onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(showDateTag = it)
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
            label = "上班时长需要精确到分钟",
            value = viewModel.editItem.needEveryMinute,
            onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(needEveryMinute = it)
            }
        )

        SwitchItemView(
            label = "日结需要填写时间",
            value = viewModel.editItem.needDayTime,
            onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(needDayTime = it)
            }
        )

        SwitchItemView(
            label = "播放打卡音效",
            value = viewModel.editItem.showVoice,
            onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(showVoice = it)
            }
        )
    }
}

@Composable
private fun HomeStatConfigContent(
    viewModel: TimeworkAppConfigViewModel,
    padding: Dp = 0.dp
) {
    ZeroGroupView(horizonPadding = padding) {
        Text("点击选择首页顶部要显示的指标", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
        Blank(4.dp)
        Text("提示：默认多行显示，超过4个指标后会从第二行开始靠左排列；开启单行显示后可左右滑动查看", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
        Blank(14.dp)
        AdderView(
            label = "统计区域字号",
            value = viewModel.editItem.normalizedHomeStatSize(),
            minValue = 12,
            maxValue = 30,
            onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(homeStatSize = it)
            }
        )
        Blank(10.dp)
        SwitchItemView(
            label = "统计区域单行显示",
            value = viewModel.editItem.homeStatSingleLine,
            onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(homeStatSingleLine = it)
            }
        )
        Blank(10.dp)
        HomeStatFieldPicker(viewModel = viewModel)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeStatFieldPicker(viewModel: TimeworkAppConfigViewModel) {
    FlowRow(modifier = Modifier.fillMaxWidth()) {
        val selectedFields = viewModel.editItem.normalizedHomeStatFields()
            .split("^")
            .filter { it.isNotBlank() }

        SelectUtil.HOME_STAT_TYPES.forEach { option ->
            val selected = selectedFields.contains(option.value)
            Text(
                text = option.label,
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 12.dp)
                    .radius(20.dp)
                    .clickable {
                        val nextValues = selectedFields.toMutableList()
                        if (selected) {
                            if (nextValues.size == 1) {
                                viewModel.baseRepository.toast("至少选择1个指标")
                                return@clickable
                            }
                            nextValues.remove(option.value)
                        } else {
                            nextValues.add(option.value)
                        }
                        val result = SelectUtil.HOME_STAT_TYPES
                            .filter { nextValues.contains(it.value) }
                            .joinToString("^") { it.value }
                        viewModel.editItem = viewModel.editItem.copy(homeStatFields = result)
                    }
                    .background(
                        ifv(
                            selected,
                            ThemeColor,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    .padding(horizontal = 14.dp)
                    .height(32.dp)
                    .wrapContentSize(),
                color = ifv(selected, Color.White, MaterialTheme.colorScheme.primary),
                fontSize = 13.sp,
                lineHeight = 13.sp
            )
        }
    }
}
