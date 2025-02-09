package cn.jianyun.worktime.main.test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.ui.component.nav.MonthChooseView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.dateStr
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.parseDate
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.qsfty.worktime.component.CalendarHeaderView
import cn.qsfty.worktime.component.VerticalView
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@Composable
fun TestV2(){

    var viewModel = hiltViewModel<V2ViewModel>()
    viewModel.tryReload("v2")

    Column {
        Text("${viewModel.oldSid}")

        GroupView(horizonPadding = 2.dp) {
            CenterRow(padding=10.dp) {
                MonthChooseView(value=viewModel.getCurrentDateStr(), onChange = {
                    viewModel.currentDate = it.parseDate()
                })
            }
            CalendarHeaderView(true)
            ScheduleCalendarBodyV2View(viewModel){ date ->

            }
        }

        LongOkButton("刷新2") {
            viewModel.baseRepository.reload()
        }
    }
}


@HiltViewModel
class V2ViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : BaseViewModel() {

    var currentDate by mutableStateOf(Date())

    init {
        mlog("init v2")
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload() {

    }

    override fun reloadData(dataChanged: Boolean) {

    }

    fun getCurrentDateStr(): String {
        return MyDateTool.toDateString(currentDate)
    }
}



@Composable
fun ScheduleCalendarBodyV2View(viewModel: V2ViewModel, content: @Composable (Date) -> Unit){
    var monthInfos = MyDateTool.getMonthInfo(viewModel.currentDate, true, mapOf(), true)

    Box(contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
            monthInfos.forEach{
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    it.forEach{
                        Column(modifier = Modifier
                            .radius(4.dp)
                            .tap {
                                if (it.day != "") {
                                    viewModel.currentDate = it.date
                                    viewModel.reloadData()
                                }
                            }
                            .border(
                                width = 2.dp,
                                color = ifv(
                                    viewModel.getCurrentDateStr() == it.date.dateStr(),
                                    ThemeColor,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                            .padding(horizontal = 2.dp)
                            .weight(1f)
//                        .height(ifv(viewModel.appConfig.showMoney, 80.dp, 60.dp))
                            .padding(vertical = 3.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(modifier = Modifier
                                .width(40.dp)
                                .height(36.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically){
                                Text(text = it.day,
                                    color= it.fetchDayColor(),
                                    modifier = Modifier
                                        .width(26.dp),
                                    fontSize = 18.sp,
                                    lineHeight = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center)

                                VerticalView(text= it.lunarDay, color=it.fetchLunarColor(), fontSize=8.sp)
                            }
                            if(it.day != ""){
                                content(it.date)
                            }
                        }
                    }
                }

                Blank(5.dp)
                Text("", modifier= Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxWidth()
                    .height(1.dp))
                Blank(5.dp)

            }
        }

        Text(MyDateTool.getBigMonth(viewModel.currentDate), fontSize = 80.sp, color= Color.Gray.copy(0.1f))
    }

}