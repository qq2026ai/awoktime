package cn.jianyun.worktime.module.base.views.cloud

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.vm.BaseCloudViewModel
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.OkAndCancelButtonGroup
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.EmptyIconDataView
import cn.jianyun.worktime.ui.component.nav.HeaderIcon
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.PageWithFooterView
import cn.jianyun.worktime.util.toVipPage
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker

@Composable
fun CommonCloudView(navHostController: NavHostController, baseCloudViewModel: BaseCloudViewModel, content:@Composable () -> Unit){

    PageWithFooterView(navHostController = navHostController, header = {
        SegmentPickerView(value=baseCloudViewModel.backType, options = SelectUtil.BACK_TYPES, onChange = {
            baseCloudViewModel.changeBackType(it)
        })
    }, rightTool = {
        HeaderIcon(icon = IconFont.cloud) {
            navHostController.navigate(Router.WebDAVManage.route)
        }
    }, footer = {
        if(baseCloudViewModel.isValid()){
            if(baseCloudViewModel.hasData()){
                OkAndCancelButtonGroup(okLabel = "全量备份", cancelLabel = "清空备份", okAction = {
                    if(baseCloudViewModel.backType == "cloud"){
                        if(!baseCloudViewModel.getRepository().isVip()){
                            toVipPage(navHostController)
                            return@OkAndCancelButtonGroup
                        }
                    }
                    baseCloudViewModel.doBackup()
                }) {
                    baseCloudViewModel.formType = FormType.clear()
                }
            }
            else{
                LongOkButton("全量备份") {
                    if(baseCloudViewModel.backType == "cloud"){
                        if(!baseCloudViewModel.getRepository().isVip()){
                            toVipPage(navHostController)
                            return@LongOkButton
                        }
                    }
                    baseCloudViewModel.doBackup()
                }
            }
        }
    }) {


        if(baseCloudViewModel.backType == "local"){
            if(baseCloudViewModel.localPath == ""){
                //没有数据
                EmptyIconDataView(icon=IconFont.cloud, label = "点击设置本地备份目录") {
                    baseCloudViewModel.formType = FormType("chooseDir")
                }

                DirectoryPicker(show = baseCloudViewModel.formType.isForm("chooseDir")) { path ->
                    if (path != null) {
                        baseCloudViewModel.saveLocalData(path)
                    }
                    baseCloudViewModel.formType = FormType()
                }
            }
            else{
                if(baseCloudViewModel.datalist.isEmpty()){
                    EmptyIconDataView("暂无本地备份数据")
                }
                else{
                    content()
                }
            }
        }
        else{
            if(!baseCloudViewModel.webDavUser.bind){
                //没有数据
                EmptyIconDataView(icon=IconFont.cloud, label = "点击绑定云备份账号") {
                    navHostController.navigate(Router.WebDAVManage.route)
                }
            }
            else{
                if(baseCloudViewModel.cloudDatalist.isEmpty()){
                    EmptyIconDataView("暂无云备份数据", icon=IconFont.cloud3)
                }
                else{
                    content()
                }
            }
        }

        if(baseCloudViewModel.formType.isClear()) {
            DeleteDialog(title = baseCloudViewModel.formType.message, okAction = {
                baseCloudViewModel.cleanBackup()
            }) {
                baseCloudViewModel.resetForm()
            }
        }

    }

}