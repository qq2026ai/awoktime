package cn.jianyun.worktime.module.timework.views.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkMasterViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.nav.ListDataView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ThemeColor


@Composable
fun TimeworkProjectChooseView(nav: NavHostController, viewModel: TimeworkMasterViewModel) {

    BottomDialogView(title = "选择记工时项目", lazy = true, height = 500.dp, rightTool = {
        SmallLinkText(text = "管理") {
            nav.navigate(TimeworkRouter.TimeworkProjectManage.route)
        }
    }, onDismiss = {
        viewModel.resetForm()
    }) {
        ListDataView(datalist = viewModel.projects) {
            GroupView(dialog = true, modifier= Modifier.clickable {
                if(it.uuid == viewModel.currentProjectId){
                    viewModel.resetForm()
                }
                else{
                    viewModel.changeProject(it)
                }
            }) {
                TwoColumnView(padding=15.dp) {
                    Column {
                        VerticalRow {
                            Text(it.name)
                            Blank()
                            if(it.uuid == viewModel.currentProjectId){
                                TagView(tag = "当前项目")
                            }
                        }
                        if(viewModel.sizeMap[it.uuid] != null) {
                            Text( "${viewModel.sizeMap[it.uuid]}条工时记录", color = ThemeColor, fontSize = 12.sp)
                        }
                    }
                }

            }
        }

    }

}