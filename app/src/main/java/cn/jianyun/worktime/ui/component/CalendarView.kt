package cn.qsfty.worktime.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.test


@Composable
fun CalendarHeaderView(mondayFirst: Boolean=true) {
    val weeks = MyDateTool.getMonthHeaderInfo(mondayFirst)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        weeks.forEach{
            Text(text = it,
                modifier=Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray,
                 fontWeight = FontWeight.Normal)
        }
    }
}

@Composable
fun VerticalView(text: String, color:Color=Color.Gray,  fontSize: TextUnit) {
    val words = text.split("").filter{it != ""}.joinToString("\n")
//    Column {
//        words.forEach{
//            Text(it, color=color, lineHeight = 6.sp, fontSize= fontSize)
//        }
//    }
    Text(words, color=color, lineHeight = 10.sp, fontSize= fontSize)
}