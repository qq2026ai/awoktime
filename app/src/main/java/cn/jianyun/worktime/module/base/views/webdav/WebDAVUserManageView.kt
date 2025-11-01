package cn.jianyun.worktime.module.base.views.webdav

import androidx.compose.runtime.Composable


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.module.base.router.WebDAVRouter
import cn.jianyun.worktime.module.base.vm.WebDAVUserViewModel
import cn.jianyun.worktime.ui.component.nav.AppLogoView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyWebdavTool
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.nav.HeaderIcon
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.toPage
import com.alibaba.fastjson2.toJSONString

@Composable
fun WebDAVUserManageView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<WebDAVUserViewModel>()
    viewModel.tryReload()

    PageView(navHostController = navHostController, title = "云备份账号" , rightTool = {
        HeaderIcon(icon = IconFont.info2) {
            viewModel.formType = FormType("doc")
        }
    }) {
        viewModel.datalist.forEach{
            GroupView(verticalPadding = 15.dp, modifier= Modifier.clickable {
                toPage(navHostController,WebDAVRouter.UserEdit.route, bundleOf("model" to it.toJSONString()))
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
                    
                    TagView(tag = "查看", color= PrimaryColor, big=true) {
                        toPage(navHostController, Router.WebDAV.route, bundleOf("model" to it.toJSONString()))
                    }
                }
            }
        }
        LongCancelButton("添加") {
            toPage(navHostController, WebDAVRouter.UserEdit.route, bundleOf("model" to WebDAVUser().toJSONString()))
        }

        if(viewModel.formType.isForm("doc")){
            WebDAVUserDocView(viewModel)
        }
    }

}