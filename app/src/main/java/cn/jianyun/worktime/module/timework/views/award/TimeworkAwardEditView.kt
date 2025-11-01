package cn.jianyun.worktime.module.timework.views.award



import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.DELETE
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.vm.TimeworkAwardViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.LongDeleteButton
import cn.jianyun.worktime.ui.component.form.LongOk2Button
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SegmentItemView
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.util.goBack
import com.alibaba.fastjson2.JSON

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkAwardEditView(navHostController: NavHostController, arguments: Bundle?) {

    var viewModel = hiltViewModel<TimeworkAwardViewModel>()

    val model = arguments?.getString("model")
    val editInfo = JSON.parseObject(model, TimeworkAward::class.java)
    viewModel.initModel(editInfo)

    Scaffold (content = {

        Box(modifier= Modifier, contentAlignment = Alignment.BottomCenter){
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)) {
                HeaderView(title= ifv(editInfo.uuid == "", "添加", "编辑"), backAction = {
                    goBack(navHostController)
                })
                Column(modifier = Modifier
                    .padding(10.dp, 25.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
                ) {
                    GroupView {
                        SegmentItemView(label = "类型", value = viewModel.editItem.type, options=SelectUtil.AWARD_TYPES, onValueChange = {
                            viewModel.editItem = viewModel.editItem.copy(type = it)
                        })
                        InputItemView(
                            label = "名称",
                            value = viewModel.editItem.name,
                            onValueChange = {
                                viewModel.editItem = viewModel.editItem.copy(name = it)
                            }
                        )
                        InputNumberView(
                            label = "默认值",
                            unit = "元",
                            decimal = true,
                            minValue = 0,
                            value = viewModel.editItem.defaultValue,
                            onValueChange = {
                                viewModel.editItem = viewModel.editItem.copy(defaultValue = it.trim())
                            }
                        )
                    }

                    if(viewModel.editItem.type == "award"){
                        LeadingHintView("常见补贴")
                        FlowTagView(options = viewModel.commonAwards, onClick = {
                            viewModel.editItem = viewModel.editItem.copy(name = it)
                        })
                    }
                    else{
                        LeadingHintView("常见扣款")
                        FlowTagView(options = viewModel.commonFines, onClick = {
                            viewModel.editItem = viewModel.editItem.copy(name = it)
                        })
                    }

                    LongOkButton("保存") {
                        viewModel.save(navHostController)
                    }
                    if(!viewModel.editItem.isAdd()) {
                        Blank()
                        LongOk2Button(ifv(viewModel.editItem.shown, "隐藏", "显示")) {
                            viewModel.doHide(navHostController)
                        }
                        Blank()
                        LongDeleteButton {
                            viewModel.formType = FormType.delete()
                        }
                    }
                }
            }

            if(viewModel.formType.isForm(DELETE)) {
                DeleteDialog(okAction = {
                    viewModel.doDelete(navHostController)
                }) {
                    viewModel.resetForm()
                }
            }
        }
    })

}