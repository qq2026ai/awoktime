package cn.jianyun.worktime.hilt.respo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import cn.jianyun.worktime.api.AppTipInfo
import cn.jianyun.worktime.api.BaseApi
import cn.jianyun.worktime.api.ConfigApi
import cn.jianyun.worktime.api.FestivalData
import cn.jianyun.worktime.api.TraceApi
import cn.jianyun.worktime.api.TraceInfo
import cn.jianyun.worktime.main.setting.user.User
import cn.jianyun.worktime.model.LocalBackupConfig
import cn.jianyun.worktime.module.base.dao.WebDAVUserDao
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.util.CacheUtil
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyPhoneTool
import cn.jianyun.worktime.util.dateStr
import cn.jianyun.worktime.util.datetimeStr
import cn.jianyun.worktime.util.getStringValue
import cn.jianyun.worktime.util.getToastMessageLength
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.makePKey
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.uuid
import cn.jianyun.worktime.util.withApi
import com.alibaba.fastjson2.JSON
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Singleton
class BaseRepository @Inject constructor(
    @Named("app") private val datastore: DataStore<Preferences>,
    val api: BaseApi,
    val webDAVUserDao: WebDAVUserDao,
    private val traceApi: TraceApi,
    private val baseApi: BaseApi,
    private val configApi: ConfigApi,
    @ApplicationContext val context: Context
) {

    //记录当前页
    var currentPage by mutableStateOf("")
    var toast: Toast? = null
    var sid = 0
    var page = ""
    var inited = false
    var registDay = 0
    var adVip = false
    var focusId by mutableStateOf("")
    var loading by mutableStateOf(false)
    var finishTime by mutableStateOf(0L)
    var brand = ""
    var loginUser by mutableStateOf(User())
    var notifyStatus by mutableStateOf(mapOf<String,Boolean>())
    var uid: String = ""
    var appTipInfo: AppTipInfo = AppTipInfo()
    var minVipDay = 15
    var curVersion: String = ""
    var showTip: Boolean by mutableStateOf(false)
    var readVersion: String = ""

    fun fetchAdsValue(): Int {
        return weightedRandom(this.appTipInfo.adsValue)
    }

    fun fetchRecoverAdsValue(): Int {
        return weightedRandom(this.appTipInfo.recoverAdsValue)
    }

    fun weightedRandom(ratio: String): Int {
        var u = ratio
        if (!ratio.contains(":")) {
            u = "1:1"
        }

        println("ads ratio: $ratio")

        // 1. 解析比值字符串
        val components = u.split(":")

        // 2. 确保有两个部分且都能转换成整数
        if (components.size != 2) {
            println("错误：比值格式不正确，应为 '权重1:权重2' 的正整数格式")
            return 1
        }

        val weight1 = components[0].toIntOrNull()
        val weight2 = components[1].toIntOrNull()

        if (weight1 == null || weight2 == null || weight1 <= 0 || weight2 <= 0) {
            println("错误：比值格式不正确，应为 '权重1:权重2' 的正整数格式")
            return 1
        }

        // 3. 计算总权重
        val totalWeight = weight1 + weight2

        // 4. 生成随机数并决定返回值
        val randomValue = (1..totalWeight).random()

        // 5. 根据权重返回对应的值
        return if (randomValue <= weight1) {
            1
        } else {
            2
        }
    }



    fun isLogin(): Boolean{
        return loginUser.uuid != "" && loginUser.username != ""
    }

    fun isNeedReload(page: String, oldSid: Int): Boolean {
        return oldSid != sid
    }

    fun hasRegist(): Boolean {
        return uid != ""
    }

    init{
        Log.d("qz init base repository", "base repository")
        runBlocking {
            uid = getUid()
            curVersion = getCurrentVersion()
            readVersion = getCache("readVersion", "")
            Log.d("qz init base repository finish", "base repository finish")
//            postPage("home")
        }
        val that = this

        GlobalScope.launch {
            delay(1000)
            mlog("makeFetchConfig")
            if(uid != ""){
                postPage("home")
                //拉一个数据
                var ds = withApi {
                    configApi.listConfigs(app="jgs")
                }
                mlog("sss", ds)
                if(ds.success){
                    var ks = ds.fetchResult()

                    var filterData = ks.filter{one -> one.isShown(that)}
                    if(filterData.isNotEmpty()){
                        that.appTipInfo = filterData[0]
                        that.minVipDay = ifv(that.appTipInfo.minVipDay < 10, 10, that.appTipInfo.minVipDay)
                        if(!that.booleanCache(that.appTipInfo.uid())) {
                            that.appTipInfo.isShow = true
                            that.showTip = true
                        }
                    }
                    else{
                        that.appTipInfo.isShow = false
                        that.showTip = false
                    }
                    var configData = ks.filter{one -> one.name == "app_config"}
                    if(!configData.isEmpty()){
                        that.appTipInfo.openScreenAds = configData[0].openScreenAds
                        that.appTipInfo.screenAdsGapMinute = configData[0].screenAdsGapMinute
                        that.appTipInfo.minVipDay = configData[0].minVipDay
                        that.appTipInfo.adsValue = configData[0].adsValue
                        that.appTipInfo.recoverAdsValue = configData[0].recoverAdsValue
                        that.appTipInfo.openRecover = configData[0].openRecover

                        //缓存这个数据
                        cacheJsonValue("appTipConfig", that.appTipInfo)

                    }
                }
            }
        }
    }

    fun isVip(): Boolean{
        return isAdVip() || adVip
    }

    fun isAdVip(): Boolean{
        val min = ifv(minVipDay < 10, 10, minVipDay)
        return loginUser.vipDate != "" && loginUser.vipDate >= Date().dateStr() || registDay < min
    }

    fun isRealVip(): Boolean{
        return loginUser.isVip()
    }

    fun openUrl(url: String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        // 启动系统浏览器
        context.startActivity(intent)
    }

    suspend fun loading(delay: Boolean = true){
        if(loading){
            return
        }
        loading = true
        if(delay){
            finishTime = System.currentTimeMillis() + 200
            delay(200)
        }
        else{
            finishTime = System.currentTimeMillis()
        }
    }

    fun makeLoading(scope: CoroutineScope? = null){
        if(loading){
            return
        }
        loading = true
        if(scope != null){
            scope.launch {
                finishTime = System.currentTimeMillis() + 800
                delay(800)
            }
        }
        else{
            finishTime = System.currentTimeMillis()
        }
    }


    suspend fun getRegistDay(): Int {
        val registDay = getCacheDay("registDay")
        return registDay
    }

    suspend fun makeLocalRegist(){
        cacheNow("registDay")
    }

    /**
     * APP初始化工作
     */
    suspend fun initApp(){
        if(inited){
            return
        }
        brand = CacheUtil.get(context, "brand")
        getUid()
        getDid()
        registDay = getRegistDay()
        getNotifyStatus()
        appTipInfo = getObjectCache("appTipConfig", AppTipInfo::class.java) ?: AppTipInfo()
        mlog("readCacheConfig", appTipInfo)
        inited = true
    }

    fun finish(){
        if(System.currentTimeMillis() > finishTime){
            loading = false
        }
        else{
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                // 异步操作
                delay(finishTime - System.currentTimeMillis())
                loading = false
                scope.cancel()
            }
        }
    }

    suspend fun getUid(): String{
        var uu = getObjectCache("uu", User::class.java)
        if(uu != null){
            loginUser = uu
        }
        else{
            loginUser = User()
        }
        return loginUser.uuid
    }

    suspend fun makeRegist() {
        var uu = getObjectCache("uu", User::class.java)
        if(uu != null && uu.uuid != ""){
            return
        }
        var newUser = uu!!
        newUser.registTime = System.currentTimeMillis()
        newUser.uuid = uuid()
        loginUser = newUser
        makeLocalRegist()
        cacheJsonValue("uu", newUser)
    }


    suspend fun getDid(): String {
        var did = getCache("did")
        if(did == ""){
            did = MyPhoneTool.getDid()
            cache("did", did)
        }
        return did
    }

    fun getCurrentVersion(): String{
        val ki = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        if(ki == null){
            return "1.0.0"
        }
        return ki!!
    }

    suspend fun fetchHoliday(year: Int): Map<String, FestivalData>{
        mlog("start fetch festival data")
        try{
            //如果是周一，则重新读取
            val lstTime = CacheUtil.getLong(context, "lastYear", 0);
            var holidays = getArrayCache("hyear${year}", FestivalData::class.java)
            if(holidays.isEmpty() || System.currentTimeMillis() - lstTime > 1000 * 60 * 60 * 24 * 5){
                val data = api.fetchHoliday(year)
                holidays = data.fetchResult()
                CacheUtil.setLong(context, "lastYear", System.currentTimeMillis())
            }
            var rst = mutableMapOf<String, FestivalData>()
            holidays.forEach{
                rst[it.date] = it
            }
            cacheJsonValue("hyear${year}", holidays)
//            log("read holiday data", rst)
            return rst
        }
        catch(e: Exception) {
            return mapOf()
        }
    }

    fun reload(){
        if(loading){
            finish()
        }
        sid += 1
    }

    fun copyData(data: String, showMsg: Boolean =false) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", data))
        if(showMsg){
            toast("复制成功")
        }
    }

    fun toast(msg: String){
        if(loading){
            finish()
        }
        if(toast != null){
            toast!!.cancel()
        }
        try{
            toast = Toast.makeText(context, msg, getToastMessageLength(msg));
            toast!!.show();
        }
        catch (ee: Exception){
            mlog("toast error", ee)
        }
    }

    suspend fun cache(key: String, value: String) {
        datastore.edit {
            it[makePKey(key)] = value
        }
    }

    suspend fun getCache(key: String, defaultValue: String = ""): String {
        return getStringValue(datastore, key, defaultValue)
    }

    suspend fun cacheInt(key: String, value: Int) {
        datastore.edit {
            it[makePKey(key)] = value.toString()
        }
    }

    suspend fun intCache(key: String, defaultValue: Int): Int {
        val value = getCache(key, defaultValue = defaultValue.toString())
        try {
            return value.toInt()
        }
        catch(e:Exception){
            return 0;
        }
    }

    suspend fun intCache(key: String): Int {
        return intCache(key, 0)
    }

    suspend fun cacheLong(key: String, value: Long) {
        datastore.edit {
            it[makePKey(key)] = value.toString()
        }
    }

    suspend fun cacheNow(key: String) {
        datastore.edit {
            it[makePKey(key)] = System.currentTimeMillis().toString()
        }
    }

    suspend fun longCache(key: String): Long {
        val value = getCache(key, defaultValue = "0")
        try {
            return value.toLong()
        }
        catch(e:Exception){
            return 0;
        }
    }

    suspend fun timeCache(key: String): String {
        val v = longCache(key)
        if(v == 0L){
            return ""
        }
        return Date(v).datetimeStr()
    }

    suspend fun getCacheHour(key: String): Int {
        val tt = longCache(key)
        val gap = System.currentTimeMillis() - tt
        return (gap / 1000 / 3600).toInt()
    }

    suspend fun getCacheDay(key: String): Int {
        val tt = longCache(key)
        if(tt == 0L){
            return 0
        }
        val gap = System.currentTimeMillis() - tt
        return (gap / 1000 / 3600 / 24).toInt()
    }

    suspend fun getCacheMinute(key: String): Int {
        val tt = longCache(key)
        val gap = System.currentTimeMillis() - tt
        return (gap / 1000 / 60).toInt()
    }

    suspend fun cacheBoolean(key: String, value: Boolean) {
        datastore.edit {
            it[makePKey(key)] = value.toString()
        }
    }

    suspend fun booleanCache(key: String): Boolean {
        val value = getCache(key, defaultValue = "false")
        try {
            return value.toBoolean()
        }
        catch(e:Exception){
            return false;
        }
    }

    suspend fun <T> cacheJsonValue(key: String, value: T) {
        cache(key, JSON.toJSONString(value))
    }

    suspend fun <T> getObjectCache(key: String, cls: Class<T>): T?  {
        val value = getCache(key, "{}")
        try{
            return JSON.parseObject(value, cls)
        }
        catch (e: Exception){
            return null
        }
    }

    suspend fun <T> getArrayCache(key: String, cls: Class<T>): List<T>  {
        val value = getCache(key, "{}")
        try{
            return JSON.parseArray(value, cls)
        }
        catch (e: Exception){
            return listOf()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun playTap(){
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK,1.0f)
    }

    suspend fun makeLogin(uu: User) {
        cacheJsonValue("uu", uu)
        loginUser = uu
    }

    suspend fun makeJgyUser(loginUser: WebDAVUser) {
        cacheJsonValue("jgy", loginUser)
    }

    suspend fun getWebDAVUser(): WebDAVUser {
        return webDAVUserDao.getMasterOne()
    }

    suspend fun listWebDAVUser(): List<WebDAVUser> {
        return webDAVUserDao.list()
    }

    suspend fun getNotifyStatus(){
        notifyStatus = (getObjectCache("notifyStatus", Map::class.java) ?: mapOf<String,Boolean>()) as Map<String, Boolean>
    }

    suspend fun makeNotifyStatus(map: Map<String, Boolean>){
        cacheJsonValue("notifyStatus", map)
        getNotifyStatus()
    }

    suspend fun getLocalPath(): String {
        return getLocalBackupConfig().path
    }
    suspend fun getLocalBackupConfig(): LocalBackupConfig {
        return getObjectCache("localBackup", LocalBackupConfig::class.java) ?: LocalBackupConfig()
    }

    suspend fun setLocalBackupConfig(config: LocalBackupConfig){
        cacheJsonValue("localBackup", config)
    }

    suspend fun getAppTheme(): String {
        return getCache("appMode", "auto")
    }

    suspend fun postEvent(event: String) {
        var traceInfo = TraceInfo(type = "event", code=event, uid=uid)
        traceInfo = enhanceTraceInfo(traceInfo)
        var dd = withApi {
            traceApi.post(traceInfo)
        }
    }

    fun postEvent2(event: String) {
        var traceInfo = TraceInfo(type = "event", code=event, uid=uid)
        traceInfo = enhanceTraceInfo(traceInfo)

        if(appTipInfo.needStat){
            GlobalScope.launch {
                var dd = withApi {
                    traceApi.post(traceInfo)
                }
            }
        }
    }

    suspend fun postPage(page: String){
        var traceInfo = TraceInfo(type = "page", code=page, uid=uid)
        traceInfo = enhanceTraceInfo(traceInfo)
        var dd =  withApi {
            traceApi.post(traceInfo)
        }
    }

    fun enhanceTraceInfo(traceInfo: TraceInfo): TraceInfo {

        val applicationUiMode = context.resources.configuration.uiMode;
        val systemMode = applicationUiMode and Configuration.UI_MODE_NIGHT_MASK
        val darkMode = systemMode == Configuration.UI_MODE_NIGHT_YES

        var newTraceInfo = traceInfo
        newTraceInfo.addField("dark", ifv(darkMode, "1", "0"))
        //打包前设置
//        newTraceInfo.addField("platform", "应用宝")
//        newTraceInfo.addField("platform", "xiaomi")
//        newTraceInfo.addField("platform", "meizu")
//        newTraceInfo.addField("platform", "oppo")
        newTraceInfo.addField("brand", Build.BRAND)
//        newTraceInfo.addField("platform", "vivo")
//        newTraceInfo.addField("platform", "oppo")
        newTraceInfo.addField("platform", "vivo")
//        newTraceInfo.addField("platform", "sumsung")
//        newTraceInfo.addField("platform", "honor")
        newTraceInfo.addField("appVersion", getCurrentVersion()) //打包前设置
        newTraceInfo.addField("registerDay", "" + loginUser.getRegistDay()) //打包前设置
        newTraceInfo.addField("vip", "0")
        newTraceInfo.addField("sysVersion", "" + Build.VERSION.RELEASE)

        return newTraceInfo
    }

    suspend fun getLatestVersion(): String{
        var version = withApi {
            traceApi.getAppVersion("ajjgs")
        }
        if(version.success){
            return version.result ?: "1.0.0"
        }
        return "1.0.0"
    }


    fun isNetworkAvailable(): Boolean {
        try{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            return network != null
        }
        catch(e: Exception){
            return true
        }

    }

    suspend fun tryUpgrade(): Boolean{
        //判断网络问题
        //判断上次缓存时间

        //判断用户登录问题

        if(!isNetworkAvailable() && !loginUser.isVip()){
            return true
        }

        val cacheHour = getCacheHour("lvt")
        if(cacheHour < 72){
            return false
        }
        try{
            if(loginUser.username != null && loginUser.username != ""){
                loginUser.deviceId = MyPhoneTool.getDid()
                val t1 = withApi {
                    baseApi.tryLogin(loginUser)
                }
                if(t1.success && t1.result == "error"){
                    toast("账号登录设备过多，试试退出其它端再登录试试")
                    val uu = loginUser
                    uu.nickname = ""
                    uu.username = ""
                    uu.vipDate = ""
                    uu.vipName = ""
                    uu.password = ""
                    uu.confirmPassword = ""
                    makeLogin(uu)
                }
                if(t1.success && t1.result == "ok"){
                    var dd =  withApi {
                        api.fetchUserInfo(loginUser)
                    }
                    if(dd.success){
                        val userInfo = dd.fetchResult() as User
                        if(userInfo.vipName != null && userInfo.vipName != ""){
                            loginUser.vipName = userInfo.vipName
                            loginUser.vipDate = userInfo.vipDate
                            makeLogin(loginUser)
                            reload()
                        }
                    }

                }
            }
        }
        catch(e: Exception){

        }
        val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val latestVersion = getLatestVersion()
        val max1 = currentVersion.substring(0, currentVersion.indexOf("."))
        val max2 = latestVersion.subSequence(0, latestVersion.indexOf("."))

        if(MyDataTool.toInteger(max2, 0) - 1 > MyDataTool.toInteger(max1, 0)) {
            return true
        }
        else{
            cacheNow("lvt")
            return false
        }
    }




}