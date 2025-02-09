package cn.jianyun.worktime.module.timework.views.salary


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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.DELETE
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.vm.TimeworkSalaryViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.goBack
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
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.ShownItemView
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import com.alibaba.fastjson2.JSON

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkSalaryEditView(navHostController: NavHostController, arguments: Bundle?) {

    var viewModel = hiltViewModel<TimeworkSalaryViewModel>()

    val model = arguments?.getString("model")

    val editInfo = JSON.parseObject(model, TimeworkSalary::class.java)

    viewModel.initModel(editInfo)

    val editItem = viewModel.editItem

    val readOnly = !editItem.isAdd()

    Scaffold (content = {

        Box(modifier= Modifier, contentAlignment = Alignment.BottomCenter){
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)) {
                HeaderView(title= ifv(editInfo.uuid == "", "添加", "薪水详情"), backAction = {
                    goBack(navHostController)
                })
                Column(modifier = Modifier
                    .padding(10.dp, 25.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
                ) {
                    GroupView {

                        InputItemView(label = "名称", readonly = readOnly, value = editItem.name, onValueChange = {
                            viewModel.editItem = editItem.copy(name = it)
                        })

                        SegmentItemView(label = "薪水类型", readonly = readOnly,value = editItem.type, onValueChange = {
                              viewModel.editItem = editItem.copy(type = it)
                        }, options = SelectUtil.SALARY_TYPES)

                        //正班
                        if(editItem.type == "normal"){
                            InputNumberView(label = "时薪", minValue = 0, readonly = readOnly,  value = editItem.value, unit = "元", decimal = true, onValueChange = {
                                viewModel.editItem = editItem.copy(value = it)
                            })
                        }
                        //加班
                        else{
                            SegmentItemView(label = "计算类型",  readonly = readOnly, value = editItem.overType, onValueChange = {
                                viewModel.editItem = editItem.copy(overType = it)
                            }, options = SelectUtil.OVER_SALARY_CALC_TYPES)

                            if(editItem.overType == "times") {

                                if(viewModel.getBaseSalarys().isEmpty()){
                                    Blank()
                                    Text(
                                        text = "提示：请先添加正班薪水再设置加倍薪水",
                                        color = DeleteColor,
                                        fontSize = 16.sp
                                    )
                                    Blank()
                                }

                                SelectItemView(label = "基础薪水",  readonly = readOnly, value = editItem.refSalary, onValueChange = {
                                    viewModel.editItem = editItem.copy(refSalary = it)
                                }, options = viewModel.getBaseSalarys())

                                InputNumberView(label = "倍数",  readonly = readOnly, value = editItem.amount, decimal = true, unit="倍", onValueChange = {
                                    viewModel.editItem = editItem.copy(amount = it)
                                }, options = listOf("1.5", "2", "3"))




                                if(editItem.amount != "" && editItem.refSalary != ""){
                                    ShownItemView(label = "加班薪水", value = "${editItem.makeShowValue(viewModel.datalist)}")
                                }



                            }
                            else{
                                InputNumberView(label = "时薪", minValue = 0,  readonly = readOnly, value = editItem.overValue, unit = "元", decimal = true, onValueChange = {
                                    viewModel.editItem = editItem.copy(overValue = it)
                                })
                            }
                        }
                        if(readOnly){
                            ShownItemView(label = "添加时间", value = editItem.gmtCreate)
                        }
                    }

                    if(!editItem.isAdd()) {
                        Blank()

                        LongOk2Button(ifv(editItem.shown, "隐藏", "显示")) {
                            viewModel.hide(navHostController)
                        }
                        Blank(5.dp)
                        LongDeleteButton {
                            viewModel.formType = FormType.delete()
                        }
                    }
                    else{
                        LongOkButton("保存") {
                            viewModel.save(navHostController)
                        }
                    }

                    if(!readOnly && (editItem.type == "normal" || editItem.overType != "times")){
                        Blank()
                        LongOk2Button("时薪不知道？用时薪计算器算下") {
                            viewModel.formType = FormType("calculator")
                        }
                    }

                    Blank()
                    LeadingHintView("温馨提示：薪水一旦创建，不支持修改，因为修改会影响历史设置的工时薪水，所以请您在添加薪水的时候认真一点，如果你的工资有调整，建议你新建一个工资，然后把旧的隐藏即可")

                }
            }

            if(viewModel.formType.isForm("calculator")) {
                TimeworkSalaryCalculatorView(flag = viewModel.formType.isForm("calculator")) {
                    viewModel.formType = FormType()
                    if(it != ""){
                        viewModel.editItem.value = it
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