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
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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
import cn.jianyun.worktime.ui.component.nav.PanelView
import cn.jianyun.worktime.ui.component.nav.SelfHeaderView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.toPage
import cn.jianyun.worktime.util.toVipPage
import com.alibaba.fastjson2.toJSONString

@Composable
fun TimeworkAwardView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkAwardViewModel>()
    viewModel.tryReload("award")

    Column {
        SelfHeaderView {
            HeaderTitle(title = "补扣管理")
        }

        Column(modifier= Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)) {


            PanelView("了解如何合理创建补贴及扣款?") {
                Column(){
                    Text("1.假如你是底薪+加班，你可以建一个底薪的补贴项，放在每月月初即可，然后创建一个加班的薪水，如果某一天有加班，可以添加对应的工时和薪水", fontSize = 13.sp)
                    Blank()
                    Text("2.假如你是综合工时【底薪+(总时长-168)*加班薪水】，底薪通过补贴的形式添加，放在每月月初即可。由于每个月干满168小时后算加班，可以先添加一个0元薪水，然后每天正常统计工时，到月底了统计下总工时。然后根据公式自行计算下，后续我们会在APP自动计算", fontSize = 13.sp)
                    Blank()
                    Text("3.假如你有全勤奖，可以在月底添加；假如你有餐补，可以每天添加；假如你有住宿补贴，可以在月初添加；用户根据实际补贴和扣款自行添加", fontSize = 13.sp)
                    Blank()
                    Text("4.此页面主要记录补贴和扣款的类目，具体打卡在首页", fontSize = 13.sp)
                }
            }


            viewModel.datalist.filter{viewModel.showAll || it.shown}.forEach{
                SwipeDeleteView(sid=it.uuid, onEdit = {
                    toPage(navHostController,TimeworkRouter.TimeworkAwardEdit.route, bundleOf("model" to it.toJSONString()))
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
                            Text(MyDataTool.getRealPrice(it.defaultValue, 2) + "元", color=it.typeColor())
                        }
                    }
                }
            }
            LongCancelButton("添加") {
                if(viewModel.datalist.count() >= 5 && !viewModel.baseRepository.isVip()){
                    toVipPage(navHostController)
                }
                else{
                    toPage(navHostController,TimeworkRouter.TimeworkAwardEdit.route, bundleOf("model" to TimeworkAward().toJSONString()))
                }
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