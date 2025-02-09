package cn.jianyun.worktime.ui.component.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import cn.jianyun.worktime.ui.android.lunar.GregorianLunarCalendarView
import cn.jianyun.worktime.ui.component.model.kt.RangeDate
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.focusColor
import cn.jianyun.worktime.util.gapDay
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.util.tipColor
import cn.jianyun.worktime.util.toAndroid
import cn.jianyun.worktime.ui.component.form.BottomConfirmButtonGroup
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.OkAndCancelButtonGroup
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.CenterColumn
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.CircleIconView
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.ifv
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthChooseView(value: String = "", beginDay: String = "1", fontSize: TextUnit = 15.sp, showNav: Boolean = true, onChange: (String) -> Unit){

    var realMonth = Date()
    var month = MyDateTool.toChineseMonthString(Date())
    if(value.length == 10){
        realMonth = MyDateTool.parseDateString(value)
        month = MyDateTool.toChineseMonthString(MyDateTool.parseDateString(value))
    }

    var showDialog by remember { mutableStateOf(false) }

    Box {
        VerticalRow {
            if(showNav){
                CircleIconView(icon = IconFont.right2, color= Color.Gray,  modifier = Modifier.rotate(180f), modifier2 = Modifier.clickable {
                    onChange(MyDateTool.toDateString(MyDateTool.gapMonth(realMonth, -1)))
                })
                Blank(10.dp)
            }
            Text(month, color= ifv(showNav, ThemeColor, MaterialTheme.colorScheme.primary), fontSize = fontSize, modifier = Modifier.tap {
                showDialog = true
            })
            if(showNav){
                Blank(10.dp)
                CircleIconView(icon = IconFont.right2, color= Color.Gray, modifier2 = Modifier.clickable {
                    onChange(MyDateTool.toDateString(MyDateTool.gapMonth(realMonth, 1)))
                })
            }
        }

        if(showDialog){

            var chooseYear by remember { mutableStateOf("" + MyDateTool.getYear(realMonth)) }
            var chooseMonth by remember { mutableStateOf("" + MyDateTool.getMonth(realMonth)) }


            BottomDialogView(title = "选择月份", height = 300.dp, onDismiss = {
                showDialog = false
            }) {

                Row {
                    Column(modifier= Modifier
                        .height(260.dp)
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        ) {
                        listOf("2022",  "2023", "2024", "2025", "2026", "2027", "2028").forEach {
                            Text(it + "年",color= focusColor(it == chooseYear), modifier = Modifier
                                .tap {
                                    chooseYear = it
                                }
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp)
                                .wrapContentSize())
                        }
                    }
                    VerticalDivider(color=Color.Gray, modifier=Modifier.padding(0.dp, 20.dp))
                    Column(modifier= Modifier
                        .height(260.dp)
                        .verticalScroll(rememberScrollState())
                        .weight(3f)
                       ) {

                        FlowRow {
                            SelectUtil.initWithUnit("", "月", 1, 12).forEach {
                                Text(it.label,color= focusColor(it.value == chooseMonth), modifier = Modifier
                                    .tap {
                                        chooseMonth = it.value
                                        showDialog = false
                                        onChange(
                                            MyDateTool.toDateString(
                                                MyDateTool.make(
                                                    chooseYear,
                                                    chooseMonth,
                                                    beginDay
                                                )
                                            )
                                        )
                                    }
                                    .fillMaxWidth(0.33f)
                                    .padding(0.dp, 20.dp)
                                    .wrapContentSize()
                                )

                            }
                        }
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YearChooseView(value: String = "",onChange: (String) -> Unit){

    var realYear = "${MyDateTool.getYear(Date())}"
    if(value.length == 4){
        realYear = value
    }

    var showDialog by remember { mutableStateOf(false) }
    var chooseYear by remember { mutableStateOf(realYear) }

    Box {
        VerticalRow {
            CircleIconView(icon = IconFont.right2, color= Color.Gray,  modifier = Modifier.rotate(180f), modifier2 = Modifier.clickable {
                onChange("${realYear.toInt() - 1}")
            })
            Blank(10.dp)
            Text(realYear + "年", fontSize = 15.sp, color= ThemeColor, modifier=Modifier.tap {
                showDialog = true
            })
            Blank(10.dp)
            CircleIconView(icon = IconFont.right2, color= Color.Gray, modifier2 = Modifier.clickable {
                onChange("${realYear.toInt() + 1}")
            })
        }

        if(showDialog){
            BottomDialogView(title = "选择年份", height = 300.dp, onDismiss = {
                showDialog = false
            }) {
                FlowRow {
                    listOf("2022",  "2023", "2024", "2025", "2026", "2027").forEach {
                        Text(it + "年",color= focusColor(it == chooseYear), modifier = Modifier
                            .tap {
                                showDialog = false
                                chooseYear = it
                                onChange(it)
                            }
                            .fillMaxWidth(0.333f)
                            .padding(0.dp, 20.dp)
                            .wrapContentSize())
                    }
                }
            }
        }
    }
}

@Composable
fun DateRangeChooseView(value: RangeDate = RangeDate(), clearable: Boolean = false, mini: Boolean = false, future: Boolean = false, onChange: (RangeDate) -> Unit){

    var showDialog by remember { mutableStateOf(false) }

    var calendar = Calendar.getInstance()
    calendar.time = MyDateTool.parseDateString(value.beginDate)

    var calendar2 = Calendar.getInstance()
    calendar2.time = MyDateTool.parseDateString(value.endDate)

    var beginState by remember {
        mutableStateOf(calendar)
    }
    var endState by remember {
        mutableStateOf(calendar2)
    }

    var focusType by remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val themeColor = ThemeColor.toAndroid()
    val dividerColor = MaterialTheme.colorScheme.surface.toAndroid()

    LaunchedEffect(Unit){
        keyboardController?.hide()
    }

    Box {
        if(mini){
            CenterColumn(modifier=Modifier.tap{
                showDialog = true
            }) {
                if(value.beginDate == ""){
                    CenterRow {
                        IconView(icon=IconFont.time, color=Color.Gray)
                        Text("日期区间", lineHeight = 12.sp, fontSize = 12.sp, color=Color.Gray)
                    }
                }
                else{
                    Text(value.showBeginDate(), color= ThemeColor, lineHeight = 10.sp, fontSize = 12.sp)
                    Text(value.showEndDate(), color = ThemeColor, lineHeight = 10.sp, fontSize = 12.sp)
                }
            }
        }
        else{
            Box(contentAlignment = Alignment.CenterEnd) {
                VerticalRow(modifier=Modifier.tap {
                    showDialog = true
                }) {
                    Text(value.showBeginDate(), color= ThemeColor, fontSize = 15.sp)
                    Text("至", color = Color.Gray, fontSize = 12.sp)
                    Text(value.showEndDate(), color = ThemeColor, fontSize = 15.sp)
                }
            }
        }

        if(showDialog){
            BottomDialogView(title = "选择日期区间",rightTool = {
              if(clearable){
                  SmallLinkText(text = "清空") {
                      showDialog = false
                      focusType = ""
                      onChange(RangeDate())
                  }
              }
            },  height = 500.dp, onDismiss = {
                showDialog = false
            }) {

                CenterRow(paddingBottom = 10.dp) {
                    SmallTipText(text = "最近7天", color= tipColor(focusType == "7d"), padding = 2.dp) {
                        beginState = MyDateTool.toCalendar(MyDateTool.gapDayFromNow(-6))
                        endState = MyDateTool.toCalendar(MyDateTool.getNow())
                        focusType = "7d"
                    }
                    SmallTipText(text = "最近1个月",color= tipColor(focusType == "1m"), padding = 2.dp) {
                        beginState = MyDateTool.toCalendar(MyDateTool.gapMonth(Date(), -1).gapDay(1))
                        endState = MyDateTool.toCalendar(MyDateTool.getNow())
                        focusType = "1m"
                    }
                    SmallTipText(text = "最近3个月",color= tipColor(focusType == "3m"), padding = 2.dp) {
                        beginState = MyDateTool.toCalendar(MyDateTool.gapMonth(Date(), -3).gapDay(1))
                        endState = MyDateTool.toCalendar(MyDateTool.getNow())
                        focusType = "3m"
                    }
                    SmallTipText(text = "最近一年",color= tipColor(focusType == "1y"), padding = 2.dp) {
                        beginState = MyDateTool.toCalendar(MyDateTool.gapMonth(Date(), -12).gapDay(1))
                        endState = MyDateTool.toCalendar(MyDateTool.getNow())
                        focusType = "1y"
                    }
                    SmallTipText(text = "本月",color= tipColor(focusType == "n1m"), padding = 2.dp) {
                        beginState = MyDateTool.toCalendar(MyDateTool.getStartDayOfMonth(Date()))
                        endState = MyDateTool.toCalendar(MyDateTool.getLastDayOfMonth(Date()))
                        focusType = "n1m"
                    }
                    SmallTipText(text = "本年",color= tipColor(focusType == "n1y"), padding = 2.dp) {
                        beginState = MyDateTool.toCalendar(MyDateTool.getStartDayOfMonth(MyDateTool.make(MyDateTool.getYear(Date()).toString(), "1", "1")))
                        endState = MyDateTool.toCalendar(MyDateTool.getLastDayOfMonth(MyDateTool.make(MyDateTool.getYear(Date()).toString(), "12", "1")))
                        focusType = "n1y"
                    }

                }

                Column(modifier= Modifier
                    .radius(8.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .height(180.dp)
                    .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    AndroidView(factory = { ctx ->
                        GregorianLunarCalendarView(ctx)
                    }, modifier=Modifier.height(180.dp).fillMaxWidth(), update = {
                        it.init(beginState, true)
                        it.setThemeColor(themeColor)
                        it.setDividerColor(dividerColor)
                        it.setOnDateChangedListener {
                            beginState = it.calendar
                            if(beginState.time > endState.time){
                                endState = beginState
                            }

                        }
                    })
                }

                CenterRow {
                    Text("至", color=Color.Gray, fontSize = 12.sp)
                }

                Column(modifier= Modifier
                    .radius(8.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .height(180.dp)
                    .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    AndroidView(factory = { ctx ->
                        GregorianLunarCalendarView(ctx)
                    }, modifier=Modifier.height(180.dp).fillMaxWidth(), update = {
                        it.init(endState, true)
                        it.setThemeColor(themeColor)
                        it.setDividerColor(dividerColor)
                        it.setOnDateChangedListener {
                            endState = it.calendar

                            if(beginState.time > endState.time){
                                beginState = endState
                            }
                        }
                    })
                }
                Blank()

                OkAndCancelButtonGroup(okAction = {
                    showDialog = false
                    focusType = ""
                    onChange(RangeDate(beginDate = MyDateTool.toDateString(beginState.time), endDate = MyDateTool.toDateString(endState.time)))
                }) {
                    showDialog = false
                    focusType = ""
                }
            }
        }
    }
}
