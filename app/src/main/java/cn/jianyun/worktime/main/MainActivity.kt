package cn.jianyun.worktime.main

import TimeworkAppThemeView
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.jianyun.worktime.BuildConfig
import cn.jianyun.worktime.R
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.main.question.QuestionView
import cn.jianyun.worktime.main.setting.local.LocalBackupView
import cn.jianyun.worktime.main.setting.user.UserDetailView
import cn.jianyun.worktime.main.setting.vip.VipPage
import cn.jianyun.worktime.module.base.router.WebDAVRouter
import cn.jianyun.worktime.module.base.views.docs.PrivatePolicy
import cn.jianyun.worktime.module.base.views.docs.UserPolicy
import cn.jianyun.worktime.module.base.views.webdav.WebDAVUserEditView
import cn.jianyun.worktime.module.base.views.webdav.WebDAVUserManageView
import cn.jianyun.worktime.module.base.views.webdav.WebDavDataView
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.module.timework.views.TimeworkMainView
import cn.jianyun.worktime.module.timework.views.award.TimeworkAwardEditView
import cn.jianyun.worktime.module.timework.views.batch.BatchAddView
import cn.jianyun.worktime.module.timework.views.cloud.TimeworkCloudManageView
import cn.jianyun.worktime.module.timework.views.defaults.TimeworkDefaultConfigEditView
import cn.jianyun.worktime.module.timework.views.defaults.TimeworkDefaultConfigView
import cn.jianyun.worktime.module.timework.views.project.TimeworkProjectManageView
import cn.jianyun.worktime.module.timework.views.salary.TimeworkSalaryEditView
import cn.jianyun.worktime.module.timework.views.share.TimeworkShareView
import cn.jianyun.worktime.module.timework.views.stat.TimeworkDetailDataView
import cn.jianyun.worktime.module.timework.views.style.TimeworkAppConfigView
import cn.jianyun.worktime.module.timework.views.tool.TimeworkBatchSettleView
import cn.jianyun.worktime.module.timework.views.tool.ImportDataView
import cn.jianyun.worktime.ui.component.form.WelcomeDialog
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.ui.theme.WorktimeTheme
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.vm.AppSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel: ViewModel() {

    private val mutableStateFlow = MutableStateFlow(true)
    val isLoading = mutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            delay(100)
            mutableStateFlow.value = false
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var baseRepository: BaseRepository

    @Inject
    lateinit var timeworkService: TimeworkService

    override fun onDestroy() {
        //退出页面时，置空所以的Message
        super.onDestroy()
    }

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //启动定时刷新任务

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition{viewModel.isLoading.value}

        setContentView(R.layout.main)

        val composeView = findViewById<ComposeView>(R.id.container)
        composeView.setContent {
            // 在这里使用 Compose
            MainScreen(baseRepository, this)
        }

        // 使用 lifecycleScope 启动协程
        lifecycleScope.launch {
            // 此协程将在 MainActivity 的整个生命周期内运行
            // 当 Activity 销毁时，协程会自动取消
            try {
                ThemeColor = baseRepository.getCache("themeColor", "#45B787").color()
                baseRepository.initApp()
                timeworkService.makeNotifyAsync(force = false)
            } catch (e: Exception) {
                // 处理异常
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mlog("activity onResume")
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(baseRepository: BaseRepository, activity: MainActivity) {

    val navController = rememberNavController()
    var settingViewModel = hiltViewModel<AppSettingViewModel>()
    settingViewModel.tryReload()

    var hasRegist by remember {
        mutableStateOf(baseRepository.hasRegist())
    }


    WorktimeTheme(darkTheme = settingViewModel.appConfig.getDarkTheme()) {
        Scaffold(
            content = {
                NavHost(
                    navController = navController,
                    startDestination = Router.Home.route,
                    modifier = Modifier.padding(0.dp)
                ) {
                    composable(route = Router.Home.route) {
                        MainView(navController, activity)
                    }

                    /**
                     * 记工时
                     */
                    composable(route= Router.Time.route) {
                        TimeworkMainView(navController, activity)
                    }

                    composable(route= TimeworkRouter.TimeworkSalaryEdit.route){ entry->
                        TimeworkSalaryEditView(navHostController = navController, arguments = entry.arguments)
                    }

                    composable(route= TimeworkRouter.TimeworkAwardEdit.route){ entry->
                        TimeworkAwardEditView(navHostController = navController, arguments = entry.arguments)
                    }

                    composable(route= TimeworkRouter.TimeworkDefaultManage.route){
                        TimeworkDefaultConfigView(navHostController = navController)
                    }
                    composable(route= TimeworkRouter.TimeworkDefaultEdit.route){ entry->
                        TimeworkDefaultConfigEditView(navHostController = navController, arguments = entry.arguments)
                    }

                    composable(route= TimeworkRouter.TimeworkAppStyle.route){
                        TimeworkAppConfigView(navHostController = navController)
                    }

                    composable(route= TimeworkRouter.TimeworkAppTheme.route){
                        TimeworkAppThemeView(navHostController = navController)
                    }

                    composable(route= TimeworkRouter.TimeworkDetailData.route){ entry->
                        TimeworkDetailDataView(navHostController = navController, arguments = entry.arguments)
                    }

                    composable(route= TimeworkRouter.TimeworkBatchSettle.route){
                        TimeworkBatchSettleView(navHostController = navController)
                    }

                    composable(route= TimeworkRouter.TimeworkCloudManage.route){
                        TimeworkCloudManageView(navHostController = navController)
                    }

                    composable(route= TimeworkRouter.TimeworkShare.route){
                        TimeworkShareView(navHostController = navController)
                    }

                    composable(route= TimeworkRouter.TimeworkProjectManage.route){
                        TimeworkProjectManageView(navHostController = navController)
                    }


//
//                    /**
//                     * 用户反馈
//                     */
                    composable(route= Router.Question.route) {entry->
                        QuestionView(navHostController = navController, arguments = entry.arguments)
                    }

                    composable(route= Router.UserPolicy.route) {entry->
                        UserPolicy(navHostController = navController)
                    }

                    composable(route= Router.PrivatePolicy.route) {entry->
                        PrivatePolicy(navHostController = navController)
                    }
//                    composable(route= Router.Feedback.route) {entry->
//                        FeedbackView(navHostController = navController, arguments = entry.arguments)
//                    }
                    composable(route=Router.UserDetail.route){
                        UserDetailView(navHostController = navController)
                    }


                    composable(route = Router.WebDAV.route) { entry->
                        WebDavDataView(navHostController = navController, arguments = entry.arguments)
                    }

                    composable(route = Router.WebDAVManage.route) {
                        WebDAVUserManageView(navHostController = navController)
                    }

                    composable(route = WebDAVRouter.UserEdit.route) { entry->
                        WebDAVUserEditView(navHostController = navController, arguments = entry.arguments)
                    }

                    composable(route = Router.LocalBackup.route) {
                        LocalBackupView(navHostController = navController)
                    }

                    composable(route = Router.ImportData.route) {
                        ImportDataView(navHostController = navController)
                    }
                    composable(route = Router.BatchAdd.route) {
                        BatchAddView(navHostController = navController)
                    }
                    composable(route = Router.VipPage.route) {
                        VipPage(navHostController = navController, activity=activity)
                    }
                }
            }
        )

        if(!hasRegist) {
            WelcomeDialog(okAction = {
                activity.lifecycleScope.launch {
                    hasRegist = true
                    baseRepository.makeRegist()
                }
            })
        }

    }

}
