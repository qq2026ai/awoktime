package cn.jianyun.worktime.main

import TimeworkAppThemeView
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import cn.admobiletop.adsuyi.ADSuyiSdk
import cn.admobiletop.adsuyi.ad.ADSuyiSplashAd
import cn.admobiletop.adsuyi.ad.data.ADSuyiSplashAdInfo
import cn.admobiletop.adsuyi.ad.error.ADSuyiError
import cn.admobiletop.adsuyi.ad.listener.ADSuyiSplashAdListener
import cn.admobiletop.adsuyi.config.ADSuyiInitConfig
import cn.admobiletop.adsuyi.listener.ADSuyiInitListener
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
import cn.jianyun.worktime.module.timework.views.tool.ImportDataView
import cn.jianyun.worktime.ui.component.form.WelcomeDialog
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.ui.theme.WorktimeTheme
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.vm.AppSettingViewModel
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.CSJAdError
import com.bytedance.sdk.openadsdk.CSJSplashAd
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTCustomController
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pro.dxys.ad.AdSdk
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

    lateinit var adSuyiSplashAd: ADSuyiSplashAd
    lateinit var dyAd: TTAdNative

    var initAppTime = 0L

    var dy_app_id = ifv(BuildConfig.IS_DEV, "5768810", "5768810")
    var dy_ad_id = ifv(BuildConfig.IS_DEV, "103821515", "103821515")

    override fun onDestroy() {
        //退出页面时，置空所以的Message
        super.onDestroy()
    }

    var lastAdTime: Long = 0L
    var recoverFlag: Boolean = false
    var initDy: Boolean = false
    var initSy: Boolean = false

    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //启动定时刷新任务

        initAppTime = System.currentTimeMillis()

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
                if(baseRepository.loginUser.username == "15068790467"){
                    baseRepository.toast(dy_app_id + ":" + dy_ad_id)
                }
                loadAds(false)
            } catch (e: Exception) {
                // 处理异常
            }
        }
    }

    fun loadAds(recover: Boolean = false, mustAd: Int = 0){

        mlog("gapAdsTime", System.currentTimeMillis() - initAppTime)
        if(baseRepository.loginUser.username != "15068790467"){
            if(baseRepository.isAdVip() && !BuildConfig.IS_DEV){
                return
            }
        }

        AdSdk.initAdn(getApplication(),"jjjgs");

        if(BuildConfig.IS_DEV){
            baseRepository.appTipInfo.adsValue = "1:1111"
            baseRepository.appTipInfo.recoverAdsValue = "1:1111"
            baseRepository.appTipInfo.screenAdsGapMinute = 0
            baseRepository.appTipInfo.openRecover = false
        }
        mlog("gapAds", (System.currentTimeMillis() - lastAdTime) / 1000, baseRepository.appTipInfo.screenAdsGapMinute)

        if(System.currentTimeMillis() - lastAdTime < 1000 * baseRepository.appTipInfo.screenAdsGapMinute * 60){
            return
        }

//        if(System.currentTimeMillis() - lastAdTime < 1000 * 5){
//            return
//        }
        lastAdTime = System.currentTimeMillis()
            //随机广告
        if(mustAd == 0){
            val adMode = ifv(recover, baseRepository.fetchRecoverAdsValue(), baseRepository.fetchAdsValue())
            mlog("first loadAd", adMode, recover)
            if(adMode == 1){
                initDyAd()
            }
            else {
                initSyAd()
            }
        }
        else{
            if(!baseRepository.appTipInfo.openRecover){
                return
            }
            mlog("recover loadAd", mustAd)
            this.recoverFlag = true
            if(mustAd == 1){
                initDyAd()
            }
            else {
                initSyAd()
            }
        }
    }

    override fun onRestart() {
        mlog("restart", "111")
        super.onRestart()
        loadAds(true)
    }

    override fun onResume() {
        super.onResume()
        mlog("activity onResume")
    }

    private fun initDyAd(){
        if(initDy){
            mlog("prepare2 initDyAds")
            loadSplashAd()
            return
        }

        mlog("prepare initDyAds")
        //初始化聚合sdk
        val that = this
        TTAdSdk.init(baseRepository.context, buildConfig())
        TTAdSdk.start(object : TTAdSdk.Callback {
            override fun success() {
                //初始化成功
                //在初始化成功回调之后进行广告加载
                val adNativeLoader = TTAdSdk.getAdManager().createAdNative(that)
                dyAd = adNativeLoader
                loadSplashAd()
                initDy = true
                mlog("initDyAds")
            }

            override fun fail(code: Int, msg: String?) {
                //初始化失败
                //广告加载失败
                mlog("initDyAdFail", msg)
                if(that.recoverFlag){
                    return
                }
                loadAds(true, 1)
            }
        })
    }

    fun buildSplashAdslot(): AdSlot {
        val displayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val screenHeightPx = displayMetrics.heightPixels
        val density: Float = displayMetrics.density
        return AdSlot.Builder()
            .setCodeId(dy_ad_id) //广告位ID
            .setIsAutoPlay(true)
            .setImageAcceptedSize(screenWidthPx, screenHeightPx)
            .setExpressViewAcceptedSize(screenWidthPx / density, screenHeightPx / density)
            .build()
    }

    // 加载开屏广告
    fun loadSplashAd() {
        val that = this
        dyAd.loadSplashAd(buildSplashAdslot(), object : TTAdNative.CSJSplashAdListener {
            override fun onSplashLoadSuccess(p0: CSJSplashAd?) {
                mlog("loadDyAdSucess")

                mlog("loadAdsTime", System.currentTimeMillis() - initAppTime)
            }

            override fun onSplashLoadFail(error: CSJAdError?) {
                //广告加载失败
                mlog("loadDyAdFail", error?.msg)
                if(that.recoverFlag){
                    return
                }
                loadAds(true, 2)
            }

            override fun onSplashRenderSuccess(csjSplashAd: CSJSplashAd?) {
                //广告渲染成功，在此展示广告
                val pr = findViewById<FrameLayout>(R.id.ads1)
                pr.visibility = View.VISIBLE
                baseRepository.adVip = true
                showSplashAd(csjSplashAd, pr); //注 ：splashContainer为展示Banner广告的容器
                baseRepository.postEvent2("ad1Success")
                if(recoverFlag){
                    baseRepository.postEvent2("recoverAd1Success")
                }
                recoverFlag = false
            }

            override fun onSplashRenderFail(p0: CSJSplashAd?, p1: CSJAdError?) {
                //广告渲染失败
            }
        }, 3000)
    }

    //展示开屏广告
    fun showSplashAd(ad: CSJSplashAd?, container: ViewGroup) {
        ad?.let {
            it.setSplashAdListener(object : CSJSplashAd.SplashAdListener {
                override fun onSplashAdShow(csjSplashAd: CSJSplashAd?) {
                    //广告展示
                    mlog("showDyAd")
                    container.visibility = View.VISIBLE
                    //获取展示广告相关信息，需要再show回调之后进行获取
                    var manager = it.mediationManager;
                    if (manager != null && manager.showEcpm != null) {
                        val ecpm = manager.showEcpm.ecpm //展示广告的价格
                        val sdkName = manager.showEcpm.sdkName  //展示广告的adn名称
                        val slotId = manager.showEcpm.slotId //展示广告的代码位ID
                    }
                }

                override fun onSplashAdClick(csjSplashAd: CSJSplashAd?) {
                    //广告点击
                    mlog("clickDyAds")
                }

                override fun onSplashAdClose(csjSplashAd: CSJSplashAd?, p1: Int) {
                    container.visibility = View.GONE
                    container.removeAllViews()
                    mlog("closeDyAds")
                }

            })
            if (container != null) {
                it.showSplashView(container) //展示开屏广告
            }
        }
    }

    // 构造TTAdConfig
    private fun buildConfig(): TTAdConfig {
        return TTAdConfig.Builder()
            .appId(dy_app_id) //APP ID
            .appName("极简记工时") //APP Name
            .useMediation(true)  //开启聚合功能
            .debug(BuildConfig.IS_DEV)  //关闭debug开关
            .themeStatus(0)  //正常模式  0是正常模式；1是夜间模式；
            /**
             * 多进程增加注释说明：V>=5.1.6.0支持多进程，如需开启可在初始化时设置.supportMultiProcess(true) ，默认false；
             * 注意：开启多进程开关时需要将ADN的多进程也开启，否则广告展示异常，影响收益。
             * CSJ、gdt无需额外设置，KS、baidu、Sigmob、Mintegral需要在清单文件中配置各家ADN激励全屏xxxActivity属性android:multiprocess="true"
             */
            .supportMultiProcess(false)  //不支持
            .customController(getTTCustomController())  //设置隐私权
            .build()
    }
    //设置隐私合规
    private fun getTTCustomController(): TTCustomController? {
        return object : TTCustomController() {
            override fun isCanUseLocation(): Boolean {  //是否授权位置权限
                return true
            }

            override fun isCanUsePhoneState(): Boolean {  //是否授权手机信息权限
                return true
            }

            override fun isCanUseWifiState(): Boolean {  //是否授权wifi state权限
                return true
            }

            override fun isCanUseWriteExternal(): Boolean {  //是否授权写外部存储权限
                return true
            }

            override fun isCanUseAndroidId(): Boolean {  //是否授权Android Id权限
                return true
            }

            override fun getMediationPrivacyConfig(): MediationPrivacyConfig? {
                return object : MediationPrivacyConfig() {
                    override fun isLimitPersonalAds(): Boolean {  //是否限制个性化广告
                        return false
                    }

                    override fun isProgrammaticRecommend(): Boolean {  //是否开启程序化广告推荐
                        return true
                    }
                }
            }
        }
    }


    private fun initSyAd() {
        mlog("initSyAds", initSy)
        if(initSy){
            adSuyiSplashAd.loadOnly("94d184376f61cccaf2")
            return
        }
        val that = this
        // 初始化ADSuyi广告SDK
        ADSuyiSdk.getInstance().init(
            this,
            ADSuyiInitConfig.Builder()
                // 设置APPID
                .appId("3424220")
                // 是否开启Debug，开启会有详细的日志信息打印，如果用上ADSuyiToastUtil工具还会弹出toast提示。
                .debug(BuildConfig.IS_DEV)
                //【慎改】是否同意隐私政策，将禁用一切设备信息读起严重影响收益
                .agreePrivacyStrategy(true)
                // 是否可获取定位数据
                .isCanUseLocation(true)
                // 是否可获取设备信息
                .isCanUsePhoneState(true)
                // 是否可读取设备安装列表
                .isCanReadInstallList(true)
                // 是否可读取设备外部读写权限
                .isCanUseReadWriteExternal(true)
                // 是否可读取WIFI信息
                .isCanUseWifiState(true)
                // 是否允许使用传感器
                .isCanUseSensor(true)
                .build(),
            object : ADSuyiInitListener {
                override fun onSuccess() {
                    // 初始化成功
                    // 创建开屏广告实例，第一个参数可以是Activity或Fragment
                    adSuyiSplashAd = ADSuyiSplashAd(that)
                    adSuyiSplashAd.listener = object: ADSuyiSplashAdListener<ADSuyiSplashAdInfo> {
                        override fun onAdExpose(p0: ADSuyiSplashAdInfo?) {
                            mlog("onAdExpose")
                        }

                        override fun onAdClick(p0: ADSuyiSplashAdInfo?) {
                            mlog("onSyAdClick")
                            baseRepository.adVip = true
                        }

                        override fun onAdClose(p0: ADSuyiSplashAdInfo?) {
                            val pr = findViewById<FrameLayout>(R.id.ads2)
                            pr.visibility = View.GONE
                            pr.removeAllViews()
                            baseRepository.adVip = true
                        }

                        override fun onAdFailed(p0: ADSuyiError?) {
                            mlog("onSyAdFail", p0?.error)
                            if(that.recoverFlag){
                                return
                            }
                            loadAds(true, 1)
                            if (p0 != null) {
                                mlog("ad failed", p0.error)
                            }
                            else{
                                mlog("add failed")
                            }
                        }

                        override fun onADTick(p0: Long) {
                            mlog("ad tick")
                        }

                        override fun onReward(p0: ADSuyiSplashAdInfo?) {
                            mlog("ad reward")
                        }

                        override fun onAdSkip(p0: ADSuyiSplashAdInfo?) {
                            mlog("ad skip")
                        }

                        override fun onAdReceive(p0: ADSuyiSplashAdInfo?) {
                            mlog("syAdsReceive")
                            val pr = findViewById<FrameLayout>(R.id.ads2)
                            pr.visibility = View.VISIBLE
                            adSuyiSplashAd.showSplash(pr)
                            baseRepository.adVip = true

                            baseRepository.postEvent2("ad2Success")
                            if(recoverFlag){
                                baseRepository.postEvent2("recoverAd2Success")
                            }
                            recoverFlag = false
                        }
                    }
                    adSuyiSplashAd.loadOnly("94d184376f61cccaf2")
                    initSy = true
                }
                override fun onFailed(error: String) {
                    // 初始化失败
                    mlog("onSyAdLoadFail", error)
                    if(that.recoverFlag){
                        return
                    }
                    loadAds(true, 1)
                }
            }
        )
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
                MainScope().launch {
                    hasRegist = true
                    baseRepository.makeRegist()
                }
            })
        }

    }

}