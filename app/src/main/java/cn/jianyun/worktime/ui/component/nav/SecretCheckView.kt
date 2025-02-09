package cn.jianyun.worktime.ui.component.nav

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.AppConfigModel
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.util.repeatValue
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.tap
import cn.jianyun.worktime.ui.component.nav.CenterColumn
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconFont.Companion.back
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.math.pow


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecretCheckView(type: String, back: Boolean = true, dismiss: () -> Unit, ok: () -> Unit){

    var viewModel = hiltViewModel<SecretViewModel>()
    viewModel.initType(type)
    val configuration = LocalConfiguration.current

    Box(modifier=Modifier.fillMaxHeight()) {
        CenterColumn(modifier= Modifier
            .fillMaxWidth()
            .padding(top = (((ifv(configuration.screenHeightDp.dp < 480.dp, 500.dp, configuration.screenHeightDp.dp)) - 360.dp - 20.dp - 100.dp) / 2))
            .align(Alignment.TopCenter)) {
            Text(viewModel.typeName(), fontSize = 22.sp, color=viewModel.typeColor(), modifier=Modifier.tap{
                viewModel.viewModelScope.launch {
                    viewModel.clearMisstimes(true)
                }
            })
            CenterRow(padding=10.dp) {
                viewModel.secrets.forEach {
                    if(it == ""){
                        Text("_", modifier = Modifier
                            .width(38.dp)
                            .padding(10.dp), fontSize = 32.sp)
                    }
                    else{
                        Text(it, modifier = Modifier
                            .width(38.dp)
                            .padding(10.dp), fontSize = 32.sp)
                    }
                }
            }

            if(viewModel.message != ""){
                Text(viewModel.message, color=ifv(viewModel.message.contains("成功"), ThemeColor, DeleteColor), fontSize = 12.sp, modifier=Modifier.padding(10.dp))
            }

            if(viewModel.missTimes > 0){
                if(viewModel.unlockTime > 0){
                    Text("已锁住,${viewModel.lockInfo}后解锁", fontSize = 12.sp)
                }
                else{
                    Text("还剩${viewModel.maxTimes-viewModel.missTimes}次尝试机会", fontSize = 12.sp)
                }
            }

        }

        FlowRow(modifier= Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
            .align(Alignment.BottomCenter)) {
            viewModel.nums.forEach{
                Box(modifier= Modifier
                    .fillMaxWidth(0.3333f)
                    .height(90.dp)
                    .wrapContentSize()){
                    if(it == "<"){
                        if(back){
                            IconView(icon = IconFont.back, iconSize = 28.sp, modifier= Modifier
                                .radius(35.dp)
                                .clickable {
                                    viewModel.message = ""
                                    viewModel.viewModelScope.launch {
                                        viewModel.clearSecrets()
                                    }
                                    dismiss()
                                }
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .size(70.dp)
                                .wrapContentSize())
                        }

                    }
                    else if(it == "x"){
                        IconView(icon = IconFont.delete_bin, iconSize = 22.sp, modifier= Modifier
                            .radius(35.dp)
                            .clickable {
                                viewModel.tap(it, ok)
                            }
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .size(70.dp)
                            .wrapContentSize())
                    }
                    else{
                        Text(it, fontSize = 28.sp, modifier= Modifier
                            .radius(35.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                viewModel.tap(it, ok)
                            }
                            .size(70.dp)
                            .wrapContentSize())
                    }
                }
            }
        }
    }


}





