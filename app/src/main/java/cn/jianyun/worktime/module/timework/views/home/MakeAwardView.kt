package cn.jianyun.worktime.module.timework.views.home


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
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
import cn.jianyun.worktime.ui.component.form.ShowInputDialog
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.nav.DeleteLinkText
import cn.jianyun.worktime.ui.component.nav.DeleteText
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.util.toPage
import com.alibaba.fastjson2.toJSONString

@Composable
fun MakeAwardView(viewModel: TimeworkMasterViewModel, navHostController: NavHostController){

    val editAwardItem = viewModel.editAwardItem

    BottomDialogView(title="${viewModel.getFocusDateStr()}${editAwardItem.typeName()}打卡", height = 400.dp, onDismiss = {
        viewModel.formType = FormType()
    }) {

        LeadingHintView("选择一项${editAwardItem.typeName()}") {
            SmallLinkText("添加${editAwardItem.typeName()}") {
                viewModel.formType = FormType()
                toPage(navHostController, TimeworkRouter.TimeworkAwardEdit.route, bundleOf("model" to  TimeworkAward(type=editAwardItem.awardType).toJSONString()))
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

        InputNumberView(label = "${editAwardItem.typeName()}", decimal = true, value = editAwardItem.awardValue, unit="元", onValueChange = {
            viewModel.editAwardItem = viewModel.editAwardItem.copy(awardValue = it)
        })

        InputItemView(label = "备注", value = editAwardItem.remark, onValueChange = {
            viewModel.editAwardItem = editAwardItem.copy(remark = it)
        })

        SwitchItemView(label = "已结算", value = editAwardItem.isSettled(), onValueChange = {
            viewModel.editAwardItem = viewModel.editAwardItem.settle(it)
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


            if(!viewModel.editAwardItem.isAdd()){

                Row(modifier=Modifier.padding(30.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    DeleteLinkText("删除记录") {
                        viewModel.deleteType = FormType(type="deleteAward")
                    }
                    Blank(30.dp)
                    SmallLinkText("保存到快捷打卡") {
                        viewModel.renameInfo = viewModel.editAwardItem.fetchAliasName()
                        viewModel.renameMode = true
                    }
                }
            }

            if(viewModel.renameMode) {
                ShowInputDialog(value = viewModel.renameInfo,  tip = "请在上方输入快捷打卡名称，下次可以直接在首页点击“此快捷打卡名称”进行快速打卡操作", multiLine = false, onValueChange = {
                    if(it != ""){
                        viewModel.saveDefaultConfig(it)
                        true
                    }
                    else{
                        viewModel.renameMode = false
                        true
                    }
                }, onDismiss = {
                    viewModel.renameMode = false
                })

            }

        }
    }


}
