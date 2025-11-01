package cn.jianyun.worktime.module.base.views.webdav


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.R
import cn.jianyun.worktime.model.DELETE
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.module.base.vm.WebDAVUserViewModel
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.LongDeleteButton
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderIcon
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyEncryptTool
import cn.jianyun.worktime.util.MyWebdavTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.ifv
import com.alibaba.fastjson2.JSON

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WebDAVUserEditView(navHostController: NavHostController, arguments: Bundle?) {


    val painter = painterResource(id = R.drawable.jgcloud)
    var viewModel = hiltViewModel<WebDAVUserViewModel>()

    val model = arguments?.getString("model")

    val editInfo = JSON.parseObject(model, WebDAVUser::class.java)

    viewModel.initModel(editInfo)

    PageView(navHostController = navHostController, rightTool = {
        HeaderIcon(icon = IconFont.info2) {
            viewModel.formType = FormType("doc")
        }
    },  title = ifv(editInfo.uuid == "", "添加", "编辑") + "云备份") {
        LeadingHintView("选择云备份平台")

        GroupView {
            Blank()
            //"GoogleDrive",  "OneDrive", "DropBox"
            FlowTagView(value=viewModel.editItem.platform, dialog = true, big=true, options = SelectUtil.initSingleValues("坚果云", "Koofr"), onClick = {
                viewModel.editItem = viewModel.editItem.copy(platform = it, url = MyWebdavTool.getWebDAVServerUrl(it))
            })
        }

        LeadingHintView("账号信息")

        GroupView {
            InputItemView(label = "用户名", value = viewModel.editItem.username, onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(username = it)
            })
            InputItemView(
                label = "密码",
                password = true,
                value = viewModel.editItem.resolvePassword(),
                onValueChange = {
                    viewModel.editItem = viewModel.editItem.copy(password = MyEncryptTool.encrypt(it))
                }
            )

            SwitchItemView(label = "主节点", value = viewModel.editItem.masterNode, onValueChange = {
                viewModel.editItem = viewModel.editItem.copy(masterNode = it)
            })
        }


//        if(viewModel.editItem.platform != "坚果云" && viewModel.editItem.platform != "Koofr") {
//            SmallTipText(text = "请先注册Koofr平台账号，再把${viewModel.editItem.platform}绑定到Koofr即可，上方只需要填写Koofr平台即可")
//        }

        SmallTipText(text = "开发者郑重承诺，默认情况下，你们的云备份账号密码仅会存储到本地，不会上传至开发者服务器")
        Blank()

        LongOkButton("保存") {
            viewModel.save(navHostController)
        }

        if(!viewModel.editItem.isAdd()){
            LongCancelButton("复制") {
                viewModel.copyOne(navHostController)
            }
        }

        LeadingHintView(label = "坚果云密码按如下步骤获取")

        Image(
            painter = painter,
            contentDescription = "Zoomable image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
        )
//
//        androidx.compose.foundation.Image(
//            painter = painterResource(R.drawable.jgcloud), contentDescription = "", modifier = Modifier
//                .radius(8.dp)
//                .scale(1.5f)
//        )


        if(!viewModel.editItem.isAdd()) {
            Blank()
            LongDeleteButton {
                viewModel.formType = FormType.delete()
            }
        }

        if(viewModel.formType.isForm(DELETE)) {
            DeleteDialog(okAction = {
                viewModel.doDelete(navHostController)
            }) {
                viewModel.resetForm()
            }
        }

        if(viewModel.formType.isForm("doc")){
            WebDAVUserDocView(viewModel)
        }

    }


}