package cn.jianyun.worktime.main.setting.user



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.R
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.main.setting.cloud.jgy.JgyBindView
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.router.WebDAVRouter
import cn.jianyun.worktime.ui.component.nav.AppLogoView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyWebdavTool
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.vm.AppSettingViewModel
import cn.jianyun.worktime.ui.component.form.ConfirmDialog
import cn.jianyun.worktime.ui.component.form.FullDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SelfDialog
import cn.jianyun.worktime.ui.component.form.SettingGroupView
import cn.jianyun.worktime.ui.component.nav.HeaderIcon
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.ui.theme.VipColor
import com.alibaba.fastjson2.toJSONString

@Composable
fun UserDetailView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<AppSettingViewModel>()
    viewModel.tryReload()
    viewModel.initWebDavData()

    var readonly = !viewModel.formType.isForm("edit")

    PageView(navHostController = navHostController, title = "我的信息", rightTool = {
        HeaderIcon(icon = IconFont.refresh) {
            viewModel.refresh()
        }
    }) {
        SettingGroupView {
            TwoColumnView {
                VerticalRow(vpadding = 10.dp) {
                    AppLogoView(size=40.dp)
                    Blank()
                    Column {
                        Text(viewModel.loginUser.username, fontSize = 14.sp, lineHeight = 13.sp)
                        Blank(2.dp)
                        Row{
                            TagView(tag = ifv(viewModel.isVip(), viewModel.loginUser.vipName, "普通用户"), color=ifv(viewModel.isVip(), VipColor, Color.Gray), hollow = true)
                            if(viewModel.isVip()){
                                Blank(2.dp)
                                TagView(tag = "有效期：" + viewModel.loginUser.vipDate, color=VipColor, hollow = true)
                            }
                        }
                    }
                }

                TagView(tag = "退出登录", color= DeleteColor, big=true, hollow = true) {
                    viewModel.formType = FormType("exit")
                }
            }
        }

        LeadingHintView(ifv(readonly, "其他信息", "编辑信息")){
            if(readonly){
                SmallLinkText(text = "编辑") {
                    viewModel.formType = FormType("edit")
                }
            }
            else{
                SmallLinkText("取消"){
                    viewModel.formType = FormType("")
                }
            }
        }
        SettingGroupView {
            InputItemView(label = "昵称", readonly = readonly, value = viewModel.loginUser.nickname,  onValueChange = {
                viewModel.loginUser = viewModel.loginUser.copy(nickname = it)
            })
            InputItemView(label = "微信", readonly = readonly,value = viewModel.loginUser.wechat,  onValueChange = {
                viewModel.loginUser = viewModel.loginUser.copy(wechat = it)
            })
            InputItemView(label = "QQ", readonly = readonly,value = viewModel.loginUser.qq,  onValueChange = {
                viewModel.loginUser = viewModel.loginUser.copy(qq = it)
            })
            InputItemView(label = "城市", readonly = readonly,value = viewModel.loginUser.city,  onValueChange = {
                viewModel.loginUser = viewModel.loginUser.copy(city = it)
            })
        }

        if(viewModel.formType.isForm("edit")){
            LongOkButton("保存") {
                viewModel.doUpdateInfo()
            }
        }

        SmallTipText(text = "登录账号仅用于记录用户付费状态，如果需要对数据进行云备份，请自行绑定第三方云备份账号") {
            
        }
        Blank()
        Blank()

        LeadingHintView ("我的云备份账号")
        Blank()

        viewModel.tempWebDavUsers.forEach{
            GroupView(verticalPadding = 15.dp, modifier= Modifier.clickable {
                navHostController.navigateTo(Router.WebDAV.route, bundleOf("model" to it.toJSONString()))
            }) {
                TwoColumnView {
                    Column {
                        VerticalRow {
                            AppLogoView(icon = MyWebdavTool.getWebDAVIcon(it.platform), size = 16.dp)
                            Blank(2.dp)
                            Text(it.platform)
                            Blank()
                            TagView(tag = ifv(it.bind, "已连接", "已断开"), color=ifv(it.bind, ThemeColor, Color.Gray))
                            if(it.masterNode){
                                Blank()
                                TagView(tag = "主节点", hollow = true)
                            }
                            if(it.cloud){
                                Blank()
                                IconView(icon = IconFont.cloud2, iconSize = 12.sp, color=Color.Gray)
                            }
                        }
                        Text(it.username, fontSize = 14.sp)
                    }
                }
            }
        }

        LongCancelButton("管理我的云备份账号") {
            navHostController.navigate(Router.WebDAVManage.route)
        }

        if(viewModel.formType.isForm("exit")){
            ConfirmDialog(title = "退出登录会导致当前设备的会员身份丢失喔，请问还要继续退出吗？", okAction = {
                viewModel.doExit(navHostController)
            }){
                viewModel.reset()
            }
        }

    }

}