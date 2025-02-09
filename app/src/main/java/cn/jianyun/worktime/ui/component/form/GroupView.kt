package cn.jianyun.worktime.ui.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.util.VERTICAL_GAP
import cn.jianyun.worktime.util.bottomGap
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.theme.ThemeColor


@Composable
fun GroupView(modifier:Modifier =Modifier, dialog: Boolean = false, focus: Boolean = false, radius: Dp = 10.dp, startPadding:Dp =0.dp, horizonPadding: Dp = 10.dp, verticalPadding: Dp = 5.dp, bottom: Dp = 10.dp, content: @Composable () -> Unit){

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = bottom)
        .border(
            ifv(focus, 2.dp, 0.dp),
            ifv(focus, ThemeColor, MaterialTheme.colorScheme.background),
            RoundedCornerShape(radius)
        )
        .radius(radius)
        .then(modifier)
        .background(ifv(dialog, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))
        .padding(start=startPadding)
        .padding(horizontal = horizonPadding, vertical = verticalPadding)) {
        content()
    }
}

@Composable
fun SettingGroupView(modifier:Modifier =Modifier, dialog: Boolean = false, focus: Boolean = false, radius: Dp = 10.dp,startPadding:Dp =5.dp, horizonPadding: Dp = 10.dp, verticalPadding: Dp = 5.dp, bottom: Dp = 10.dp, content: @Composable () -> Unit){
    GroupView(modifier,dialog, focus, radius,startPadding, horizonPadding, verticalPadding, bottom, content)
}


@Composable
fun CenterGroupView(modifier:Modifier =Modifier, focus: Boolean = false, radius: Dp = 10.dp, horizonPadding: Dp = 10.dp, verticalPadding: Dp = 5.dp, bottom: Dp = 10.dp, content: @Composable () -> Unit){

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = bottom)
        .border(
            ifv(focus, 2.dp, 0.dp),
            ifv(focus, ThemeColor, MaterialTheme.colorScheme.background),
            RoundedCornerShape(radius)
        )
        .radius(radius)
        .then(modifier)
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = horizonPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.Center
    ) {
        content()
    }
}


@Composable
fun ZeroGroupView(horizonPadding: Dp = 0.dp, dialog: Boolean = false, content: @Composable () -> Unit){
    GroupView(horizonPadding = horizonPadding, dialog = dialog, verticalPadding = 0.dp, content = content)
}


@Composable
fun GroupTitleView(title: String,bottom: Dp = VERTICAL_GAP, horizonPadding:Dp = 0.dp, verticalPadding:Dp = 10.dp, tool: @Composable (() -> Unit)? = null, content: @Composable () -> Unit) {
    Column(modifier=Modifier.bottomGap(gap=bottom)) {
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 2.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Text(title, fontSize = 12.sp, color = Color.Gray)
            if(tool != null){
                tool()
            }
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizonPadding, vertical = verticalPadding)) {
            content()
        }
    }
}


//两列布局，左右拉满
