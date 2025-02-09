package cn.jianyun.worktime.main.setting.cloud.jgy



import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyWebdavTool
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.ui.component.form.InnerInputItemView
import cn.jianyun.worktime.ui.component.form.OkAndCancelButtonGroup
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class JgyBindViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : ViewModel() {

    var loginUser by mutableStateOf(WebDAVUser())

    fun doBind(onDismiss: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            val msg = loginUser.isValid()
            if(!isOk(msg)){
                baseRepository.toast(msg)
                return@launch
            }
            baseRepository.loading()
            //webdav 检查
            loginUser.url = "https://dav.jianguoyun.com/dav/"
//            loginUser.url = "https://app.koofr.net/dav/"


            val ok = MyWebdavTool.check(loginUser)
            if(ok){
                baseRepository.makeJgyUser(loginUser.copy(bind = true, platform = "坚果云"))
                baseRepository.reload()
                baseRepository.toast("绑定成功")
                onDismiss()
            }
            else{
                baseRepository.toast("账号或密码错误")
            }
            baseRepository.finish()

//            val sardine: Sardine = OkHttpSardine()
//            sardine.setCredentials(loginUser.username, loginUser.password);
//            try{
//                val res = sardine.list(loginUser.url)
//                mlog("listData", res)
//                baseRepository.makeJgyUser(loginUser.copy(bind = true))
//                baseRepository.reload()
//                baseRepository.toast("绑定成功")
//                onDismiss()
//            }
//            catch (ee: Exception){
//                mlog("error2", ee)
//                baseRepository.toast("账号或密码错误")
//            }
//            baseRepository.finish()
        }
    }
}

@Composable
fun JgyBindView(onDismiss: () -> Unit) {
    val viewModel = hiltViewModel<JgyBindViewModel>()

    Column {
        HeaderView("绑定坚果云账号")
        Blank()
        Blank()
        InnerInputItemView(label = "账号", placeholder = "请输入坚果云账号", value = viewModel.loginUser.username, onValueChange = {
            viewModel.loginUser = viewModel.loginUser.copy(username = it)
        })
        Blank()
        InnerInputItemView(label = "密码",  placeholder = "请输入坚果云应用密码", password=true, value = viewModel.loginUser.password, onValueChange = {
            viewModel.loginUser = viewModel.loginUser.copy(password = it)
        })

        Blank()
        SmallLinkText(text = "查看坚果云webdav账号设置教程?") {

        }

        Blank()
        Blank()
        OkAndCancelButtonGroup(okLabel = "登录",  okAction = {
            viewModel.doBind{
                onDismiss()
            }
        }) {
            onDismiss()
        }


    }

}