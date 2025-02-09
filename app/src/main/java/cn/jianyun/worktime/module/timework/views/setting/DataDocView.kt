package cn.jianyun.worktime.module.timework.views.setting



import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.ui.component.form.BottomDialogView

@Composable
fun DataDocView(viewModel: TimeworkMasterViewModel){

    BottomDialogView(title="工时数据说明", height = 500.dp, onDismiss = {
        viewModel.formType = FormType()
    }) {

        Text("用户工时数据默认仅保存在手机本地")

    }

}
