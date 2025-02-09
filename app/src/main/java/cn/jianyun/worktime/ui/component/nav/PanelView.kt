package cn.jianyun.worktime.ui.component.nav

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.ZeroGroupView
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.theme.ThemeColor

@Composable
fun PanelView(title: String,content: String = "", dialog: Boolean = false, tip: Boolean = false,  color: Color = ThemeColor, opened: Boolean = false, body: (@Composable () -> Unit)? = null){

    var open by remember {
        mutableStateOf(opened)
    }

    ZeroGroupView (dialog=dialog){

        if(tip){
            Text(title, color=ifv(open, color, Color.Gray), fontSize = 12.sp, modifier=Modifier.tap {
                open = !open
            })
        }
        else{
            Row(modifier = Modifier
                .radius(6.dp)
                .clickable {
                    open = !open
                }
                .fillMaxWidth()
                .height(38.dp)
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                Text(title, color=ifv(open, color, MaterialTheme.colorScheme.primary), fontSize = 15.sp)
                IconView(icon = ifv(open, IconFont.down, IconFont.right), color=ifv(open, color, MaterialTheme.colorScheme.tertiary))
            }
        }

        if(open){
            GroupView(horizonPadding = ifv(tip, 0.dp, 15.dp), dialog = dialog) {
                if(body == null){
                    content.split("\n").forEach {
                        Text(it,  fontSize = 13.sp, color=ifv(tip, Color.Gray, MaterialTheme.colorScheme.primary))
                        Blank(3.dp)
                    }
                }
                else{
                    body()
                }
            }
        }
    }

}