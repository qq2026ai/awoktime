package cn.jianyun.worktime.module.timework.views.salary


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.DELETE
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.views.stat.Table
import cn.jianyun.worktime.module.timework.views.stat.getHourContent
import cn.jianyun.worktime.module.timework.views.stat.getHourHeader
import cn.jianyun.worktime.module.timework.vm.SalaryInfo
import cn.jianyun.worktime.module.timework.vm.TimeworkSalaryViewModel
import cn.jianyun.worktime.ui.component.form.ConfirmDialog
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
import cn.jianyun.worktime.ui.component.nav.DeleteLinkText
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.mainBg
import com.alibaba.fastjson2.JSON

val detailSize = listOf(120, 60, 120, 80)


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkSalaryEditView(navHostController: NavHostController, arguments: Bundle?) {

    var viewModel = hiltViewModel<TimeworkSalaryViewModel>()

    val model = arguments?.getString("model")

    val editInfo = JSON.parseObject(model, TimeworkSalary::class.java)

    viewModel.initModel(editInfo)

    val editItem = viewModel.editItem

    val readOnly = !editItem.isAdd() && !viewModel.allowEdit

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
                        if(viewModel.allowEdit){
                            LongOkButton(label = "确认编辑") {
                                viewModel.formType = FormType(type = "confirmEdit")
                            }
                        }
                        else{
                            LongOkButton(label = "我要编辑薪水") {
                                viewModel.allowEdit = true
                            }
                        }

                        Blank(20.dp)

                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                            SmallLinkText(ifv(editItem.shown, "隐藏", "显示") + "薪水") {
                                viewModel.hide(navHostController)
                            }
                            Blank(20.dp)
                            DeleteLinkText("删除薪水") {
                                if(viewModel.workList.size > 0){
                                    viewModel.baseRepository.toast("当前薪水已经有打卡记录，禁止删除")
                                }
                                else{
                                    viewModel.formType = FormType.delete()
                                }
                            }
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


                    //显示打卡记录
                    if(!editItem.isAdd()){
                        LeadingHintView("打卡记录（${viewModel.workList.size}次）")

                        if(viewModel.workList.isEmpty()){
                            Text("当前薪水暂无打卡记录")
                        }
                        else{
                            Column(modifier=Modifier.height((Math.min(Math.max(viewModel.workList.size * 30, 300), 500)).dp)){
                                Table(
                                    modifier = Modifier
                                        .mainBg(6.dp)
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                    ,
                                    columnCount = 4,
                                    rowCount = viewModel.workList.size + 1,
                                    afterRow = {
                                        Text("", modifier = Modifier
                                            .background(Color.Gray.copy(0.12f))
                                            .width(detailSize.sum().dp)
                                            .height(ifv(it < viewModel.workList.size - 1, 1.dp, 0.dp)))
                                    },
                                    cellContent = { columnIndex, rowIndex ->
                                        if(rowIndex == 0){
                                            getSalaryHeader(x = columnIndex)
                                        }
                                        else{
                                            getSalaryDetail(viewModel.workList[rowIndex-1], columnIndex)
                                        }
                                    })
                            }
                        }
                    }
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

            if(viewModel.formType.isForm("confirmEdit")) {
                ConfirmDialog(title="如果你只是临时调整薪资，建议添加一个薪水，因为直接修改薪水，历史打卡金额也会受影响，你真的要保存吗？", okAction= {
                    viewModel.save(navHostController)
                }, cancelAction = {
                    viewModel.resetForm()
                })
            }
        }
    })
}

@Composable
fun getSalaryHeader(x: Int) {
    val titles = listOf("日期", "类型", "时长", "收入")
    return Text(titles[x], fontSize = 14.sp, modifier = Modifier.width(detailSize[x].dp).height(30.dp))
}

@Composable
fun getSalaryDetail(salary: SalaryInfo, x: Int) {
    var tag = ""
    if(x == 0){
        tag = salary.day
    }
    else if(x == 1){
        tag = ifv(salary.type == "normal" , "正班" , "加班")
    }
    else if(x == 2){
        tag =  MyDataTool.getShownTime(salary.period)
    }
    else if(x == 3){
        tag = salary.price
    }
    return Text(tag, fontSize = 14.sp, modifier = Modifier.width(detailSize[x].dp).height(30.dp))
}
