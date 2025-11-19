package cn.jianyun.worktime.main.setting.user

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.ui.component.nav.AppLogoView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.WarnText
import cn.jianyun.worktime.vm.AppSettingViewModel
import cn.jianyun.worktime.ui.component.form.InnerInputItemView
import cn.jianyun.worktime.ui.component.form.OkAndCancelButtonGroup
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.form.SelfDialog
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.SmallTipText

@Composable
fun LoginDialog(viewModel: AppSettingViewModel, type: String = "", backAction: (() -> Unit)? = null){

    SelfDialog(onDismiss = {
        viewModel.formType = FormType()
    }) {

        CenterRow(padding = 10.dp) {
            AppLogoView()
            Blank()
            Text("极简记工时")
        }

        CenterRow(padding = 10.dp, paddingBottom = 20.dp) {
            SegmentPickerView(value=viewModel.loginType.type, options = SelectUtil.LOGIN_TYPES, onChange = {
                viewModel.loginType = FormType(it)
            })
        }

        if(viewModel.purchaseFlag){
            WarnText(label = "请登录或注册账号后再购买，避免无法绑定会员身份", fontSize = 14.sp)
            Blank(10.dp)
        }

        if(type == "feedback") {
            WarnText(label = "请登录或注册账号后再反馈，避免无法查看回复", fontSize = 12.sp)
            Blank(10.dp)
        }

        Column {
            InnerInputItemView(label = "账号", placeholder = "手机号或者邮箱", value = viewModel.loginUser.username, onValueChange = {
                viewModel.loginUser = viewModel.loginUser.copy(username = it)
            })
            Blank()
            InnerInputItemView(label = "密码", password=true, value = viewModel.loginUser.password, onValueChange = {
                viewModel.loginUser = viewModel.loginUser.copy(password = it)
            })
            if(viewModel.loginType.isForm("regist")) {
                Blank()
                InnerInputItemView(label = "确认", placeholder = "请输入确认密码", password=true, value = viewModel.loginUser.confirmPassword, onValueChange = {
                    viewModel.loginUser = viewModel.loginUser.copy(confirmPassword = it)
                })
            }
        }

        if(viewModel.loginType.isForm("regist")){
            Blank()

            SmallTipText(text = "注意：这是极简记工时专属账号，若您需要对数据进行云备份，请额外配置您的云备份账号")

            Blank()
            Blank()
            OkAndCancelButtonGroup(okLabel = "确认注册", okAction = {
                viewModel.doRegist()
            }) {
                viewModel.formType = FormType()
                backAction?.let { it() }
            }
        }
        else{
            Blank()
            Blank()
            OkAndCancelButtonGroup(okLabel = "登录",  okAction = {
                viewModel.doLogin()
            }) {
                viewModel.formType = FormType()
                backAction?.let { it() }
            }
        }
        BackHandler {
            // your action
            viewModel.formType = FormType()
            backAction?.let { it() }
        }
    }
    BackHandler {
        // your action
        viewModel.formType = FormType()
        backAction?.let { it() }
    }
}