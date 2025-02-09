package cn.jianyun.worktime.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import lombok.Data
import java.util.Date


@Data
data class MonthDateInfo(
    var date: Date = MyDateTool.make("1930", "10", "1"),
    var day:String = "",
    var lunarDay:String = "",
    var today:Boolean = false,
    var empty:Boolean = true,
    var holiday:Boolean = false,
    var work:Boolean = false
){
    @Composable
    fun fetchDayColor(): Color {
        if(this.today){
            return DeleteColor
        }
        return MaterialTheme.colorScheme.primary
    }

    fun fetchLunarColor(): Color {
        if(this.holiday){
            return ThemeColor
        }
        if(this.work){
            return PrimaryColor
        }
        return Color.Gray
    }

}
