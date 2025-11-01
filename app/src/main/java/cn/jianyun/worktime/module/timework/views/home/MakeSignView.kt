package cn.jianyun.worktime.module.timework.views.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.Cancel2Button
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.OkButton
import cn.jianyun.worktime.ui.component.form.SegmentItemView
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.form.TimePeriodPickerItemView3
import cn.jianyun.worktime.ui.component.form.TimePickerItemView3
import cn.jianyun.worktime.ui.component.nav.DeleteText
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.util.ifv


@Composable
fun MakeSignView(viewModel: TimeworkMasterViewModel, navHostController: NavHostController){

    val editWorkItem = viewModel.editWorkItem

    BottomDialogView(title = "${viewModel.getCurrentDateStr()}工时打卡", onDismiss = {
        viewModel.formType = FormType()
    }) {

        SegmentItemView(label = "打卡类型", width=60.dp, value = editWorkItem.mode, options= SelectUtil.SIGN_TYPES, onValueChange = {
            viewModel.editWorkItem = editWorkItem.copy(mode = it)
        })

        if(editWorkItem.mode == "hour"){

            if(!editWorkItem.onlyOver){
                TimePeriodPickerItemView3(
                    label = "正班时长",
                    value = editWorkItem.baseSalaryTime,
                    minuteStep = viewModel.appConfig.fetchMinuteStep(),
                    onValueChange = {
                        viewModel.editWorkItem = editWorkItem.copy(baseSalaryTime = it)
                    }
                )
                SelectItemView(label = "正班薪水",
                    value = editWorkItem.salaryUuid,
                    onValueChange = {
                        viewModel.editWorkItem = editWorkItem.copy(salaryUuid = it)
                    },
                    options = viewModel.checkSalarys(editWorkItem.salaryUuid))

                SwitchItemView(label = "是否加班", value = editWorkItem.overTime , onValueChange = {
                    viewModel.editWorkItem = editWorkItem.copy(overTime = it)
                })

            }

            if(editWorkItem.overTime) {
                TimePeriodPickerItemView3(
                    label = "加班时长",
                    minuteStep = viewModel.appConfig.fetchMinuteStep(),
                    value = editWorkItem.overSalaryTime,
                    onValueChange = {
                        viewModel.editWorkItem = editWorkItem.copy(overSalaryTime = it)
                    }
                )

                SelectItemView(label = "加班薪水",
                    value = editWorkItem.overSalaryUuid,
                    onValueChange = {
                        viewModel.editWorkItem = editWorkItem.copy(overSalaryUuid = it)
                    },
                    options = viewModel.checkSalarys(editWorkItem.overSalaryUuid))

                SwitchItemView(label = "仅加班", value = editWorkItem.onlyOver , onValueChange = {
                    viewModel.editWorkItem = editWorkItem.copy(onlyOver = it)
                })
            }

        }
        if(editWorkItem.mode == "day"){
            InputNumberView(label = "日结工资", value = editWorkItem.amount, unit="元", decimal = true, onValueChange = {
                viewModel.editWorkItem = editWorkItem.copy(amount = it)
            })

            if(viewModel.appConfig.needDayTime){
                TimePickerItemView3(
                    label = "上班时间",
                    allowEmpty = true,
                    minuteStep = viewModel.appConfig.fetchMinuteStep(),
                    placeholder = "选填",
                    value = editWorkItem.beginTime,
                    onValueChange = {
                        viewModel.editWorkItem = editWorkItem.copy(beginTime = it)
                    }
                )
                TimePickerItemView3(
                    label = "下班时间",
                    allowEmpty = true,
                    minuteStep = viewModel.appConfig.fetchMinuteStep(),
                    placeholder = "选填",
                    value = editWorkItem.endTime,
                    onValueChange = {
                        viewModel.editWorkItem = editWorkItem.copy(endTime = it)
                    }
                )
                TimePeriodPickerItemView3(
                    label = "休息时长",
                    allowEmpty = true,
                    minuteStep = viewModel.appConfig.fetchMinuteStep(),
                    placeholder = "选填",
                    value = editWorkItem.restTime,
                    onValueChange = {
                        viewModel.editWorkItem = editWorkItem.copy(restTime = it)
                    }
                )
            }

        }

        if(editWorkItem.mode == "time"){
            TimePickerItemView3(
                label = "上班时间",
                minuteStep = viewModel.appConfig.fetchMinuteStep(),
                value = editWorkItem.beginTime,
                onValueChange = {
                    viewModel.editWorkItem = editWorkItem.copy(beginTime = it)
                }
            )
            TimePickerItemView3(
                label = "下班时间",
                minuteStep = viewModel.appConfig.fetchMinuteStep(),
                value = editWorkItem.endTime,
                onValueChange = {
                    viewModel.editWorkItem = editWorkItem.copy(endTime = it)
                }
            )
            TimePeriodPickerItemView3(
                label = "休息时长",
                allowEmpty = true,
                minuteStep = viewModel.appConfig.fetchMinuteStep(),
                value = editWorkItem.restTime,
                onValueChange = {
                    viewModel.editWorkItem = editWorkItem.copy(restTime = it)
                }
            )
            SelectItemView(label = "上班薪水", value = editWorkItem.salaryUuid, onValueChange = {
                viewModel.editWorkItem = editWorkItem.copy(salaryUuid = it)
            }, options = viewModel.checkSalarys(editWorkItem.salaryUuid))
        }

        InputItemView(label = "备注", value = editWorkItem.remark, onValueChange = {
            viewModel.editWorkItem = editWorkItem.copy(remark = it)
        })

        if(viewModel.salarys.isEmpty()){
            SmallTipText(text = "当前薪水为空，建议先添加一个再来操作", color= DeleteColor)
        }

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
