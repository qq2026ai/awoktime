package cn.jianyun.worktime.module.timework.views.biz

import android.content.res.Resources.Theme
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.R
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.nav.MonthChooseView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.dateStr
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mainBg
import cn.jianyun.worktime.util.parseDate
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.VibrateUtil
import cn.qsfty.worktime.component.CalendarHeaderView
import cn.qsfty.worktime.component.VerticalView
import java.util.Date



@Composable
fun TimeworkCalendarView(viewModel: TimeworkMasterViewModel){

//    val context = LocalContext.current
//    val soundPool = remember {
//        SoundPool.Builder()
//            .setMaxStreams(1)
//            .setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_GAME)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .build()
//            )
//            .build()
//    }
//
//    val soundId = remember { soundPool.load(context, R.raw.click_6, 1) }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            soundPool.release()
//        }
//    }

    return GroupView(horizonPadding = 2.dp) {
        CalendarHeaderView(viewModel.appConfig.isMondayFirst())
        TimeworkCalendarBodyView(viewModel){ date ->
            TimeworkCellView(viewModel.fetchShownData(date))
        }
        TwoColumnView {
            Row(modifier= Modifier
                .mainBg(6.dp)
                .clickable {
                    VibrateUtil.vibrate(viewModel.baseRepository.context)
                    viewModel.currentDate = MyDateTool.getStartDayOfMonth(
                        MyDateTool.gapDay(
                            MyDateTool.getStartDayOfMonth(viewModel.currentDate), -5
                        )
                    )
                    viewModel.chooseDates.clear()
                    viewModel.doChange()
                }
                .padding(10.dp, 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconView(icon = IconFont.back, color = Color.Gray)
                Blank(3.dp)
                Text("上个月", color = Color.Gray)
            }

            if(viewModel.getCurrentDateStr() != MyDateTool.toDateString(Date())) {
                Text("今天", modifier= Modifier
                    .mainBg(6.dp)
                    .clickable {
                        VibrateUtil.vibrate(viewModel.baseRepository.context)
                        if(MyDateTool.toChineseMonthString(viewModel.currentDate) != MyDateTool.toChineseMonthString(Date())) {
                            viewModel.chooseDates.clear()
                        }
                        viewModel.currentDate = Date()
                        viewModel.doChange()
                    }
                    .width(60.dp)
                    .height(30.dp)
                    .wrapContentSize(), fontSize = 12.sp, color= DeleteColor)
            }

            Row(modifier= Modifier
                .mainBg(6.dp)
                .clickable {
                    VibrateUtil.vibrate(viewModel.baseRepository.context)
                    viewModel.currentDate = MyDateTool.getStartDayOfMonth(
                        MyDateTool.gapDay(
                            MyDateTool.getLastDayOfMonth(viewModel.currentDate), 5
                        )
                    )
                    viewModel.chooseDates.clear()
                    viewModel.doChange()
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

@Composable
fun TimeworkCalendarBodyView(viewModel: TimeworkMasterViewModel,  content: @Composable (Date) -> Unit){
    val monthInfos = MyDateTool.getMonthInfo(viewModel.currentDate, viewModel.appConfig.isMondayFirst(), ifv(viewModel.appConfig.showFestival, viewModel.holidayMap, mapOf()), false)
    Box(contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
            monthInfos.forEach{
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    it.forEach{
                        Box(contentAlignment = Alignment.TopStart, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier
                                .tap {
                                    if (it.day != "") {
                                        VibrateUtil.vibrate(viewModel.baseRepository.context)
                                        viewModel.makeCurrentDate(it.date)
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    color = ifv(
                                        viewModel.isFocus(it.date),
                                        ThemeColor,
                                        MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 2.dp)

                                .background(ifv(
                                    viewModel.isFocus(it.date),
                                    ThemeColor.copy(0.2f),
                                    MaterialTheme.colorScheme.surface
                                ),)
                                .height(viewModel.appConfig.allSize())
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

                                    if(viewModel.appConfig.showLunar || viewModel.appConfig.showFestival && it.holiday) {
                                        VerticalView(text= it.lunarDay, color=it.fetchLunarColor(), fontSize=8.sp)
                                    }

                                }
                                if(it.day != ""){
                                    content(it.date)
                                }
                            }

                            if(it.day != ""){
                                IconView(icon= IconFont.money, color = ThemeColor, iconSize = 13.sp)
                            }
                        }
                    }
                }

                if(viewModel.appConfig.showHLine){
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


}