package cn.jianyun.worktime.module.timework.views.home


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.Cancel2Button
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.OkButton
import cn.jianyun.worktime.ui.component.form.SegmentItemView
import cn.jianyun.worktime.ui.component.nav.DeleteText


@Composable
fun MakeRestView(viewModel: TimeworkMasterViewModel, navHostController: NavHostController){

    val editWorkItem = viewModel.editWorkItem

    BottomDialogView(title = "${viewModel.getCurrentDateStr()}工时打卡", onDismiss = {
        viewModel.formType = FormType()
    }) {

        SegmentItemView(label = "打卡类型", width=60.dp, value = editWorkItem.mode, options= SelectUtil.LEAVE_TYPES, onValueChange = {
            viewModel.editWorkItem = editWorkItem.copy(mode = it)
        })

        InputItemView(label = "备注", multiLine = true, value = editWorkItem.remark, onValueChange = {
            viewModel.editWorkItem = viewModel.editWorkItem.copy(remark = it) as TimeworkData
        })

        Blank(10.dp)

        Row(modifier=Modifier.fillMaxWidth()) {
            Cancel2Button(modifier = Modifier.weight(1f)) {
                viewModel.formType = FormType()
            }
            Blank()
            OkButton(modifier = Modifier.weight(1f)) {
                viewModel.doSaveWorkData()
            }
        }

        if(!viewModel.editWorkItem.isAdd()){
            DeleteText {
                viewModel.deleteType = FormType(type="deleteSign")
            }
        }

    }


}
