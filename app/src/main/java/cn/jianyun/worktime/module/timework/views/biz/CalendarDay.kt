import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.jianyun.worktime.api.FestivalData
import cn.jianyun.worktime.model.MonthDateInfo
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.VibrateUtil
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mainBg
import cn.jianyun.worktime.util.mlog
import cn.qsfty.worktime.component.CalendarHeaderView
import cn.qsfty.worktime.component.VerticalView
import kotlinx.coroutines.flow.dropWhile
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.*
import java.util.Calendar
import java.util.Calendar.*
import java.util.Date

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(dataModel: TimeworkMasterViewModel,  content: @Composable (Date) -> Unit) {
    val viewModel: CalendarViewModel = viewModel()

    val monthDataMap by viewModel.monthCache.collectAsState()

    // 初始定位到比较大的数字，让用户可以左右滑

    // 🚀 核心设定：
    // 设置一个巨大的中间值作为“当前月”的索引。
    // 这样 index - INITIAL_INDEX = 0 (当前月), -1 (上个月), +1 (下个月)
    val INITIAL_INDEX = 100

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = INITIAL_INDEX)
    // 🚀 替代 Pager 的关键：吸附行为
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // 获取屏幕宽度，确保每个 Item 占满一屏

    // 监听滑动停止，触发预加载
    // derivedStateOf 确保只在 index 真正变化时触发，减少重组
    val currentVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(currentVisibleIndex) {

        mlog("monday First", dataModel.appConfig.isMondayFirst())
        viewModel.onPageChanged(currentVisibleIndex, dataModel.appConfig.isMondayFirst(), ifv(dataModel.appConfig.showFestival, dataModel.holidayMap,
            HashMap()))

        dataModel.currentDate = viewModel.getDate(currentVisibleIndex, MyDateTool.getDay(dataModel.currentDate))
        dataModel.reloadData(false)
    }


    // 2. 🚀 新增：监听 dataModel.currentDate 变化 (外部跳转)
    // 比如点击“今天”，或者选择日期后，日历要滚到对应月份
    LaunchedEffect(dataModel.currentDate) {
        val targetIndex = viewModel.getIndexForDate(dataModel.currentDate)

        // 只有当目标 Index 与当前显示的 Index 不一致时才滚动
        // 这既实现了跳转，又避免了滑动更新 currentDate 时的死循环
        if (targetIndex != listState.firstVisibleItemIndex) {
            listState.scrollToItem(targetIndex)
        }
    }

    LaunchedEffect(dataModel.appConfig.beginDay) {
        mlog("beginDayChanged", dataModel.appConfig.isMondayFirst())
        viewModel.onPageChanged(currentVisibleIndex, dataModel.appConfig.isMondayFirst(), ifv(dataModel.appConfig.showFestival, dataModel.holidayMap,
            HashMap()), true)
    }

    return GroupView(horizonPadding = 0.dp) {
        CalendarHeaderView(dataModel.appConfig.isMondayFirst())

        // 日历滑动区域
        LazyRow(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = Int.MAX_VALUE,
                key = { index -> index } // 使用 index 作为 key
            ) { index ->

                // 外层容器：宽度强制填满屏幕，模拟 Pager 的一页
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val state = monthDataMap[index]
                    when (state) {
                        null, is MonthUiState.Loading -> {
                            // 加载中状态
                            LoadingView()
                        }
                        is MonthUiState.Success -> {
                            // 显示真实日历
                            MonthCalendarView(state, dataModel, content)
                        }
                    }
                }
            }
        }

        TwoColumnView {
            Row(modifier= Modifier
                .mainBg(6.dp)
                .clickable {
                    VibrateUtil.vibrate(dataModel.baseRepository.context)
                    dataModel.currentDate = MyDateTool.getStartDayOfMonth(
                        MyDateTool.gapDay(
                            MyDateTool.getStartDayOfMonth(dataModel.currentDate), -5
                        )
                    )
                    dataModel.chooseDates.clear()
                    dataModel.doChange()
                }
                .padding(10.dp, 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconView(icon = IconFont.back, color = Color.Gray)
                Blank(3.dp)
                Text("上个月", color = Color.Gray)
            }

            if(dataModel.getCurrentDateStr() != MyDateTool.toDateString(Date())) {
                Text("今天", modifier= Modifier
                    .mainBg(6.dp)
                    .clickable {
                        VibrateUtil.vibrate(dataModel.baseRepository.context)
                        if(MyDateTool.toChineseMonthString(dataModel.currentDate) != MyDateTool.toChineseMonthString(Date())) {
                            dataModel.chooseDates.clear()
                        }
                        dataModel.currentDate = Date()
                        dataModel.doChange()
                    }
                    .width(60.dp)
                    .height(30.dp)
                    .wrapContentSize(), fontSize = 12.sp, color= DeleteColor)
            }

            Row(modifier= Modifier
                .mainBg(6.dp)
                .clickable {
                    VibrateUtil.vibrate(dataModel.baseRepository.context)
                    dataModel.currentDate = MyDateTool.getStartDayOfMonth(
                        MyDateTool.gapDay(
                            MyDateTool.getLastDayOfMonth(dataModel.currentDate), 5
                        )
                    )
                    dataModel.chooseDates.clear()
                    dataModel.doChange()
                }
                .padding(10.dp, 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("下个月", color = Color.Gray)
                Blank(3.dp)
                IconView(icon = IconFont.arrow_right, color = Color.Gray)
            }
        }

    }

}

