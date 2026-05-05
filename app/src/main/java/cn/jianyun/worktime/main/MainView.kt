package cn.jianyun.worktime.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.main.setting.vip.VipView
import cn.jianyun.worktime.module.timework.views.TimeworkMainView
import cn.jianyun.worktime.ui.component.nav.AppLogoView
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.GlobalSecretCheckView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.vm.AppSettingViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView(navHostController: NavHostController, activity: Activity) {

    var appViewModel = hiltViewModel<AppSettingViewModel>()
    appViewModel.tryReload()

    Box(modifier= Modifier
        .fillMaxSize()){
        if(!appViewModel.initApp){
            CenterRow(modifier = Modifier.align(alignment = Alignment.BottomEnd).padding(bottom=20.dp)) {
                AppLogoView(size=40.dp)
                Blank(5.dp)
                Column(modifier=Modifier.padding(2.dp)) {
                    Text("极简记工时", fontSize = 14.sp, lineHeight = 20.sp)
                    Text("一个帮助你记工时的app", fontSize = 11.sp, lineHeight = 16.sp, color=Color.Gray)
                }
            }
        }
        else if(!appViewModel.baseRepository.isRealVip()){
            VipView(settingViewModel = appViewModel, activity = activity)
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

