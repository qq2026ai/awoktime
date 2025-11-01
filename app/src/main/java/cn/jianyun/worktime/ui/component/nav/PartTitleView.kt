package cn.jianyun.worktime.ui.component.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.theme.ThemeColor

@Composable
fun PartTitleView(title: String){

    Box(modifier = Modifier.padding(vertical = 15.dp)){
        Text(title, fontSize = 13.sp, lineHeight=13.sp, color= Color.White, modifier = Modifier
            .offset(2.dp, 2.dp)
            .radius(40.dp)
            .background("#F26B1F".color())
            .padding(horizontal = 12.dp, vertical = 6.dp))
        Text(title,  fontSize = 13.sp, lineHeight=13.sp,color= Color.White, modifier = Modifier
            .radius(40.dp)
            .background(ThemeColor)
            .padding(horizontal = 12.dp, vertical = 6.dp))
    }


}

@Composable
fun TagView(tag: String, fontSize: TextUnit = 11.sp, round: Boolean = false, offsetY: Dp = 0.dp, big:Boolean = false, color: Color= ThemeColor, hollow:Boolean = false, onClick: (() -> Unit)? = null) {
    var r = ifv(round, 30.dp, ifv(big, 6.dp, 4.dp))
    var h = ifv(round, 6.dp, ifv(big, 8.dp, 3.dp))
    var v = ifv(big, 3.dp, 1.dp)
    var f = ifv(big, 13.sp, 11.sp)
    if(hollow){
        Text(tag, fontSize = fontSize, lineHeight = f, color=color, modifier = Modifier
            .radius(r)
            .clickable(enabled = onClick != null){
                if(onClick != null){
                    onClick()
                }
            }
            .background(color.copy(0.2f))
            .padding(h, v))
    }
    else{
        Text(tag, fontSize = fontSize, fontWeight = ifv(round, FontWeight.Bold, FontWeight.Normal),  lineHeight = f, color=Color.White, modifier = Modifier
            .offset(y=offsetY)
            .radius(r)
            .clickable(enabled = onClick != null){
                if(onClick != null){
                    onClick()
                }
            }
            .background(color)
            .padding(h, v))
    }
}

@Composable
fun DialogTitleView(title: String, tip: String = "", rightTool: @Composable (() -> Unit)? = null) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween){
        VerticalRow {
            Text(title, fontWeight = FontWeight.Medium)
            if(tip != ""){
                Blank()
                Text("(${tip})", fontSize = 11.sp, color= ThemeColor)
            }
        }
        if(rightTool != null){
            rightTool()
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowTagView(options: List<String>, hpadding: Dp = 0.dp, unit: String = "", onClick: (String) -> Unit) {
    FlowRow(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = hpadding)
    ) {
        options.forEach{
            if(it.trim() != ""){
                Text(it + unit, modifier = Modifier
                    .padding(0.dp, 0.dp, 6.dp, 8.dp)
                    .radius(20.dp)
                    .clickable {
                        onClick(it.trim())
                    }
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp)
                    .height(28.dp)
                    .wrapContentSize(),
                    fontSize = 13.sp, lineHeight = 13.sp
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowTagView(value: String = "",singleLine: Boolean= false, big: Boolean = false, dialog: Boolean = false, options: List<SelectDO>,vpadding: Dp = 0.dp, hpadding: Dp = 0.dp, onClick: (String) -> Unit) {

    if(singleLine){
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(horizontal = hpadding, vertical = vpadding)) {
            options.forEach{
                Text(it.label, modifier = Modifier
                    .padding(end=ifv(big, 10.dp, 6.dp))
                    .radius(20.dp)
                    .clickable {
                        onClick(it.value)
                    }
                    .background(
                        ifv(
                            value == it.value,
                            ThemeColor,
                            ifv(
                                dialog,
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(horizontal = ifv(big, 16.dp, 6.dp))
                    .height(ifv(big, 32.dp, 28.dp))
                    .wrapContentSize(),
                    color = ifv(value == it.value, Color.White, MaterialTheme.colorScheme.primary),
                    fontSize = ifv(big , 15.sp, 12.sp), lineHeight = 13.sp
                )
            }
        }
    }
    else{
        FlowRow(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = hpadding, vertical = vpadding)
        ) {
            options.forEach{
                Text(it.label, modifier = Modifier
                    .padding(0.dp, 0.dp, ifv(big, 10.dp, 6.dp), ifv(big, 12.dp, 8.dp))
                    .radius(20.dp)
                    .clickable {
                        onClick(it.value)
                    }
                    .background(
                        ifv(
                            value == it.value,
                            ThemeColor,
                            ifv(
                                dialog,
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(horizontal = ifv(big, 20.dp, 12.dp))
                    .height(ifv(big, 40.dp, 28.dp))
                    .wrapContentSize(),
                    color = ifv(value == it.value, Color.White, MaterialTheme.colorScheme.primary),
                    fontSize =  ifv(big , 15.sp, 13.sp), lineHeight = 13.sp
                )
            }
        }
    }


}