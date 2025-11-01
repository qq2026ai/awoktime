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
import androidx.compose.ui.unit.sp
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
import cn.jianyun.worktime.ui.component.nav.PanelView
import cn.jianyun.worktime.ui.component.nav.SelfHeaderView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.toPage
import cn.jianyun.worktime.util.toVipPage
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


            PanelView("了解如何合理创建薪水?") {
                Column {
                    Text("1.假如你是小时工，正班18元每小时，加班是1.5倍，周末2倍。那么可以建3个薪水，用户平时根据实际上班类型选择对应的薪水即可", fontSize = 13.sp)
                    Blank()
                    Text("2.假如你是底薪+加班，你可以建一个底薪的补贴项，放在每月月初即可，然后创建一个加班的薪水，如果某一天有加班，可以添加对应的工时和薪水", fontSize = 13.sp)
                    Blank()
                    Text("3.假如你是综合工时【底薪+(总时长-168)*加班薪水】，底薪通过补贴的形式添加，放在每月月初即可。由于每个月干满168小时后算加班，可以先添加一个0元薪水，然后每天正常统计工时，到月底了统计下总工时。然后根据公式自行计算下，后续我们会在APP自动计算", fontSize = 13.sp)
                    Blank()
                    Text("4.薪水一旦创建，我们不支持修改，请添加的时候认真考虑，如果想要修改薪水工资，可以考虑先隐藏旧的，然后添加一个新的薪水", fontSize = 13.sp)
                }
            }

            viewModel.datalist.filter{ifv(viewModel.showAll, true, it.shown)}.forEach{
                GroupView(verticalPadding = 15.dp, modifier= Modifier.clickable {
                    toPage(navHostController,TimeworkRouter.TimeworkSalaryEdit.route, bundleOf("model" to it.toJSONString()))
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
                if(viewModel.datalist.count() >= 5 && !viewModel.baseRepository.isVip()){
                    toVipPage(navHostController)
                }
                else{
                    toPage(navHostController,TimeworkRouter.TimeworkSalaryEdit.route, bundleOf("model" to TimeworkSalary().toJSONString()))
                }
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