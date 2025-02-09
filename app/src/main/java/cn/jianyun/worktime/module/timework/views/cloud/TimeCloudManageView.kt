package cn.jianyun.worktime.module.timework.views.cloud

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.base.views.cloud.CommonCloudView
import cn.jianyun.worktime.module.timework.vm.TimeworkCloudManageViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.LongOkButton


@Composable
fun TimeworkCloudManageView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkCloudManageViewModel>()
    viewModel.tryReload()

    CommonCloudView(navHostController = navHostController, baseCloudViewModel = viewModel) {
        viewModel.datalist.forEach{
            GroupView(verticalPadding = 10.dp, modifier= Modifier.clickable {
                viewModel.justShowBackupDialog(it)
            }) {
                Text("备份日期: ${it.dateInfo()}")
                Text("工时数据: ${it.count}条")
                Text("数据容量: ${it.sizeInfo()}")
                Text("备份来源: ${it.source}")
            }
        }
        if(viewModel.formType.isForm("showBackupData")){
            TimeworkCloudRecoveryView(navHostController, viewModel = viewModel)
        }
    }



}