// 🚀 新增：骨架屏组件 (看起来像日历，但是没有数字)
// 这样在滑动等待数据时，用户感觉界面没有“消失”，只是数字还没出来
@Composable
fun LoadingView() {
    Column(modifier = Modifier.padding(0.dp)) {
        val rows = 6 // 通常日历最大6行
        val cols = 7
        for (r in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                for (c in 0 until cols) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp) // 保持和你真实 Item 差不多的高度
                            .padding(vertical = 3.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 可以放一个淡淡的灰色块，或者留白
                        // Box(modifier = Modifier.size(20.dp).background(Color.LightGray.copy(alpha = 0.3f), CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
fun MonthCalendarView(state: MonthUiState.Success, dataModel: TimeworkMasterViewModel,  content: @Composable (Date) -> Unit) {
    Column(modifier = Modifier.padding(0.dp)) {
        val days = state.days
        val rows = 7
        val cols = 7
        for (r in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                for (c in 0 until cols) {
                    if (r < days.size) {
                        DayCell(it = days[r][c], modifier = Modifier.weight(1f), dataModel=dataModel,  content)
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            if(dataModel.appConfig.showHLine && r < days.size - 1){
                Blank(2.dp)
                Text("", modifier= Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxWidth()
                    .height(1.dp))
                Blank(2.dp)
            }
            else{
                Blank(2.dp)
            }

        }
    }
}

@Composable
fun DayCell(it: MonthDateInfo, modifier: Modifier = Modifier, dataModel: TimeworkMasterViewModel,  content: @Composable (Date) -> Unit) {
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        if (it.day != "") {
            Box(contentAlignment = Alignment.TopStart, modifier = Modifier) {
                Column(modifier = Modifier
                    .tap {
                        dataModel.makeCurrentDate(it.date)
                    }
                    .border(
                        width = 1.dp,
                        color = ifv(
                            dataModel.isFocus(it.date),
                            ThemeColor,
                            MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 2.dp)

                    .background(ifv(
                        dataModel.isFocus(it.date),
                        ThemeColor.copy(0.2f),
                        MaterialTheme.colorScheme.surface
                    ),)
                    .fillMaxWidth()
                    .height(dataModel.appConfig.allSize())
                    .padding(vertical = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier
                        .width(40.dp)
                        .height(30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = it.day,
                            color= it.fetchDayColor(),
                            modifier = Modifier
                                .width(26.dp)
                                .height(30.dp).wrapContentHeight(),
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center)

                        if(dataModel.appConfig.showLunar || dataModel.appConfig.showFestival && it.holiday) {
                            VerticalView(text= it.lunarDay, color=it.fetchLunarColor(), fontSize=8.sp)
                        }

                    }
                    if(it.day != ""){
                        content(it.date)
                    }
                }

//                            if(it.day != ""){
//                                IconView(icon= IconFont.money, color = ThemeColor, iconSize = 13.sp)
//                            }
            }

        }
    }
}

// 月份状态：包含加载状态
sealed interface MonthUiState {
    object Loading : MonthUiState
    data class Success(val year: Int, val month: Int, val days: List<List<MonthDateInfo>>) : MonthUiState
}

class CalendarViewModel() : ViewModel() {

    // 缓存已加载的月份数据：Key 是 monthIndex (距离基准月的偏移量)

    private val _monthCache = MutableStateFlow<Map<Int, MonthUiState>>(emptyMap())
    val monthCache = _monthCache.asStateFlow()

    // 🚀 核心设定：无限滑动的中间锚点
    // 设置为 Int 最大值的一半，这样用户既可以向左滑(减小)，也可以向右滑(增大)
    val INITIAL_INDEX = 100

    // 🚀 核心设定：基准时间
    // 获取当前系统时间 (例如 2026-01)，作为 index = INITIAL_INDEX 时的日期
    private val baseYearMonth = YearMonth.now()
    /**
     * 当页面滑动时调用此方法
     * @param centerIndex 当前屏幕中间显示的 index
     */
    fun onPageChanged(centerIndex: Int, mondayFirst: Boolean, festivalData: Map<String, FestivalData>, reload: Boolean = false) {
        if(reload){
            _monthCache.value = emptyMap()
        }
        // 预加载策略：加载当前页 + 前后各 1 页
        val range = (centerIndex - 1)..(centerIndex + 1)
        range.forEach { index ->
            // 如果缓存里没有，才去加载，避免重复计算
            if (!_monthCache.value.containsKey(index)) {
                loadMonthData(index,mondayFirst , festivalData)
            }
        }
    }

    // 🚀 新增：根据 Date 获取 Index (用于 dataModel 变化时跳转日历)
    fun getIndexForDate(date: Date): Int {
        val calendar = getInstance()
        calendar.time = date
        val year = calendar.get(YEAR)
        val month = calendar.get(MONTH) + 1 // Calendar.MONTH is 0-based

        val targetYearMonth = YearMonth.of(year, month)

        // 计算目标月份与基准月份的差值
        val diffMonths = MONTHS.between(baseYearMonth, targetYearMonth).toInt()

        return INITIAL_INDEX + diffMonths
    }

    fun getDate(index: Int, oldDay: Int): Date{
        // 计算偏移量：当前 index 距离“中间起点”差了几个月
        val offsetMonth = index - INITIAL_INDEX

        // 使用 java.time API 自动处理跨年逻辑 (例如 1月 - 1 = 去年12月)
        // 这完美解决了 "前10年、后3年" 的计算问题
        val targetDate = baseYearMonth.plusMonths(offsetMonth.toLong())

        val year = targetDate.year
        // monthValue 是 1-12。MyDateTool 通常需要 0-11，所以这里减 1
        // 如果你的 MyDateTool make 函数参数逻辑不同，请在此调整
        val monthIndexForTool = targetDate.monthValue

        return MyDateTool.make("" + year, "" + monthIndexForTool, "" + oldDay)
    }

    private fun loadMonthData(index: Int, mondayFirst: Boolean, festivalData: Map<String, FestivalData>) {
        // 先占位，显示 Loading
        val currentCache = _monthCache.value
        val hasExistingData = currentCache[index] is MonthUiState.Success

        // 🚀 核心修改 2：只有在完全没有数据时，才发送 Loading 状态
        // 这样如果已经有数据（比如缓存），用户会继续看到旧数据，直到新数据算好直接覆盖
        if (!hasExistingData) {
            _monthCache.update { it + (index to MonthUiState.Loading) }
        }


        // 🚀 关键：启动 IO 协程，绝对不阻塞主线程
        viewModelScope.launch(Dispatchers.IO) {

            // --- A. 计算目标年月 ---
            // 计算偏移量：当前 index 距离“中间起点”差了几个月
            val offsetMonth = index - INITIAL_INDEX

            // 使用 java.time API 自动处理跨年逻辑 (例如 1月 - 1 = 去年12月)
            // 这完美解决了 "前10年、后3年" 的计算问题
            val targetDate = baseYearMonth.plusMonths(offsetMonth.toLong())

            val year = targetDate.year
            // monthValue 是 1-12。MyDateTool 通常需要 0-11，所以这里减 1
            // 如果你的 MyDateTool make 函数参数逻辑不同，请在此调整
            val monthIndexForTool = targetDate.monthValue


            val daysList = MyDateTool.getMonthInfo(
                MyDateTool.make("" + year, "" + monthIndexForTool, "" + 1),
                mondayFirst,
                festivalData, false)

            mlog("render:"  + index + ":" + year + "_" + monthIndexForTool, mondayFirst)
            mlog(daysList)

            // 4. 切回主线程更新状态
            withContext(Dispatchers.Main) {
                _monthCache.update {
                    it + (index to MonthUiState.Success(year, monthIndexForTool, daysList))
                }
            }
        }
    }
}