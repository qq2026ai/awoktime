package cn.jianyun.worktime.module.timework.views



import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.views.award.TimeworkAwardView
import cn.jianyun.worktime.module.timework.views.home.TimeworkHomeView
import cn.jianyun.worktime.module.timework.views.salary.TimeworkSalaryView
import cn.jianyun.worktime.module.timework.views.setting.TimeworkSettingView
import cn.jianyun.worktime.module.timework.views.stat.TimeworkStatView
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.nav.NavItemView
import cn.jianyun.worktime.ui.component.nav.IconFont


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkMainView(navHostController: NavHostController, activity: Activity) {

    var viewModel = hiltViewModel<TimeworkMasterViewModel>()

    Scaffold (content = {
        Box(modifier= Modifier, contentAlignment = Alignment.BottomCenter){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .background(MaterialTheme.colorScheme.background)) {

                if(viewModel.isHomePage()){
                    TimeworkHomeView(navHostController)
                }

                if(viewModel.isAwardPage()){
                    TimeworkAwardView(navHostController)
                }

                if(viewModel.isStatPage()){
                    TimeworkStatView(navHostController)
                }

                if(viewModel.isSalaryPage()){
                    TimeworkSalaryView(navHostController)
                }

                if(viewModel.isSettingPage()) {
                    TimeworkSettingView(navHostController, activity = activity)
                }

            }

            Row(modifier= Modifier.background(MaterialTheme.colorScheme.background)) {
                NavItemView(label="工时", icon= IconFont.home, focus=viewModel.isHomePage(), onClick = {
                    viewModel.pageType = FormType(type = "home")
                }, modifier = Modifier.weight(1f))

                NavItemView(label="统计", icon= IconFont.stat, focus=viewModel.isStatPage(), onClick = {
                    viewModel.pageType = FormType(type = "stat")
                }, modifier = Modifier.weight(1f))

                NavItemView(label="薪水", icon= IconFont.money, focus=viewModel.isSalaryPage(), onClick = {
                    viewModel.pageType = FormType(type = "salary")
                }, modifier = Modifier.weight(1f))

                NavItemView(label="补扣", icon= IconFont.award, focus=viewModel.isAwardPage(), onClick = {
                    viewModel.pageType = FormType(type = "award")
                }, modifier = Modifier.weight(1f))

                NavItemView(label="设置", icon= IconFont.settings, focus=viewModel.isSettingPage(), onClick = {
                    viewModel.pageType = FormType(type = "setting")
                }, modifier = Modifier.weight(1f))

            }
        }
    })

}