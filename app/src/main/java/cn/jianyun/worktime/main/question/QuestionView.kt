package cn.jianyun.worktime.main.question

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.ui.component.nav.LazyAppPageView
import cn.jianyun.worktime.ui.component.nav.ListDataView
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.component.nav.PanelView

@Composable
fun QuestionView(navHostController: NavHostController, arguments: Bundle?){

    val module = arguments?.getString("module") ?: ""
    var viewModel = hiltViewModel<QuestionViewModel>()
    viewModel.initModel(module)

    LazyAppPageView(navHostController = navHostController, title = "常见问题解答") {

        ListDataView(datalist = viewModel.questions, paddingBottom = 0.dp, end = false) {
            PanelView(it.title, it.content)
        }

    }
}
