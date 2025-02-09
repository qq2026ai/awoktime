package cn.jianyun.worktime.module.timework.views.tool



import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.timework.vm.ImportDataViewModel
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.InputView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.ClipboardUtil

@Composable
fun ImportDataView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<ImportDataViewModel>()
    viewModel.tryReload()

    PageView(navHostController = navHostController, title = "导入小程序工时") {

        GroupView {
            Text(text = "如果你是小程序「极简记工时」老用户，想把小程序记录的工时数据导入当前APP,你只需要进入小程序，然后把「我的」页面顶部的用户ID复制后粘贴到下方即可")
        }

        InputView(placeholder = "输入小程序用户ID", value=viewModel.userId,  align= TextAlign.Center,  onValueChange = {
            viewModel.userId = it
        })
        Blank()
        LongOkButton("开始导入") {
            viewModel.makeImport(navHostController)
        }
        Blank()
        LongCancelButton("粘贴") {
            val tt = ClipboardUtil.getText(viewModel.baseRepository.context)
            if (tt != "") {
                if(tt.length != 8){
                    viewModel.baseRepository.toast("你粘贴的不是用户ID")
                }
                else{
                    viewModel.userId = tt.trim()
                }
            }
        }
    }

}