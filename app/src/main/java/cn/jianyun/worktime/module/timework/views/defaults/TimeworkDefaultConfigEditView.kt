package cn.jianyun.worktime.module.timework.views.defaults



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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.model.DELETE
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkDefaultConfigViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.LongDeleteButton
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.LongOk2Button
import cn.jianyun.worktime.ui.component.form.SegmentItemView
import cn.jianyun.worktime.ui.component.form.SelectItemView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.form.TimePeriodPickerItemView3
import cn.jianyun.worktime.ui.component.form.TimePickerItemView3
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.toPage
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.toJSONString

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkDefaultConfigEditView(navHostController: NavHostController, arguments: Bundle?) {

    var viewModel = hiltViewModel<TimeworkDefaultConfigViewModel>()

    LaunchedEffect(Unit){
        viewModel.tryReload("defaultConfig")
        val model = arguments?.getString("model")
        val editInfo = JSON.parseObject(model, TimeworkDefaultConfig::class.java)
        viewModel.initModel(editInfo)
    }

    val editItem = viewModel.editItem
    val editWorkItem = viewModel.editWorkItem
    val editAwardItem = viewModel.editAwardItem

    Scaffold (content = {

        Box(modifier= Modifier, contentAlignment = Alignment.BottomCenter){
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)) {
                HeaderView(title= ifv(editItem.uuid == "", "添加", "编辑"), backAction = {
                    goBack(navHostController)
                })
                Column(modifier = Modifier
                    .padding(10.dp, 25.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
                ) {
                    GroupView {
                        InputItemView(label = "名称", value = editItem.name, onValueChange = {
                            viewModel.editItem = editItem.copy(name = it)
                        })

                        SegmentItemView(label = "类型", value = editItem.type, options=SelectUtil.DEFAULT_TYPES, onValueChange = {
                            viewModel.editItem = editItem.copy(type = it)
                        })
                    }

                    //打卡
                    if(editItem.type == "sign"){
                        LeadingHintView("设置打卡信息")
                        GroupView {
                            SegmentItemView(label = "打卡方式", value = editWorkItem.mode, options=SelectUtil.SIGN_TYPES, onValueChange = {
                                viewModel.editWorkItem = editWorkItem.copy(mode = it)
                            })

                            if(editWorkItem.mode == "hour"){

                                if(!editWorkItem.onlyOver){
                                    TimePeriodPickerItemView3(
                                        label = "正班时长",
                                        minuteStep = viewModel.appConfig.fetchMinuteStep(),
                                        value = editWorkItem.baseSalaryTime,
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
                                InputNumberView(label = "日结工资", decimal = true, value = editWorkItem.amount, unit="元", onValueChange = {
                                    viewModel.editWorkItem = editWorkItem.copy(amount = it.trim())
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
//                                else if(viewModel.appConfig.needDayPeriod) {
//                                    TimePeriodPickerItemView3(
//                                        label = "工作时长",
//                                        minuteStep = viewModel.appConfig.fetchMinuteStep(),
//                                        value = editWorkItem.baseSalaryTime,
//                                        onValueChange = {
//                                            viewModel.editWorkItem = editWorkItem.copy(baseSalaryTime = it)
//                                        }
//                                    )
//                                }
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
                                }, options = viewModel.checkSalarys(editWorkItem.overSalaryUuid))
                            }


                            InputItemView(
                                label = "备注",
                                value = editWorkItem.remark,
                                onValueChange = {
                                    viewModel.editWorkItem = editWorkItem.copy(remark = it)
                                }
                            )
                        }

                        if(viewModel.salarys.isEmpty()){
                            SmallTipText(text = "当前薪水为空，建议先添加一个再来操作", color= DeleteColor)
                            Blank()
                            LongOkButton("添加薪水") {
                                toPage(navHostController,TimeworkRouter.TimeworkSalaryEdit.route, bundleOf("model" to TimeworkSalary().toJSONString()))
                            }
                            Blank()
                        }
                    }

                    //补贴
                    if(editItem.type == "award"){
                        LeadingHintView("设置补贴信息")
                        GroupView {
                            SelectItemView(label = "选择补贴项", value = editAwardItem.awardUuid, onValueChange = {v ->
                                viewModel.editAwardItem = viewModel.editAwardItem.copy(awardUuid = v)
                                var defaultOne = viewModel.awardList.find{it.uuid == v}
                                if(defaultOne != null){
                                    viewModel.editAwardItem = viewModel.editAwardItem.copy(awardValue = defaultOne.defaultValue, awardName = defaultOne.name)
                                }
                            }, options =  viewModel.awardList.filter{it.type == "award"}.map{it.toSelect()})

                            InputNumberView(label = "补贴金额", unit="元", decimal = true, value = editAwardItem.awardValue, onValueChange = {
                                viewModel.editAwardItem = viewModel.editAwardItem.copy(awardValue = it)
                            })


                            InputItemView(
                                label = "备注",
                                value = editWorkItem.remark,
                                onValueChange = {
                                    viewModel.editWorkItem = editWorkItem.copy(remark = it)
                                }
                            )
                        }

                        if(viewModel.awardList.filter{it.type == "award"}.isEmpty()){
                            SmallTipText(text = "当前补贴项为空，建议先添加一个再来操作", color= DeleteColor)

                            Blank()
                            LongOkButton("添加补贴") {
                                toPage(navHostController,TimeworkRouter.TimeworkAwardEdit.route, bundleOf("model" to TimeworkAward(type="award").toJSONString()))
                            }
                            Blank()
                        }
                    }

                    //扣款
                    if(editItem.type == "fine"){
                        LeadingHintView("设置扣款信息")
                        GroupView {
                            SelectItemView(label = "选择扣款项", value = editAwardItem.awardUuid, onValueChange = {v ->
                                viewModel.editAwardItem = viewModel.editAwardItem.copy(awardUuid = v)
                                var defaultOne = viewModel.awardList.find{it.uuid == v}
                                if(defaultOne != null){
                                    viewModel.editAwardItem = viewModel.editAwardItem.copy(awardValue = defaultOne.defaultValue, awardName = defaultOne.name)
                                }
                            }, options =  viewModel.awardList.filter{it.type == "fine"}.map{it.toSelect()})

                            InputNumberView(label = "扣款金额", unit="元", decimal = true, value = editAwardItem.awardValue, onValueChange = {v ->
                                viewModel.editAwardItem = viewModel.editAwardItem.copy(awardValue = v)
                            })

                            InputItemView(
                                label = "备注",
                                value = editWorkItem.remark,
                                onValueChange = {
                                    viewModel.editWorkItem = editWorkItem.copy(remark = it)
                                }
                            )
                        }

                        if(viewModel.awardList.filter{it.type == "fine"}.isEmpty()){
                            SmallTipText(text = "当前扣款项为空，建议先添加一个再来操作", color= DeleteColor)

                            Blank()
                            LongOkButton("添加扣款") {
                                toPage(navHostController,TimeworkRouter.TimeworkAwardEdit.route, bundleOf("model" to TimeworkAward(type="fine").toJSONString()))
                            }
                            Blank()
                        }

                    }

                    LongOkButton("保存") {
                        viewModel.save(navHostController)
                    }
                    if(!editItem.isAdd()) {
                        Blank()
                        LongOk2Button(ifv(editItem.shown, "隐藏", "显示")) {
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
