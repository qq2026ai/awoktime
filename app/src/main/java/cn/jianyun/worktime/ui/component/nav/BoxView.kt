package cn.jianyun.worktime.ui.component.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.model.kt.MenuItem
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius


@Composable
fun TwoColumnView(modifier: Modifier = Modifier,
                  padding: Dp=0.dp,
                  vpadding: Dp=0.dp,
                  content: @Composable () -> Unit
               ) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)
        .padding(padding)
        .padding(vertical = vpadding)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}


@Composable
fun CenterRow(modifier: Modifier = Modifier, padding: Dp=0.dp, paddingBottom: Dp=0.dp,
                  content: @Composable () -> Unit
) {
    Row(modifier = Modifier
        .then(modifier)
        .fillMaxWidth()
        .padding(padding)
        .padding(bottom = paddingBottom)
        ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}


@Composable
fun CenterColumn(modifier: Modifier = Modifier, padding: Dp=0.dp, paddingBottom: Dp=0.dp,
              content: @Composable () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(padding)
        .padding(bottom = paddingBottom)
        .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
fun VerticalRow(modifier: Modifier = Modifier, padding: Dp=0.dp,vpadding: Dp=0.dp,
              content: @Composable () -> Unit
) {
    Row(modifier = Modifier
        .radius(3.dp)
        .then(modifier)
        .padding(vertical = vpadding)
        .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}


@Composable
fun FullRow(modifier: Modifier = Modifier, padding: Dp=0.dp, content: @Composable () -> Unit){
    Row(modifier = Modifier
        .radius(3.dp)
        .then(modifier)
        .padding(padding)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun WithUnitView(modifier: Modifier = Modifier, padding: Dp=0.dp, text:String, unit:String
) {
    Row(modifier = Modifier
        .radius(3.dp)
        .then(modifier)
        .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(ifv(text == "" || text == "无", "", text + unit))
    }
}

@Composable
fun DeleteText(label: String = "删除记录", onClick: () -> Unit) {
    CenterRow(padding = 8.dp) {
        Text(label, fontSize = 12.sp, color= Color.Gray, modifier = Modifier
            .radius(4.dp)
            .clickable {
                onClick()
            }
            .padding(5.dp, 2.dp))
    }
}

@Composable
fun TwoRowView(modifier: Modifier = Modifier,
               line1: String,
               line1Size: TextUnit = 13.sp,
               color1: Color= MaterialTheme.colorScheme.primary,
               line2: String,
               line2Size: TextUnit = 13.sp,
               color2: Color= MaterialTheme.colorScheme.primary) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(line1,  color= color1, fontSize= line1Size)
        Text(line2, color= color2, fontSize= line2Size)
    }
}

@Composable
fun TwoRowViewWithIcon(modifier: Modifier = Modifier,
               icon: Int,
               iconSize: TextUnit = 13.sp,
               iconColor: Color= MaterialTheme.colorScheme.primary,
               line2: String,
               line2Size: TextUnit = 13.sp,
               color2: Color= MaterialTheme.colorScheme.primary,
                       spacing: Dp=2.dp) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        IconView(icon = icon,  iconSize, iconColor)
        Blank(spacing)
        Text(line2,  lineHeight=10.sp, color= color2, fontSize= line2Size, textAlign = TextAlign.Center)
    }
}


@Composable
fun LeadingHintView(label: String, left: Dp = 0.dp,  color: Color = MaterialTheme.colorScheme.tertiary, fontSize: TextUnit = 14.sp, other: @Composable (() -> Unit)? = null){
    Row(modifier = Modifier
        .padding(bottom = 5.dp, start = left, end = left)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(label, fontSize= fontSize, color = color)
        if(other != null){
            other!!()
        }
    }
}


@Composable
fun SmallLinkText(text:String, padding: Dp =2.dp, color:Color= ThemeColor, onClick: () -> Unit) {
    Text(text, color=color, fontSize = 12.sp, modifier= Modifier
        .radius(2.dp)
        .clickable {
            onClick()
        }
        .padding(padding))
}

@Composable
fun DeleteLinkText(text:String, padding: Dp =2.dp, color:Color= DeleteColor, onClick: () -> Unit) {

    var delete by remember{
        mutableStateOf(false)
    }

    Box {
        Text(text, color=color, fontSize = 12.sp, modifier= Modifier
            .radius(2.dp)
            .clickable {
                delete = true
            }
            .padding(padding))
        if(delete){
            DeleteDialog(okAction = {
                onClick()
            }) {
                delete = false
            }
        }
    }
}

@Composable
fun SmallTipText(text:String, color:Color= Color.Gray,hpadding:Dp =0.dp, padding:Dp = 0.dp,modifier:Modifier=Modifier, onClick: (() -> Unit)? = null) {
    if(onClick != null){
        Text(text, color=color, fontSize = 12.sp, lineHeight = 12.sp, modifier= Modifier
            .then(modifier)
            .tap{
                onClick()
            }
            .padding(padding)
            .padding(horizontal = hpadding))
    }
    else{
        Text(text, color=color, fontSize = 12.sp, lineHeight = 12.sp, modifier= Modifier
            .then(modifier)
            .padding(padding)
            .padding(horizontal = hpadding))
    }

}

@Composable
fun OnlySmallTipText(text:String, maxLines: Int = 10, fontSize:TextUnit = 12.sp, color:Color= Color.Gray,padding:Dp = 0.dp,modifier:Modifier=Modifier) {
    Text(text, color=color, maxLines = maxLines, fontSize=fontSize, lineHeight = fontSize, modifier= Modifier
        .then(modifier)
        .padding(padding))
}


@Composable
fun CenterSmallTipText(text:String, color:Color= Color.Gray, padding: Dp = 10.dp, onClick: (() -> Unit)? = null) {
    CenterRow(padding = padding) {
        SmallTipText(text = text, color, onClick = onClick)
    }
}


@Composable
fun HintText(label: String, color: Color = MaterialTheme.colorScheme.tertiary, fontSize: TextUnit = 12.sp){
    Text(label, fontSize= fontSize, color = color)
}


@Composable
fun EmptyDataView(label: String, color: Color = MaterialTheme.colorScheme.tertiary, fontSize: TextUnit = 14.sp, onClick: (() -> Unit)? = null){

    Column( modifier = Modifier
        .tap {
            if (onClick != null) {
                onClick()
            }
        }
        .fillMaxWidth()
        .height(200.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Text(label, fontSize= fontSize, color = color)
    }
    
}

@Composable
fun EmptyIconDataView(label: String,icon: Int = IconFont.box_none, color: Color = MaterialTheme.colorScheme.tertiary, fontSize: TextUnit = 14.sp, onClick: (() -> Unit)? = null){

    GroupView(modifier=Modifier.clickable(enabled = onClick != null) {
        onClick?.let { it() }
    }) {
        Blank(20.dp)
        CenterRow(padding = 10.dp) {
            IconView(icon = icon, iconSize = 30.sp, color=Color.Gray)
        }
        CenterRow {
            Text(label, color= Color.Gray, fontSize=12.sp)
        }
        Blank(20.dp)
    }


}



@Composable
fun BoxView(label: String = "", color: Color = MaterialTheme.colorScheme.primary, width: Dp, height: Dp, background: Color, radius: Dp) {
    Text(label,
        color = color,
        lineHeight = 10.sp,
        modifier = Modifier
            .radius(radius)
            .background(background)
            .width(width)
            .height(height)
            .wrapContentSize()
    )

}


@Composable
fun FixSizeView(width: Dp, height: Dp, radius:Dp, modifier:Modifier = Modifier, horizontalArr: Arrangement.Horizontal = Arrangement.Center, content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .width(width)
        .height(height)
        .radius(radius)
        .then(modifier), horizontalArr, verticalAlignment = Alignment.CenterVertically){
        content()
    }
}

@Composable
fun FixCircleSizeView(size: Dp, modifier:Modifier = Modifier, horizontalArr: Arrangement.Horizontal = Arrangement.Center, content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .width(size)
        .height(size)
        .radius(size / 2)
        .then(modifier), horizontalArr, verticalAlignment = Alignment.CenterVertically){
        content()
    }
}


