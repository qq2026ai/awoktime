package cn.jianyun.worktime.main.setting.vip

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.ui.component.form.FullDialog
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mainBg
import cn.jianyun.worktime.vm.AppSettingViewModel
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SelfDialog
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ImportantColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.views.base.PrivatePolicyView


@Composable
fun VipView(settingViewModel: AppSettingViewModel, activity: Activity) {

    FullDialog(onDismiss = {
        settingViewModel.reset()
    }) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
        ) {
            GroupView {
                CenterRow(padding = 20.dp) {
                    VerticalRow {
                        Text("极简记工时", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Blank(5.dp)
                        TagView(tag = "Pro", color = ThemeColor, round = true,  fontSize = 16.sp)
                    }
                }

                LeadingHintView("购买方案")
                Row(modifier=Modifier.fillMaxWidth()) {
                    PriceItemView(settingViewModel = settingViewModel, type = "month", price = "3元", tip = "每天1毛钱", modifier=Modifier.weight(1f))
                    Blank(10.dp)
                    PriceItemView(settingViewModel = settingViewModel, tag="优惠50%", type = "year", price = "18元", tip = "每天5分钱", modifier=Modifier.weight(1f))
                    Blank(10.dp)
                    PriceItemView(settingViewModel = settingViewModel, type = "forever", price = "38元", tip = "一次性买断", modifier=Modifier.weight(1f))
                }

                Blank()
                LeadingHintView("会员权益")

                VipItemView(label = "支持开发", icon = IconFont.thumb, remark = "促使开发者砥砺前行，呈现更优质的App功能")
                VipItemView(label = "无广告干扰", icon = IconFont.image, remark = "彻底移除所有广告，专注记录工时")
                VipItemView(label = "云端备份", icon = IconFont.cloud2, remark = "支持云盘备份，永远不会丢失数据")
                VipItemView(label = "无限项目", icon = IconFont.infinite, remark = "项目数量不受限制")
                VipItemView(label = "快捷打卡", icon = IconFont.flashlight, remark = "工时打卡快人一步")
                VipItemView(label = "秘钥校验", icon = IconFont.password, remark = "数据更安全")
                VipItemView(label = "数据统计", icon = IconFont.stat, remark = "多维度数据统计")
                VipItemView(label = "数据导出", icon = IconFont.file_download, remark = "即将支持详细报表导出")
                VipItemView(label = "专属客服", icon = IconFont.vip, remark = "会员用户优先响应")

                Blank()

                Text("我是独立开发者，您的支持能够让我们坚持开发下去，也会让我们更加有信心开发更多优质的内容和更多新的APP")

                Blank()
                LongOkButton("确认购买") {
                    settingViewModel.doPurchase(activity)
                }
                Blank()
                LongCancelButton("取消购买") {
                    settingViewModel.reset()
                }
            }
        }


    }
}


@Composable
fun PriceItemView(settingViewModel: AppSettingViewModel, tag: String = "", type: String, price: String, tip: String, modifier:Modifier){
    Box(contentAlignment = Alignment.TopCenter, modifier=Modifier.then(modifier)) {
        Column(modifier= Modifier
            .border(
                3.dp,
                ifv(settingViewModel.currentMode == type, ThemeColor, Color.Gray.copy(0.5f)),
                RoundedCornerShape(28.dp)
            )
            .tap {
                settingViewModel.currentMode = type
            }
            .padding(vertical = 10.dp)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(SelectUtil.getLabel(SelectUtil.VIP_TYPES, type), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Blank(3.dp)
            Text(price, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Blank(3.dp)
            Text(tip, fontSize = 11.sp, color = Color.Gray)
        }

        if(tag != ""){
            TagView(tag = tag, round = true, color = ImportantColor, offsetY = (-8).dp)
        }

    }
}

@Composable
fun VipItemView(label: String, icon: Int,remark: String){

    Row(modifier= Modifier
        .padding(vertical = 5.dp)
        .mainBg(12.dp, MaterialTheme.colorScheme.surfaceVariant)
        .padding(10.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        IconView(icon = icon, iconSize = 30.sp, color= ThemeColor)
        Blank(10.dp)

        Column {
            Text(label, fontWeight = FontWeight.Medium)
            Text(remark, color=Color.Gray, fontSize = 12.sp)
        }

    }
}