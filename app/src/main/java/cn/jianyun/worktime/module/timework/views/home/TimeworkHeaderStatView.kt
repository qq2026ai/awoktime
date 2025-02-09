package cn.jianyun.worktime.module.timework.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.util.withUnit
import cn.jianyun.worktime.ui.theme.ThemeColor


@Composable
fun TimeworkHeaderStatView(viewModel: TimeworkMasterViewModel) {

    var fontSize = ifv(viewModel.monthStatModel.dayMoney != "", 12.sp, 15.sp)

    Row(modifier= Modifier
        .radius(12.dp)
        .background(ThemeColor)
        .padding(0.dp, 12.dp)
        .fillMaxWidth()) {

        if(viewModel.monthStatModel.dayCount == "" || viewModel.monthStatModel.baseHour != "" || viewModel.monthStatModel.overHour != ""){
            statNumView(title = "正班工时", fontSize, value = MyDataTool.getShownTime(viewModel.monthStatModel.baseHour, true), modifier=Modifier.weight(1f))
            statNumView(title = "加班工时", fontSize, value = MyDataTool.getShownTime(viewModel.monthStatModel.overHour, true), modifier=Modifier.weight(1f))
        }
        if(viewModel.monthStatModel.dayMoney != ""){
            statNumView(title = "日结收入", fontSize, value = viewModel.monthStatModel.dayMoney.withUnit(2,"元"), modifier=Modifier.weight(1f))
        }
        statNumView(title = "补扣金额", fontSize, value = MyDataTool.withUnit(viewModel.monthStatModel.awardMoney, 2,"元"), modifier=Modifier.weight(1f))
        statNumView(title = "本月收入", fontSize, value = MyDataTool.withUnit(viewModel.monthStatModel.fetchTotalMoney(), 2,"元"), modifier=Modifier.weight(1f))
    }

}


@Composable
private fun statNumView(title: String, fontSize: TextUnit = 15.sp, value: String, modifier: Modifier=Modifier){
    Column(modifier=modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color= Color.White, fontSize = 12.sp)
        Text(value, color= Color.White, maxLines = 1, fontSize = fontSize, fontWeight = FontWeight.Medium)
    }
}