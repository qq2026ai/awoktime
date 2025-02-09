package cn.jianyun.worktime.util


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import cn.jianyun.worktime.ui.theme.ImportantColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.ui.theme.ImportantColor
import cn.jianyun.worktime.ui.theme.ThemeColor


val VERTICAL_GAP = 15.dp
val BORDER_RADIUS = 12.dp

@Stable
fun Modifier.bottomGap(
    gap: Dp = VERTICAL_GAP
) = this then Modifier.padding(bottom=gap)


@Stable
fun Modifier.radius(
    radius: Dp = BORDER_RADIUS
) = this then Modifier.clip(RoundedCornerShape(radius))

@Stable
fun GlanceModifier.radius(
    radius: Dp = BORDER_RADIUS
) = this then GlanceModifier.cornerRadius(radius)



@Composable
fun Modifier.mainBg(
    radius: Dp = BORDER_RADIUS,
    bg: Color = MaterialTheme.colorScheme.surface
) = this then Modifier
    .radius(radius)
    .background(bg)

@Composable
fun GlanceModifier.mainBg(
    radius: Dp = BORDER_RADIUS,
    opacity: Float = 1f
) = this then GlanceModifier.cornerRadius(radius).background(ColorProvider(MaterialTheme.colorScheme.surface.copy(alpha = opacity)))


@Composable
fun Modifier.test() = this then Modifier.background(randomColor())


@Composable
fun LinkText(label: String, padding: Dp = 0.dp, fontSize: TextUnit = 15.sp, color: Color = ThemeColor, action: (() -> Unit)?=null){
    Text(label, modifier = Modifier
        .radius(4.dp)
        .clickable(enabled = action != null) {
            if (action != null) {
                action()
            }
        }
        .padding(padding), color=color, fontSize = fontSize)
}

@Composable
fun WarnText(label: String, padding: Dp = 0.dp, fontSize: TextUnit = 15.sp, color: Color = ImportantColor, action: (() -> Unit)?=null){
     LinkText(label = label, padding = padding, fontSize=fontSize, color=color, action=action)
}


@Composable
fun GrayLinkText(label: String, padding: Dp = 0.dp, fontSize: TextUnit = 15.sp, action: () -> Unit){
    LinkText(label = label, padding=padding, color = MaterialTheme.colorScheme.tertiary, action=action)
}

@Composable
fun Blank(size:Dp = 10.dp){
    Spacer(Modifier.size(size))
}

@Composable
fun GlanceBlank(size:Dp = 10.dp){
    androidx.glance.layout.Spacer(GlanceModifier.size(size))
}

@Composable
fun BigBlank(size:Dp = 20.dp){
    Spacer(Modifier.size(size))
}

@Composable
fun GlanceOffsetView(x: Dp = 0.dp, y: Dp = 0.dp, modifier: GlanceModifier = GlanceModifier,  content:  @Composable () -> Unit) {
    androidx.glance.layout.Row(modifier = GlanceModifier.padding(start= x, top = y).then(modifier)) {
        content()
    }
}

@Composable
fun GlancePaddingView(start: Dp = 0.dp, top: Dp = 0.dp,end: Dp = 0.dp, bottom: Dp = 0.dp, modifier: GlanceModifier = GlanceModifier,  content:  @Composable () -> Unit) {
    androidx.glance.layout.Row(modifier = GlanceModifier.padding(start, top, end, bottom).then(modifier)) {
        content()
    }
}

@Composable
fun GlancePaddingView(horizontal: Dp = 0.dp, vertical: Dp = 0.dp, modifier: GlanceModifier = GlanceModifier,  content:  @Composable () -> Unit) {
    androidx.glance.layout.Row(modifier = GlanceModifier.padding(horizontal, vertical).then(modifier)) {
        content()
    }
}



