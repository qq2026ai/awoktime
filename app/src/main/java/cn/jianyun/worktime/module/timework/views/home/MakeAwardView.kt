package cn.jianyun.worktime.module.timework.views.home


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.Cancel2Button
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.OkButton
import cn.jianyun.worktime.ui.component.nav.DeleteText
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import com.alibaba.fastjson2.toJSONString

@Composable
fun MakeAwardView(viewModel: TimeworkMasterViewModel, navHostController: NavHostController){

    val editAwardItem = viewModel.editAwardItem

    BottomDialogView(title="${viewModel.getCurrentDateStr()}${editAwardItem.typeName()}打卡", height = 300.dp, onDismiss = {
        viewModel.formType = FormType()
    }) {

        LeadingHintView("选择一项${editAwardItem.typeName()}") {
            SmallLinkText("添加${editAwardItem.typeName()}") {
                viewModel.formType = FormType()
                navHostController.navigateTo(TimeworkRouter.TimeworkAwardEdit.route,
                    bundleOf("model" to  TimeworkAward(type=editAwardItem.awardType).toJSONString()))
            }
        }

        FlowTagView(value=editAwardItem.awardUuid, dialog=true, options = viewModel.awards.filter{it.shown && it.type == editAwardItem.awardType}.map{it.toSelect()}) {v ->
            viewModel.editAwardItem = viewModel.editAwardItem.copy(awardUuid = v)
            var defaultOne = viewModel.awards.find{it.uuid == v}
            if(defaultOne != null && defaultOne.defaultValue != ""){
                viewModel.editAwardItem = viewModel.editAwardItem.copy(awardValue = defaultOne.defaultValue)
            }
        }

        if(viewModel.awards.filter{it.shown && it.type == editAwardItem.awardType}.isEmpty()){
            SmallTipText(text = "当前补扣项为空，建议先添加一个再来操作", color= DeleteColor)
        }

        InputNumberView(label = "${editAwardItem.typeName()}", value = editAwardItem.awardValue, unit="元", onValueChange = {
            viewModel.editAwardItem = viewModel.editAwardItem.copy(awardValue = it)
        })

        InputItemView(label = "备注", value = editAwardItem.remark, onValueChange = {
            viewModel.editAwardItem = editAwardItem.copy(remark = it)
        })

        Blank(10.dp)
        Row(modifier=Modifier.fillMaxWidth()) {
            Cancel2Button(modifier = Modifier.weight(1f)) {
                viewModel.formType = FormType()
            }
            Blank()
            OkButton(modifier = Modifier.weight(1f)) {
                viewModel.doSaveAwardData()
            }
        }

        if(!editAwardItem.isAdd()){
            DeleteText {
                viewModel.deleteType = FormType("deleteAward")
            }
        }
    }


}
