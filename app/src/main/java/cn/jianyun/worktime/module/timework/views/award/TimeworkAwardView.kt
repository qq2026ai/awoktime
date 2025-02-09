package cn.jianyun.worktime.module.timework.views.award




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
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkAwardViewModel
import cn.jianyun.worktime.ui.component.nav.SwipeDeleteView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.ifv
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
import com.alibaba.fastjson2.toJSONString

@Composable
fun TimeworkAwardView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkAwardViewModel>()
    viewModel.tryReload()

    Column {
        SelfHeaderView {
            HeaderTitle(title = "补扣管理")
        }

        Column(modifier= Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)) {
            viewModel.datalist.filter{viewModel.showAll || it.shown}.forEach{
                SwipeDeleteView(sid=it.uuid, onEdit = {
                    navHostController.navigateTo(TimeworkRouter.TimeworkAwardEdit.route, bundleOf("model" to it.toJSONString()))
                }, onDelete = {
                    //判断是否删除
                    viewModel.doDeleteOne(it)
                }) {
                    TwoColumnView(modifier=Modifier.padding(15.dp, 20.dp)) {
                        VerticalRow {
                            Text(it.name, fontWeight = FontWeight.Medium)
                            Blank(5.dp)
                            TagView(tag = it.typeName(), color=it.typeColor())

                            if(!it.shown){
                                Blank()
                                TagView(tag = "隐藏", hollow = true, color= PrimaryColor)
                            }
                        }
                        if(it.defaultValue != ""){
                            Text(it.defaultValue + "元", color=it.typeColor())
                        }
                    }
                }
            }
            LongCancelButton("添加") {
                navHostController.navigateTo(TimeworkRouter.TimeworkAwardEdit.route, bundleOf("model" to TimeworkAward().toJSONString()))
            }

            CenterRow(padding=10.dp) {
                if(viewModel.datalist.count{!it.shown} > 0){
                    Blank()
                    SmallLinkText(text = ifv(viewModel.showAll, "仅显示可用补扣", "显示全部补扣")) {
                        viewModel.showAll = !viewModel.showAll
                    }
                }
                Blank()
                if(!viewModel.showAll && viewModel.datalist.filter{it.shown}.size > 1){
                    SmallLinkText(text = "补扣排序") {
                        viewModel.formType = FormType.sort()
                    }
                    Blank()
                }
            }

            if(viewModel.formType.isSort()) {
                SortDialogView(title = "排序", options = viewModel.datalist.filter{it.shown}.map{it.toSelect()}, onChange = {
                    viewModel.doSort(it)
                    viewModel.formType = FormType()
                }) {
                    viewModel.formType = FormType()
                }
            }
        }
    }

}