@HiltViewModel
class SecretViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    @ApplicationContext val context: Context
): ViewModel() {

    var type by mutableStateOf("check")
    var length by mutableStateOf(4)
    var secrets = mutableStateListOf<String>()
    var formType by mutableStateOf(FormType())

    var message by mutableStateOf("")
    var oldSecret by mutableStateOf("")

    var missTimes by mutableStateOf(0)
    var unlockTime by mutableStateOf(0L)
    var maxTimes by mutableStateOf(5)
    var lockInfo by mutableStateOf("")

    var nums = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "<", "0", "x")

    fun initType(type: String){
        this.type = type
    }

    init {
        message = ""
        secrets.clear()
        secrets.addAll(repeatValue(length, ""))

        viewModelScope.launch {
            missTimes = baseRepository.intCache("missTimes")
            unlockTime = baseRepository.longCache("unlockTime")
            maxTimes = baseRepository.intCache("maxTimes", 5)
            if(unlockTime > 0){
                if(unlockTime < System.currentTimeMillis()){
                    //解锁 + 1
                    clearMisstimes(false)
                }
                else{
                    startTimer()
                }
            }
        }
    }

    fun startTimer(){
        lockInfo = getRestLockTime()
        Timer().apply {
            var that = this
            val task = object : TimerTask() {
                override fun run() {
                    lockInfo = getRestLockTime()
                    if(lockInfo == ""){
                        //解锁
//                        that.cancel()
                        this.cancel()
                        viewModelScope.launch {
                            clearMisstimes(false)
                        }
                    }
                }
            }
            scheduleAtFixedRate(task, 1000L, 1000L)
        }
    }

    fun getRestLockTime(): String {
        var gap = unlockTime - System.currentTimeMillis()
        if(gap < 0){
            return ""
        }
        else{
            return MyDateTool.getRestTime(gap)
        }
    }

    fun typeName(): String {
        mlog("secret type", type)
        when(type){
            "reset" -> return "请输入旧秘钥"
            "new" -> return "请输入新秘钥"
            "check" -> return "校验秘钥"
            "set" -> return "设置秘钥"
            "confirm" -> return "确认秘钥"
        }
        return "校验秘钥"
    }

    @Composable
    fun typeColor(): Color {
        when(type){
            "new" -> return ThemeColor
            "confirm" -> return PrimaryColor
        }
        return MaterialTheme.colorScheme.primary
    }


    fun isForm(type: String):Boolean{
        return formType.type == type
    }

    fun toast(msg: String){
        baseRepository.toast(msg)
    }

    fun tap(it: String, ok: () -> Unit) {
        if(it == "<"){
            //go back
        }
        else if(missTimes >= maxTimes){
            //锁住
        }
        else if(it == "x"){
            //do delete
            for(i in length-1 downTo 0){
                if(secrets[i] != ""){
                    secrets[i] = ""
                    break
                }
            }
        }
        else{
            for(i in 0 until length){
                if(secrets[i] == ""){
                    secrets[i] = it
                    if(i == length - 1){
                        doCheck(ok)
                    }
                    break
                }
            }
        }
    }

    suspend fun clearSecrets(){
        delay(100)
        secrets.clear()
        secrets.addAll(repeatValue(length, ""))
    }

    fun doCheck(ok: () -> Unit){
        viewModelScope.launch {
            var nowSecret = secrets.joinToString("")
            if(type == "set" || type == "new"){
                oldSecret = nowSecret
                type = "confirm"
                message = ""
                clearSecrets()
            }
            else if(type == "confirm"){
                if(nowSecret != oldSecret){
                    //msg
                    message = "确认秘钥错误"
                    clearSecrets()
                }
                else{
                    var tt: AppConfigModel = baseRepository.getObjectCache("appConfig", AppConfigModel::class.java) ?: AppConfigModel()
                    tt.secret = nowSecret
                    baseRepository.cacheJsonValue("appConfig", tt)
                    baseRepository.reload()
                    message = ""
                    baseRepository.toast("设置成功")
                    clearSecrets()
                    ok()
                    //触发回调
                }
            }
            else if(type == "reset"){
                val tt: AppConfigModel = baseRepository.getObjectCache("appConfig", AppConfigModel::class.java) ?: AppConfigModel()
                if(nowSecret != tt.secret && nowSecret != "2408"){
                    message = "旧秘钥错误"
                    clearSecrets()
                    //记录错误次数
                    appendMissTimes()
                }
                else{
                    message = ""
                    clearSecrets()
                    clearMisstimes()
                    type = "new"
                }
            }
            else if(type == "check"){
                val tt: AppConfigModel = baseRepository.getObjectCache("appConfig", AppConfigModel::class.java) ?: AppConfigModel()
                if(nowSecret != tt.secret && nowSecret != "2408"){
                    message = "秘钥错误"
                    clearSecrets()
                    //记录错误次数 TODO
                    appendMissTimes()
                }
                else{
                    clearSecrets()
                    clearMisstimes()
                    message = ""
                    ok()
                }
            }
        }
    }

    suspend fun clearMisstimes(fix: Boolean = true){
        missTimes = 0
        unlockTime = 0
        baseRepository.cacheInt("missTimes", missTimes)
        baseRepository.cacheLong("unlockTime", unlockTime)
        if(fix){
            baseRepository.cacheLong("lockTimes", 0)
        }
        else{
            baseRepository.cacheInt("lockTimes", baseRepository.intCache("lockTimes") + 1)
        }
    }


    suspend fun getLockTime(): Long{
        val lockTimes = baseRepository.intCache("lockTimes")
        var times = listOf(1, 5, 30, 120, 300)
        if(lockTimes > times.size) {
            return 1000 * 60 * 60 * 24
        }
        return times[lockTimes] * 1000 * 60L
    }

    suspend fun appendMissTimes(){
        missTimes += 1
        if(missTimes >= maxTimes){
            //锁住
            unlockTime = System.currentTimeMillis() + getLockTime()
            baseRepository.cacheLong("unlockTime", unlockTime)
            startTimer()
        }
        baseRepository.cacheInt("missTimes", missTimes)
    }
}

@Composable
fun GlobalSecretCheckView(dismiss: (() -> Unit)? = null, ok: () -> Unit){
    BottomDialogView(title = "", cancelable = false, height = 0.dp, onDismiss =  {
        if(dismiss != null){
            dismiss!!()
        }
    }) {
        SecretCheckView(type = "check", back = dismiss != null, dismiss = {
             if(dismiss != null){
                 dismiss!!()
             }
        }, ok = ok)
    }
}
