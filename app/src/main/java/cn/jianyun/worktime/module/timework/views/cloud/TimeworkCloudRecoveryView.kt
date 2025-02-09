package cn.jianyun.worktime.module.timework.views.cloud

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.timework.vm.TimeworkCloudManageViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.LongOkButton


@Composable
fun TimeworkCloudRecoveryView(navHostController: NavHostController, viewModel: TimeworkCloudManageViewModel){


    BottomDialogView(title = "从备份中恢复数据",  onDismiss = {
        viewModel.resetForm()
    }) {
        Blank()
        LongOkButton("全量覆盖本地数据") {
            viewModel.doCover(navHostController)
        }
        Blank()
    }

}
