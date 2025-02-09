package cn.jianyun.worktime.module.timework.views.salary


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkSalaryViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.SortDialogView
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.HeaderTitle
import cn.jianyun.worktime.ui.component.nav.SelfHeaderView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import com.alibaba.fastjson2.toJSONString

@Composable
fun TimeworkSalaryView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkSalaryViewModel>()
    viewModel.tryReload()

    Column {
        SelfHeaderView {
            HeaderTitle(title = "薪水管理")
        }

        Column(modifier= Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)) {
            viewModel.datalist.filter{ifv(viewModel.showAll, true, it.shown)}.forEach{
                GroupView(verticalPadding = 15.dp, modifier= Modifier.clickable {
                    navHostController.navigateTo(TimeworkRouter.TimeworkSalaryEdit.route, bundleOf("model" to it.toJSONString()))
                }) {
                    TwoColumnView {
                        VerticalRow {
                            Text(it.name, fontWeight = FontWeight.Medium)
                            Blank(5.dp)
                            if(it.type == "over"){
                                TagView(tag = "加班")
                            }
                            if(!it.shown){
                                Blank(5.dp)
                                TagView(tag = "隐藏", hollow = true, color= PrimaryColor)
                            }
                        }
                        Text(it.showValue, color= ThemeColor)
                    }
                }
            }
            LongCancelButton("添加薪水") {
                navHostController.navigateTo(TimeworkRouter.TimeworkSalaryEdit.route, bundleOf("model" to TimeworkSalary().toJSONString()))
            }

            Blank()
            CenterRow {
                if(viewModel.datalist.count{!it.shown} > 0){
                    Blank()
                    SmallLinkText(text = ifv(viewModel.showAll, "仅显示可用薪水", "显示全部薪水")) {
                        viewModel.showAll = !viewModel.showAll
                    }
                }
                Blank()
                if(viewModel.datalist.filter{it.shown}.size > 1){
                    SmallLinkText(text = "薪水排序") {
                        viewModel.formType = FormType.sort()
                    }
                    Blank()
                }
            }

            if(viewModel.formType.isSort()) {
                SortDialogView(title = "薪水排序", options = viewModel.datalist.filter{it.shown}.map{it.toSelect()}, onChange = {
                    viewModel.doSort(it)
                    viewModel.formType = FormType()
                }) {
                    viewModel.formType = FormType()
                }
            }
        }
    }



}