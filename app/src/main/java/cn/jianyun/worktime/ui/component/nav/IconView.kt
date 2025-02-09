package cn.jianyun.worktime.ui.component.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.util.focusColor
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.theme.remixicon


@Composable
fun IconView(icon: Int, iconSize: TextUnit = 14.sp, color: Color = MaterialTheme.colorScheme.primary, bold: Boolean = false, modifier:Modifier=Modifier){
    Text(icon.toChar().toString(),modifier=modifier,lineHeight=10.sp, fontFamily = remixicon, fontSize = iconSize, color=color, fontWeight = ifv(bold, FontWeight.Medium, FontWeight.Normal))
}

@Composable
fun CircleIconView(icon: Int, iconSize: TextUnit = 14.sp, wrapperSize: Dp = 16.dp, color: Color = MaterialTheme.colorScheme.primary, bold: Boolean = false, modifier:Modifier=Modifier, modifier2:Modifier=Modifier){
    Box(modifier= Modifier
        .radius(wrapperSize / 2)
        .then(modifier2)
        .size(wrapperSize)
        .background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
        IconView(icon = icon, iconSize, color, bold, modifier)
    }
}


@Composable
fun FocusText(text: String, focus: Boolean, fontSize: TextUnit = 16.sp){
    Text(text, color= focusColor(flag = focus), fontSize=fontSize)
}


class IconFont{
    companion object {

        const val user = 0xf256
        const val check = 0xeb7b
        const val copy = 0xecd5
        const val box_none = 0xf3a7
        const val shear = 0xf0bf
        const val paste = 0xecef
        const val pencil = 0xefe0
        const val delete_bin = 0xec2a
        const val clear = 0xeb99
        const val move = 0xea70
        const val batchAdd = 0xf4c6
        const val add = 0xea13
        const val more1 = 0xef77
        const val more2 = 0xef79
        const val add2 = 0xed5a
        const val right = 0xea6e
        const val right2 = 0xea54
        const val right3 = 0xea53

        const val down = 0xea4e
        const val down2 = 0xea50
        const val down3 = 0xea4f


        const val school = 0xf35a
        const val back = 0xea60
        const val alert = 0xf385
        const val chart = 0xea96
        const val adjust = 0xea62
        const val calendar = 0xeb21
        const val todo = 0xeb23
        const val stat = 0xea9e
        const val alarm = 0xea1b
        const val info = 0xee59
        const val info2 = 0xf169
        const val sun = 0xf1bf
        const val product = 0xf3a7
        const val home = 0xee1f
        const val share = 0xf0fe
        const val file_download = 0xecd9
        const val vip = 0xf290
        const val password = 0xeece
        const val doc = 0xf199
        const val folder_download = 0xed60
        const val download = 0xec54
        const val notification = 0xef94
        const val computer =  0xebca
        const val palette = 0xefc5
        const val time = 0xf20f
        const val settings = 0xf0e4
        const val flashlight = 0xed3d
        const val lightbulb_flash = 0xeea8
        const val apps = 0xea42
        const val questionnaire = 0xf048
        const val sub = 0xf1af
        const val resize = 0xed8c
        const val list_indefinite = 0xf399
        const val mobile = 0xf15a
        const val thumb = 0xf207
        const val refresh = 0xf064


        const val image = 0xee4b
        const val award = 0xea8a
        const val money = 0xef61
        const val back2 = 0xea58
        const val remove = 0xec1a
        const val random = 0xf3fd
        const val gift = 0xedb9
        const val change = 0xf477
        const val catalog = 0xea42
        const val list = 0xf399
        const val search = 0xf3d1
        const val search2 = 0xf0d1
        const val filter = 0xed27
        const val cloud = 0xeb9d
        const val cloud2 = 0xf24e
        const val cloud3 = 0xf442
        const val infinite = 0xf3b9

        const val folder= 0xed6a
        const val file= 0xecc5
        const val file_image = 0xf3c5
        const val file_zip = 0xed1f

        const val arrow_right= 0xEA6C


    }
}


/*

{"user_3": "&#xf256"},
{"settings": "&#xf0ee"},
{"focus": "&#xed4e"},
{"calendar": "&#xeb27"},
{"calendar_check": "&#xeb23"},
{"more": "&#xef79"},
{"more_2": "&#xef77"},
{"pie_chart": "&#xeffa"},
{"line_chart": "&#xeeab"},
{"time": "&#xf20f"},
{"checkbox_circle": "&#xeb81"},
{"checkbox_blank_circle": "&#xeb7d"},
{"lock": "&#xeece"},
{"chat_1": "&#xeb4d"},
{"contrast_2": "&#xebd4"},
{"arrow_right_s": "&#xea6e"},
{"arrow_down_s": "&#xea4e"},
{"send_plane": "&#xf0da"},
{"edit": "&#xec86"},
{"apps": "&#xea44"},
{"palette": "&#xefc5"},
{"share": "&#xf0fe"},
{"home_5": "&#xee1f"},
{"add": "&#xea13"},
{"add_circle": "&#xea11"},
{"arrow_left_s": "&#xea64"},
{"prohibited": "&#xf3a1"},
{"magic": "&#xeeea"},
{"magic_fill": "&#xeee9"},
{"menu_add": "&#xef3a"},
{"checkbox": "&#xeb85"},
{"checkbox_blank": "&#xeb7f"},
{"more_2_fill": "&#xef76"},
{"check": "&#xeb7b"},
{"hourglass": "&#xf339"},
{"alarm": "&#xea1b"},
{"sun": "&#xf1bf"},
{"user_smile": "&#xf274"},
{"shield_user": "&#xf10c"},
{"money_cny_circle": "&#xef61"},
{"money_dollar_circle": "&#xef65"},
{"gift_2": "&#xedb9"},
{"exchange": "&#xecad"},
{"exchange_dollar": "&#xeca9"},
{"search": "&#xf0d1"},
{"user_search": "&#xf26c"},
{"sort_desc": "&#xf160"},
{"arrow_drop_up": "&#xea56"},
{"arrow_drop_down": "&#xea50"},
{"file_copy": "&#xecd5"},
{"article": "&#xea7e"},
{"arrow_down": "&#xea4c"},
{"font_size": "&#xed8d"},
{"arrow_right_up": "&#xea70"},
{"arrow_left_right": "&#xea62"},
{"close": "&#xeb99"},
{"close_circle": "&#xeb97"},
{"subtract": "&#xf1af"},
{"notification": "&#xef9a"},
{"information": "&#xee59"},
{"bookmark_fill": "&#xeae4"},
{"megaphone": "&#xf385"},
{"bookmark": "&#xeae5"},
{"calendar_todo": "&#xeb29"},
 */