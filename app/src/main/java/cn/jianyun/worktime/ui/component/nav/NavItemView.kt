package cn.jianyun.worktime.ui.component.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.R
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.theme.ThemeColor


@Composable
fun NavItemView(label: String, icon:Int, focus:Boolean, modifier:Modifier=Modifier, onClick: () -> Unit){
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        .then(modifier)
        .radius(8.dp)
        .clickable {
            onClick()
        }
        .padding(vertical = 10.dp)) {
        IconView(icon = icon,  iconSize=20.sp,  color= ifv(focus, ThemeColor, Color.Gray), bold = true)
        Text(label, fontSize = 10.sp, lineHeight = 10.sp, color= ifv(focus, ThemeColor, Color.Gray), fontWeight = FontWeight.Medium)
    }

}



@Composable
fun AppLogoView(size: Dp = 20.dp, icon:Int =R.mipmap.ic_launcher){
    Image(painter = painterResource(icon), contentDescription = "", modifier= Modifier
        .radius(8.dp)
        .size(size))
}