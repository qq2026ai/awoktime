package cn.jianyun.worktime.vm

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.api.ApiResult
import cn.jianyun.worktime.api.BaseApi
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.main.setting.user.User
import cn.jianyun.worktime.model.AppConfigModel
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.withApi
import cn.jianyun.worktime.module.timework.dao.TimeworkDataDao
import cn.jianyun.worktime.util.CacheUtil
import cn.jianyun.worktime.util.parseDateTime
import com.alibaba.fastjson2.JSON
import com.alipay.sdk.app.PayTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val baseRepository: BaseRepository
): ViewModel() {

    var formType by mutableStateOf(FormType())

    fun isForm(type: String):Boolean{
        return formType.type == type
    }

    fun toast(msg: String){
        baseRepository.toast(msg)
    }
}


@HiltViewModel
class AppSettingViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val timeworkDataDao: TimeworkDataDao,
    val baseApi: BaseApi
): ViewModel() {

    var appConfig by mutableStateOf(AppConfigModel())
    var formType by mutableStateOf(FormType())
    var loginType by mutableStateOf(FormType("login"))
    var loginUser by mutableStateOf(User())
    var initApp by mutableStateOf(false)
    var webDAVUser by mutableStateOf(WebDAVUser())
    var sid by mutableStateOf(0)

    var darkTheme by mutableStateOf(false)

    var purchaseFlag by mutableStateOf(false)
    var brand by mutableStateOf("")
    var initeWebDav by mutableStateOf(false)

    var tempWebDavUsers by mutableStateOf(listOf<WebDAVUser>())

    var notifyStatus2 = mutableStateMapOf<String,Boolean>()

    var currentMode by mutableStateOf("year")

    var inited by mutableStateOf(false)

    init {
        reload()
    }

    fun reset(){
        formType = FormType()
    }

    fun isVip(): Boolean{
        return loginUser.isVip()
    }

    fun isLogin(): Boolean {
        return loginUser.isLogin()
    }

    fun isOpenNotify():Boolean{
        return notifyStatus2["all"] ?: false
    }

    fun initWebDavData(){
        if(initeWebDav){
            return
        }
        initeWebDav = true
        viewModelScope.launch {
            tempWebDavUsers = baseRepository.listWebDAVUser()
        }
    }

    fun checkLogin(){
        if(!loginUser.isLogin()){
            formType = FormType("login")
        }
        else{
            formType = FormType("")
        }
    }

    fun reload(){
        viewModelScope.launch {
            sid = baseRepository.sid
            val registDay = baseRepository.getRegistDay()
            if(registDay == 0){
                val minDate = timeworkDataDao.findMinDate()
                mlog("minDate", minDate)
                if(minDate != null && minDate.length > 10){
                    baseRepository.cacheLong("registDay", minDate.parseDateTime().time)
                    baseRepository.registDay = baseRepository.getRegistDay()
                }
            }
            else{
                baseRepository.registDay = registDay
            }

            baseRepository.initApp()
            appConfig = baseRepository.getObjectCache("appConfig", AppConfigModel::class.java) ?: AppConfigModel()
            if(appConfig.needSecret && appConfig.secret != "" && !inited){
                formType = FormType("check")
                inited = true
            }
            loginUser = baseRepository.loginUser
            brand = baseRepository.brand
            notifyStatus2.putAll(baseRepository.notifyStatus)
            webDAVUser = baseRepository.getWebDAVUser()
            delay(300 + (Math.random() * 100).toLong())
            initApp = true
        }
    }

    fun sleep(n:Long= 5, cb: () -> Unit){
        viewModelScope.launch {
            delay(n * 100)
            cb()
        }
    }

    fun tryReload(){
        if(sid != baseRepository.sid){
            reload()
        }
    }

    fun refresh(){
        viewModelScope.launch {
            baseRepository.loading()
            var dd =  withApi {
                baseRepository.api.fetchUserInfo(loginUser)
            }
            if(dd.success){
                val userInfo = dd.fetchResult() as User
                val config = userInfo.cloudConfig
                if(config != null && config != ""){
                    val webDavUsers = JSON.parseArray(config, WebDAVUser::class.java)
                    //写入本地,直接覆盖
                    webDavUsers.forEach{
                        baseRepository.webDAVUserDao.insert(it)
                    }
                    initeWebDav = false
                    initWebDavData()
                }
                if(userInfo.vipName != ""){
                    loginUser.vipName = userInfo.vipName
                    loginUser.vipDate = userInfo.vipDate
                    baseRepository.makeLogin(loginUser)
                    baseRepository.reload()
                    reload()
                }
            }
        }
    }

    fun isForm(type: String):Boolean{
        return formType.type == type
    }

    fun toast(msg: String){
        baseRepository.toast(msg)
    }

    fun makeChanged(){
        viewModelScope.launch {
            baseRepository.cacheJsonValue("appConfig", appConfig)
            appConfig = baseRepository.getObjectCache("appConfig", AppConfigModel::class.java) ?: AppConfigModel()
            baseRepository.reload()
        }
    }

    fun isNeedSecret(destination: String): Boolean {
        return appConfig.needSecret && appConfig.needSecretApps.split("^").toSet().contains(destination)
    }

    fun doLogin() {
        val msg = loginUser.isLoginValid()
        if(!isOk(msg)){
            return baseRepository.toast(msg)
        }
        //执行登录
        viewModelScope.launch {
            baseRepository.loading()
            loginUser.deviceId = baseRepository.getDid()
            loginUser.application = "workTime"
            loginUser.platform = "android"
            val rst = baseRepository.api.login(loginUser)
            baseRepository.finish()
            if(rst.success){
                val ttt = rst.fetchResult()
                ttt.registTime = loginUser.registTime
                baseRepository.makeLogin(ttt)
                baseRepository.toast("登录成功")
                loginUser = ttt
                formType = FormType()
            }
            else{
                baseRepository.toast("账号或密码错误")
            }
        }
    }

    fun doRegist() {
        val msg = loginUser.isRegistValid()
        if(!isOk(msg)){
            return baseRepository.toast(msg)
        }
        viewModelScope.launch {
            baseRepository.loading()
            loginUser.deviceId = baseRepository.getDid()
            loginUser.application = "workTime"
            loginUser.platform = "android"
            loginUser.uuid = baseRepository.getUid()

            val rst = withApi {
                baseRepository.api.regist(loginUser)
            }
            baseRepository.finish()
            if(rst.success){
                formType = FormType()
                print("xx: "+ JSON.toJSONString(rst.fetchResult()))
                val tt2 = rst.fetchResult()
                tt2.registTime = loginUser.registTime
                loginUser = tt2
                baseRepository.makeLogin(tt2)
            }
            else{
                baseRepository.toast(rst.message)
            }
        }

    }

    fun doExit(navHostController: NavHostController) {
        viewModelScope.launch {
            baseRepository.loading()

            val rst = withApi {
                baseRepository.api.exit(loginUser)
            }
            formType = FormType()
            //不论是否成功，都退出

            val uu = baseRepository.loginUser
            uu.nickname = ""
            uu.username = ""
            uu.vipDate = ""
            uu.vipName = ""
            uu.password = ""
            uu.confirmPassword = ""

            baseRepository.makeLogin(uu)
            baseRepository.finish()
            baseRepository.reload()
            goBack(navHostController)
        }
    }

    fun doUpdateInfo() {
        viewModelScope.launch {
            baseRepository.loading()
            val rst = baseRepository.api.updateInfo(loginUser)
            if(rst.success){
                baseRepository.toast("设置成功")
                baseRepository.makeLogin(loginUser)
                baseRepository.reload()
                formType = FormType()
            }
            else{
                baseRepository.toast("暂时失败")
                baseRepository.finish()
            }
        }
    }

    fun unbindJgy() {
        viewModelScope.launch {
            baseRepository.makeJgyUser(WebDAVUser())
            reset()
            baseRepository.reload()
        }
    }

    fun changeNotifyStatus(value: String, open: Boolean) {
        viewModelScope.launch {
            notifyStatus2[value] = open
            //保存到缓存里
            baseRepository.makeNotifyStatus(notifyStatus2)
        }
    }

    fun changeBrand(it: String) {
        viewModelScope.launch {
            brand = it
            baseRepository.brand = it
            CacheUtil.set(baseRepository.context, "brand", it)
        }
    }

    fun refreshInfo() {

    }

    fun doPurchase(activity: Activity, cb: () -> Unit) {

        if(!loginUser.isLogin()){
            purchaseFlag = true
            formType = FormType("login")
            return
        }

        viewModelScope.launch {
            baseRepository.loading()
            val info: ApiResult<String> = withApi {
                baseApi.sdkRequest("workTime","android", loginUser.uuid, currentMode, false)
            }
            if(info.success){
                val alipay = PayTask(activity)
                val orderResult = info.fetchResult()
                val result = withContext(Dispatchers.IO) {
                    alipay.payV2(orderResult, true)
                }

                if(result.get("resultStatus") != null && result.get("resultStatus") == "6001") {

                }
                else{
                    val aliResult = result.get("result")
                    if(aliResult != ""){
                        val realResult = JSON.parseObject(aliResult, AliPayResult::class.java)
                        if(realResult.isSuccess()) {
                            loginUser.makeVip(currentMode)
                            baseRepository.makeLogin(loginUser)
                            toast("购买成功，感谢您的支持")
                            cb()
                        }
                    }
                    else{

                    }
                }
                mlog("payResult", result)


//                val checkResult = withApi {
//                    delay(1000)
//                    shareApi.checkVip("plan", "android", loginUser.uuid, currentMode)
//                }
//                mlog("checkResult", checkResult)
            }
            else{
                toast(info.message)
            }
            baseRepository.finish()
        }
    }
}


data class AliPayResult(
    var alipay_trade_app_pay_response: AliPayResultInfo = AliPayResultInfo()
){
    fun isSuccess(): Boolean {
        return alipay_trade_app_pay_response.msg == "Success" && alipay_trade_app_pay_response.code == "10000"
    }
}

data class AliPayResultInfo(
    var code: String = "",
    var msg: String = "",
    var out_trade_no: String = "",
)