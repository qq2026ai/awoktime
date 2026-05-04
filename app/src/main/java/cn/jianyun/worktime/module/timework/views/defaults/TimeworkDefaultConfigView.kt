package cn.jianyun.worktime.module.timework.views.defaults


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig
import cn.jianyun.worktime.module.timework.route.TimeworkRouter
import cn.jianyun.worktime.module.timework.vm.TimeworkDefaultConfigViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.SortDialogView
import cn.jianyun.worktime.ui.component.nav.DeleteText
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.HintText
import cn.jianyun.worktime.ui.component.nav.PanelView
import cn.jianyun.worktime.ui.component.nav.TagView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.util.toPage
import cn.jianyun.worktime.util.toVipPage
import com.alibaba.fastjson2.toJSONString

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkDefaultConfigView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkDefaultConfigViewModel>()
    viewModel.tryReload()

    Scaffold(content = {

        Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HeaderView(title = "快捷打卡管理", backAction = {
                    goBack(navHostController)
                })
                Column(
                    modifier = Modifier
                        .padding(10.dp, 25.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 80.dp)
                ) {

                    PanelView(title= "快捷打卡说明") {
                        Text("最新版本支持多个默认打卡配置，同时支持工时和补扣打卡，在主页只要点击快捷打卡选项，就可以快速打卡，且无需弹窗", fontSize = 13.sp)
                        Blank(3.dp)
                        Text("使用场景:", fontSize = 14.sp)
                        Blank(3.dp)
                        Text("1.张三周一到周五08:00到17:00为正班时间，薪水是A，18:00到21:00是加班时间，薪水是B，且每次加班有晚餐补贴，那么张三就可以建三个默认打卡，在主页可以点击3个就可以了", fontSize = 13.sp)
                        Blank(3.dp)
                        Text("2.张三除了工作日上班，周末还在做其它日结工作，就可以再建一个日结的默认打卡", fontSize = 13.sp)
                        Blank(3.dp)
                        Text("值得说明的一点是，对于那些只是临时不用的快捷打卡可以先隐藏，这样避免主页显示过多的快捷打卡选项，当然，删除也没有关系啦", fontSize = 13.sp)
                    }

                    viewModel.datalist.forEach {
                        GroupView(verticalPadding = 15.dp, modifier = Modifier.clickable {
                            toPage(navHostController,
                                TimeworkRouter.TimeworkDefaultEdit.route,
                                bundleOf("model" to it.toJSONString())
                            )
                        }) {
                            TwoColumnView {
                                Column {
                                    VerticalRow {
                                        Text(it.name, fontWeight = FontWeight.Medium)
                                        Blank(5.dp)
                                        TagView(tag = SelectUtil.getLabel(SelectUtil.DEFAULT_TYPES, it.type), color=SelectUtil.getColor(SelectUtil.DEFAULT_TYPES, it.type).color())
                                        if(!it.shown){
                                            Blank(5.dp)
                                            TagView(tag = "已隐藏", color = Color.Gray)
                                        }
                                    }
                                    if(it.remark != ""){
                                        HintText(label = "备注：" + it.remark)
                                    }
                                }

                                Text(it.aliasName, color= Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                    LongCancelButton("添加") {
                        //判断vip
                        toPage(navHostController, TimeworkRouter.TimeworkDefaultEdit.route, bundleOf("model" to TimeworkDefaultConfig().toJSONString()))
                    }

                    if(viewModel.datalist.size > 1){
                        DeleteText("排序") {
                            viewModel.formType = FormType.sort()
                        }
                    }

                    if (viewModel.formType.isSort()) {
                        SortDialogView(
                            title = "排序",
                            options = viewModel.datalist.filter { it.shown }.map { it.toSelect() },
                            onChange = {
                                viewModel.doSort(it)
                                viewModel.formType = FormType()
                            }) {
                            viewModel.formType = FormType()
                        }
                    }


                }
            }
        }

    })
}
