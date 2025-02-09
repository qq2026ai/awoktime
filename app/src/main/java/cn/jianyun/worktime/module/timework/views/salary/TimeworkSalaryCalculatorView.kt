package cn.jianyun.worktime.module.timework.views.salary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.LongOk2Button
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.OnlySmallTipText
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.toFloatData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@Composable
fun TimeworkSalaryCalculatorView(flag: Boolean, dismiss: (String) -> Unit){

    var viewModel = hiltViewModel<CalculatorViewModel>()

    BottomDialogView(title = "时薪计算器", onDismiss = {
        dismiss("")
    }) {

        SmallTipText(text = "这里的总工资，可以根据用户实际情况来填写，如果是按月计算基本工资，就输入月薪，如果是按周计算，就输入周薪")

        InputNumberView(label = "总工资", value = viewModel.salary, unit = "元", decimal = false, onValueChange = {
            viewModel.salary = it
        })

        InputNumberView(label = "每天工作时长",  decimal = true, value = viewModel.hours, unit="小时",  onValueChange = {v ->
            viewModel.hours = v
        })

        InputNumberView(label = "工作天数", decimal = true,  value = viewModel.days, unit="天",  onValueChange = {v ->
            viewModel.days = v
        })

        FlowTagView(options = SelectUtil.REST_TYPES, singleLine = true){
            viewModel.days = it
        }
        Blank()

        OnlySmallTipText(text = "计算公式: 总工资/工作天数/每天工作时长")
        Blank()



        if(!viewModel.isEmpty()){
            if(!isOk(viewModel.test())){
                OnlySmallTipText(text = viewModel.test(), color = DeleteColor)
            }
            else{
                LongOk2Button( "计算时薪:${viewModel.cal()}元") {

                }
            }
        }

        if(flag){
            Blank()
            LongOkButton("填入此薪水") {
                if(viewModel.isEmpty() || !isOk(viewModel.test())){
                    viewModel.baseRepository.toast(viewModel.test())
                    return@LongOkButton
                }
                dismiss(viewModel.cal())
            }
        }

        
    }

}


@HiltViewModel
class CalculatorViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : ViewModel() {

    var salary by mutableStateOf("")
    var hours by mutableStateOf("8")
    var days by mutableStateOf("26")

    fun isEmpty(): Boolean{
        return salary == "" || hours == "" || days == ""
    }

    fun test(): String {
        var hours = hours.toFloatData(2)
        if(hours <= 0 || hours >= 24){
            return ("工作时长必须大于0且小于24")
        }
        var salarys = salary.toFloatData(2)
        if(salarys <= 0){
            return ("薪水必须大于0")
        }
        var days = days.toFloatData(2)
        if(days <= 0){
            return ("工作天数必须大于0")
        }
        return "ok"
    }

    fun cal(): String {
        var hours = hours.toFloatData(2)
        if(hours <= 0 || hours >= 24){
            return ("工作时长必须大于0且小于24")
        }
        var salarys =salary.toFloatData(2)
        if(salarys <= 0){
            return ("薪水必须大于0")
        }
        var days = days.toFloatData(2)
        if(days <= 0){
            return ("工作天数必须大于0")
        }
        var baseValue = salarys / days / hours
        return (MyDataTool.getRealPrice(baseValue.toString(), 2))
    }

}