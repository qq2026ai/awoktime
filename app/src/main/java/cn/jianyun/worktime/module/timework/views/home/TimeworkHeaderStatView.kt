package cn.jianyun.worktime.module.timework.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.module.timework.dto.TimeworkStatData
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.radius

data class HomeStatItem(
    val title: String,
    val value: String
)

@Composable
fun TimeworkHeaderStatView(
    viewModel: TimeworkMasterViewModel,
    maskMoney: Boolean = false,
    onClick: () -> Unit = {}
) {

    val statItems = makeHomeStatItems(
        fields = viewModel.appConfig.normalizedHomeStatFields(),
        statData = viewModel.monthStatModel,
        maskMoney = maskMoney
    )

    Column(
        modifier = Modifier
            .radius(12.dp)
            .background(ThemeColor)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 12.dp)
    ) {
        HomeStatContent(
            statItems = statItems,
            preferredValueSize = viewModel.appConfig.normalizedHomeStatSize(),
            singleLine = viewModel.appConfig.homeStatSingleLine
        )
    }

}

@Composable
private fun HomeStatContent(
    statItems: List<HomeStatItem>,
    preferredValueSize: Int,
    singleLine: Boolean
) {
    val valueSize = when {
        statItems.size <= 2 -> preferredValueSize.coerceIn(12, 30)
        statItems.size == 3 -> preferredValueSize.coerceAtMost(28).coerceAtLeast(12)
        statItems.size == 4 -> preferredValueSize.coerceAtMost(24).coerceAtLeast(12)
        statItems.size == 5 -> preferredValueSize.coerceAtMost(22).coerceAtLeast(12)
        else -> preferredValueSize.coerceIn(12, 30)
    }
    val valueFontSize = valueSize.sp
    val titleFontSize = when {
        valueSize >= 26 -> 14.sp
        valueSize >= 22 -> 13.sp
        valueSize >= 18 -> 12.sp
        else -> 11.sp
    }
    val scrollItemWidth = when {
        valueSize >= 28 -> 150.dp
        valueSize >= 24 -> 136.dp
        valueSize >= 20 -> 122.dp
        valueSize >= 16 -> 108.dp
        else -> 94.dp
    }

    when {
        statItems.size <= 1 -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                statItems.firstOrNull()?.let {
                    statNumView(
                        title = it.title,
                        titleFontSize = titleFontSize,
                        fontSize = valueFontSize,
                        value = it.value
                    )
                }
            }
        }

        statItems.size == 2 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    statNumView(
                        title = statItems[0].title,
                        titleFontSize = titleFontSize,
                        fontSize = valueFontSize,
                        value = statItems[0].value
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    statNumView(
                        title = statItems[1].title,
                        titleFontSize = titleFontSize,
                        fontSize = valueFontSize,
                        value = statItems[1].value
                    )
                }
            }
        }

        statItems.size <= 4 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                statItems.forEach {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        statNumView(
                            title = it.title,
                            titleFontSize = titleFontSize,
                            fontSize = valueFontSize,
                            value = it.value
                        )
                    }
                }
            }
        }

        else -> {
            if(singleLine) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp)
                ) {
                    statItems.forEach {
                        Box(
                            modifier = Modifier
                                .width(scrollItemWidth)
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            statNumView(
                                title = it.title,
                                titleFontSize = titleFontSize,
                                fontSize = valueFontSize,
                                value = it.value
                            )
                        }
                    }
                }
            } else {
                HomeStatFlowLayout(
                    statItems = statItems,
                    titleFontSize = titleFontSize,
                    valueFontSize = valueFontSize
                )
            }
        }
    }
}

@Composable
private fun HomeStatFlowLayout(
    statItems: List<HomeStatItem>,
    titleFontSize: TextUnit,
    valueFontSize: TextUnit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        statItems.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        statNumView(
                            title = it.title,
                            titleFontSize = titleFontSize,
                            fontSize = valueFontSize,
                            value = it.value
                        )
                    }
                }
                repeat(4 - rowItems.size) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

private fun makeHomeStatItems(fields: String, statData: TimeworkStatData, maskMoney: Boolean): List<HomeStatItem> {
    return fields.split("^").mapNotNull {
        when(it){
            "baseHour" -> HomeStatItem("正班工时", formatHour(statData.baseHour))
            "overHour" -> HomeStatItem("加班工时", formatHour(statData.overHour))
            "totalHour" -> HomeStatItem("总工时", formatHour(statData.fetchTotalHour()))
            "dayHour" -> HomeStatItem("日结工时", formatHour(statData.dayHour))
            "awardMoney" -> HomeStatItem("补扣金额", formatSignedMoney(statData.awardMoney, maskMoney))
            "pureAwardMoney" -> HomeStatItem("补贴金额", formatMoney(statData.awardMoney, maskMoney))
            "fineMoney" -> HomeStatItem("扣款金额", formatMoney(statData.fineMoney, maskMoney))
            "dayMoney" -> HomeStatItem("日结收入", formatMoney(statData.dayMoney, maskMoney))
            "dayCount" -> HomeStatItem("日结次数", formatCount(statData.dayCount, "次"))
            "totalDay" -> HomeStatItem("出勤天数", formatCount("${statData.totalDay}", "天"))
            "totalMoney" -> HomeStatItem("总收入", formatMoney(statData.fetchTotalMoney(), maskMoney))
            "settledMoney" -> HomeStatItem("已结算", formatMoney(statData.settledMoney, maskMoney))
            "unSettledMoney" -> HomeStatItem("待结算", formatMoney(statData.unSettledMoney, maskMoney))
            else -> null
        }
    }.filter {
        SelectUtil.contains(SelectUtil.HOME_STAT_TYPES, findStatValue(it.title))
    }
}

private fun findStatValue(title: String): String {
    return when(title){
        "正班工时" -> "baseHour"
        "加班工时" -> "overHour"
        "总工时" -> "totalHour"
        "日结工时" -> "dayHour"
        "补扣金额" -> "awardMoney"
        "补贴金额" -> "pureAwardMoney"
        "扣款金额" -> "fineMoney"
        "日结收入" -> "dayMoney"
        "日结次数" -> "dayCount"
        "出勤天数" -> "totalDay"
        "总收入" -> "totalMoney"
        "已结算" -> "settledMoney"
        "待结算" -> "unSettledMoney"
        else -> ""
    }
}

private fun formatMoney(value: String, maskMoney: Boolean = false): String {
    if(maskMoney){
        return "**"
    }
    return if(value == "" || value == "0"){
        "0元"
    }
    else{
        MyDataTool.withUnit(value, 2, "元")
    }
}

private fun formatSignedMoney(value: String, maskMoney: Boolean = false): String {
    if(maskMoney){
        return "**"
    }
    return if(value == "" || value == "0"){
        "0元"
    }
    else{
        MyDataTool.withUnit(value, 2, "元")
    }
}

private fun formatCount(value: String, unit: String): String {
    return if(value == "" || value == "0"){
        "0$unit"
    }
    else{
        MyDataTool.withUnit(value, unit)
    }
}

private fun formatHour(value: String): String {
    return if(value == ""){
        "0h"
    }
    else{
        MyDataTool.getShownTime(value, true)
    }
}

@Composable
private fun statNumView(
    title: String,
    titleFontSize: TextUnit = 11.sp,
    fontSize: TextUnit = 15.sp,
    value: String,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.White, fontSize = titleFontSize, maxLines = 1)
        Text(value, color= Color.White, maxLines = 1, fontSize = fontSize, fontWeight = FontWeight.Medium)
    }
}
