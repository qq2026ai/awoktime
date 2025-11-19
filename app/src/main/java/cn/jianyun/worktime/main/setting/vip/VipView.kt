package cn.jianyun.worktime.main.setting.vip

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.setting.user.LoginDialog
import cn.jianyun.worktime.model.FormType
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
import cn.jianyun.worktime.util.goBack

@Composable
fun VipPage(navHostController: NavHostController, activity: Activity) {
    var appViewModel = hiltViewModel<AppSettingViewModel>()
    VipView(settingViewModel = appViewModel, activity = activity, navHostController=navHostController)
}

@Composable
fun VipView(settingViewModel: AppSettingViewModel, activity: Activity, navHostController: NavHostController? = null) {

    FullDialog(onDismiss = {
        if(navHostController != null){
            goBack(navHostController)
        }
        else{
            settingViewModel.reset()
        }
    }) {

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
        ) {
            GroupView(bottom = 0.dp) {
                Box(contentAlignment = Alignment.CenterEnd){
                    CenterRow(padding = 20.dp) {
                        VerticalRow {
                            Text("极简记工时", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Blank(5.dp)
                            TagView(tag = "会员", color = ThemeColor, round = true,  fontSize = 12.sp)
                        }
                    }
                    Text("×", fontSize = 30.sp, fontWeight = FontWeight.Light, color = Color.Gray, modifier = Modifier.offset(y= -20.dp).clickable {
                        if(navHostController != null){
                            goBack(navHostController)
                        }
                        else{
                            settingViewModel.reset()
                        }
                    }.padding(10.dp))
                }

                if(settingViewModel.baseRepository.appTipInfo.discount){
                    GroupView(modifier=Modifier.clickable {
                        var type = settingViewModel.baseRepository.appTipInfo.newPage
                        if(type == "web" && settingViewModel.baseRepository.appTipInfo.url != ""){
                            settingViewModel.baseRepository.openUrl(settingViewModel.baseRepository.appTipInfo.url)
                        }
                    }) {
                        Text("温馨提示:${settingViewModel.baseRepository.appTipInfo.message}",
                            modifier=Modifier.padding(end = 10.dp),
                            fontSize = 12.sp, color = settingViewModel.baseRepository.appTipInfo.showColor())
                    }
                }

                LeadingHintView("购买方案")
                Row(modifier=Modifier.fillMaxWidth()) {
                    PriceItemView(settingViewModel = settingViewModel, type = "month", price = "3元", tip = "每天1毛钱", modifier=Modifier.weight(1f))
                    Blank(10.dp)
                    PriceItemView(settingViewModel = settingViewModel, tag="优惠50%", tagColor = ImportantColor,   type = "year", price = "18元", tip = "每天5分钱", modifier=Modifier.weight(1f))
                    Blank(10.dp)
                    PriceItemView(settingViewModel = settingViewModel,  tag="最划算",  tagColor = ThemeColor, type = "forever", price = "36元", tip = "一次性买断", modifier=Modifier.weight(1f))
                }

                Blank()
                LeadingHintView("会员权益")
                VipItemView(label = "个人云端备份", icon = IconFont.cloud2, remark = "支持云盘备份，永远不会丢失数据")
                VipItemView(label = "开发者云备份", icon = IconFont.cloud2, remark = "会员自动备份，永远不会丢失数据")
                VipItemView(label = "快捷打卡", icon = IconFont.flashlight, remark = "普通用户最多建一个快捷打卡")
                VipItemView(label = "批量工时打卡", icon = IconFont.add2, remark = "批量设置多天打卡记录")
                VipItemView(label = "无限项目", icon = IconFont.infinite, remark = "普通用户最多建1个工时项目")
                VipItemView(label = "无限薪水及补扣", icon = IconFont.money, remark = "普通用户最多建5个薪水和5个补扣")
                VipItemView(label = "秘钥校验", icon = IconFont.password, remark = "打开APP进行秘钥校验，数据更安全")
                VipItemView(label = "数据统计", icon = IconFont.stat, remark = "支持年度及自定义时间统计")
                VipItemView(label = "数据导出", icon = IconFont.file_download, remark = "支持详细数据导出到Excel")
                VipItemView(label = "无广告干扰", icon = IconFont.image, remark = "彻底移除所有广告，专注记录工时")
                VipItemView(label = "专属客服", icon = IconFont.vip, remark = "会员用户优先响应")
                VipItemView(label = "支持开发", icon = IconFont.thumb, remark = "促使开发者砥砺前行，呈现更优质的App功能")
                Blank()

                Text("我是独立开发者，您的支持能够让我们坚持开发下去，也会让开发者更加有信心开发更多优质的内容和更多新的APP", fontSize = 12.sp)
                Text("请微信联系客服，微信号:yongbw2020", fontSize = 12.sp, color = ThemeColor)

                Blank()
                LongOkButton("微信联系：yongbw2020 购买会员") {
                    if(!settingViewModel.baseRepository.isLogin()){
                        settingViewModel.formType = FormType("login")
                        settingViewModel.baseRepository.toast("请先登录")
                        return@LongOkButton
                    }
                }
                Blank()
                LongCancelButton("返回") {
                    if(navHostController != null){
                        goBack(navHostController)
                    }
                    else{
                        settingViewModel.reset()
                    }
                }
            }
        }

        if(settingViewModel.formType.isForm("login")){
            LoginDialog(settingViewModel)
        }

    }
}


@Composable
fun PriceItemView(settingViewModel: AppSettingViewModel, tag: String = "", tagColor: Color= ImportantColor, type: String, price: String, tip: String, modifier:Modifier){
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
            TagView(tag = tag, round = true, color = tagColor, offsetY = (-8).dp)
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