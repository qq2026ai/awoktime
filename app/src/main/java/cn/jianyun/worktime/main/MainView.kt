package cn.jianyun.worktime.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.glance.layout.Spacer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.views.TimeworkMainView
import cn.jianyun.worktime.ui.component.nav.AppLogoView
import cn.jianyun.worktime.ui.component.nav.GlobalSecretCheckView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.mainBg
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.vm.AppSettingViewModel
import cn.jianyun.worktime.ui.component.nav.CenterColumn
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.IconFont.Companion.back
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.goBack

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView(navHostController: NavHostController, activity: Activity) {

    var appViewModel = hiltViewModel<AppSettingViewModel>()
    appViewModel.tryReload()

    Box(modifier= Modifier
        .fillMaxSize()){
        if(appViewModel.userApps.isEmpty()){
            CenterRow(modifier = Modifier.align(alignment = Alignment.BottomEnd).padding(bottom=20.dp)) {
                AppLogoView(size=40.dp)
                Blank(5.dp)
                Column(modifier=Modifier.padding(2.dp)) {
                    Text("极简记工时", fontSize = 14.sp, lineHeight = 20.sp)
                    Text("一个帮助你记工时的app", fontSize = 11.sp, lineHeight = 16.sp, color=Color.Gray)
                }
            }
        }
        else{
            TimeworkMainView(navHostController = navHostController, activity = activity)
        }
        if(appViewModel.formType.isForm("check")){
            GlobalSecretCheckView{
                appViewModel.formType = FormType("")
            }
        }

    }



}


@Composable
fun PlanItem(title: String, subTitle: String, destination: String, onTap: () -> Unit) {

    mlog("RouterInfo", title, destination)

    Box(modifier= Modifier
        .fillMaxWidth(1f)
        .padding(5.dp)
        .height(80.dp)
        .mainBg(10.dp)
        .clickable {
            onTap()
        }
        .padding(start = 15.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(title, fontSize = 20.sp, modifier = Modifier.zIndex(100f))
            Text(subTitle, fontSize = 12.sp, color= Color.Gray.copy(0.8f))
        }
    }
}



val APPS = listOf(
//    SelectDO("代办事项", Router.TodoList.route, "Todo List"),
    SelectDO("密码管理器", Router.Password.route, "Password Manager"),
    SelectDO("记工时", Router.Time.route, "Time Recording"),
    SelectDO("排班计划", Router.Schedule.route, "Schedule Plan"),
    SelectDO("人情记账", Router.Gift.route, "Gift Manager"),
//    SelectDO("工厂计件", Router.Piecework.route,  "Piece Work"),
    SelectDO("倒数纪念日", Router.Countdown.route, "Days Master"),
//    SelectDO("记借钱", Router.Borrow.route, "Borrow Manager"),
//    SelectDO("我的日记", Router.Diary.route, "Diary Manager"),
//    SelectDO("旅游计划", Router.Travel.route, "Travel Plan"),
)