@Composable
fun FixCircleIconSizeView(icon: Int, size: Dp, modifier:Modifier = Modifier, horizontalArr: Arrangement.Horizontal = Arrangement.Center) {
    Row(modifier = Modifier
        .width(size)
        .height(size)
        .radius(size / 2)
        .then(modifier), horizontalArr, verticalAlignment = Alignment.CenterVertically){
        IconView(icon = icon, iconSize = 20.sp)
    }
}

@Composable
fun HeaderIcon(icon: Int, size: Dp = 36.dp,fontSize: TextUnit = 20.sp,  onClick: () -> Unit) {
    Row(modifier = Modifier
        .width(size)
        .height(size)
        .radius(size / 2)
        .clickable {
            onClick()
        }, Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        IconView(icon = icon, iconSize = fontSize, color=MaterialTheme.colorScheme.primary.copy(0.8f))
    }
}

@Composable
fun HeaderIcon2(icon: Int, size: Dp = 36.dp,fontSize: TextUnit = 20.sp,  onClick: () -> Unit) {
    Row(modifier = Modifier
        .width(size)
        .height(size)
        .radius(size / 2)
        .clickable {
            onClick()
        }, Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        IconView2(icon = icon, iconSize = fontSize, color=MaterialTheme.colorScheme.primary.copy(0.8f))
    }
}



@Composable
fun HeaderMoreMenu(menus: List<MenuItem>) {

    var expanded by remember {
        mutableStateOf(false)
    }

    HeaderIcon(icon = IconFont.more1) {
        expanded = true
    }

    if(expanded){
        DropdownMenu(
            modifier = Modifier
                .radius(0.dp)
                .background(MaterialTheme.colorScheme.surface),
            expanded = expanded,
            onDismissRequest = { expanded = false }  // 菜单外点击关闭
        ) {
            // 菜单项
            menus.forEach {
                DropdownMenuItem(modifier=Modifier.height(50.dp), onClick = {
                    expanded = false
                    it.action()
                }) {
                    VerticalRow {
                        IconView(icon = it.icon)
                        Blank()
                        Text(it.name, fontSize = 13.sp, lineHeight = 13.sp,  color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

}



@Composable
fun HeaderTextTool(text: String, size: Dp = 36.dp,fontSize: TextUnit = 12.sp,  onClick: () -> Unit) {
    Row(modifier = Modifier
        .width(size)
        .height(size)
        .radius(size / 2)
        .clickable {
            onClick()
        }, Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        Text(text, fontSize = fontSize, color=MaterialTheme.colorScheme.primary.copy(0.8f))
    }
}

@Composable
fun FixWidthView(width: Dp,modifier:Modifier = Modifier, horizontalArr: Arrangement.Horizontal = Arrangement.Center, content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .width(width)
        .fillMaxHeight()
        .then(modifier), horizontalArr, verticalAlignment = Alignment.CenterVertically){
        content()
    }
}

@Composable
fun FixHeightView(height: Dp, content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .height(height)
        .fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        content()
    }
}


@Composable
fun CircleBoxView(size: Dp, background: Color, action: () -> Unit, content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .width(size)
        .height(size)
        .radius(size / 2)
        .clickable {
            action()
        }
        .background(background), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        content()
    }
}
