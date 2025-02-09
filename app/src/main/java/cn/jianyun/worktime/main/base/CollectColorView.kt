//package cn.jianyun.worktime.views.base
//
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.os.bundleOf
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import cn.jianyun.worktime.model.FormType
//import cn.jianyun.worktime.ui.component.form.BottomSheet
//import cn.jianyun.worktime.ui.component.form.DoubleButtonGroup
//import cn.jianyun.worktime.ui.component.form.GroupView
//import cn.jianyun.worktime.ui.component.form.SheetModel
//import cn.jianyun.worktime.ui.component.form.ShowColorPickerView
//import cn.jianyun.worktime.ui.component.form.ShowInputDialog
//import cn.jianyun.worktime.ui.component.nav.CircleBoxView
//import cn.jianyun.worktime.ui.component.nav.EmptyDataView
//import cn.jianyun.worktime.ui.component.nav.HeaderIcon
//import cn.jianyun.worktime.ui.component.nav.HeaderView
//import cn.jianyun.worktime.ui.component.nav.IconFont
//import cn.jianyun.worktime.ui.theme.ThemeColor
//
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun CollectColorView(navHostController: NavHostController) {
//
//    var viewModel = hiltViewModel<ColorViewModel>()
//
//    var showColor by remember { mutableStateOf(false) }
//    var showInputColor by remember { mutableStateOf(false) }
//
//    Scaffold (content = {
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.surface)) {
//            HeaderView(title= "我收藏的颜色", backAction = {
//                goBack(navHostController)
//            }, rightTool = {
//                Row{
//                    HeaderIcon(IconFont.share){
//
//                        if(viewModel.listColor().isEmpty()){
//                            viewModel.toast("请至少一个颜色后再分享")
//                        }
//                        else{
//                            viewModel.copyData(viewModel.collectColors)
//                            viewModel.toast("恭喜，所有颜色已复制到粘贴板")
//                        }
//                    }
//                }
//            })
//
//            Box(contentAlignment = Alignment.BottomEnd){
//                Column(modifier = Modifier
//                    .padding(10.dp)
//                    .fillMaxHeight()
//                    .verticalScroll(rememberScrollState())
//                    .padding(bottom = 80.dp)
//                ) {
//                    viewModel.listColor().forEach {
//                        GroupView(verticalPadding = 15.dp, modifier = Modifier.clickable {
//                            viewModel.formType = FormType("edit", it)
//                        }) {
//                            Row {
//                                CircleBoxView(
//                                    size = 20.dp,
//                                    background = it.color(),
//                                    action = {
//
//                                    }) {
//                                }
//                                Text(it.uppercase(), modifier = Modifier.padding(start = 10.dp))
//                            }
//                        }
//                    }
//
//                    if(viewModel.collectColors == ""){
//                        EmptyDataView(label = "当前暂无收藏的颜色")
//                    }
//                }
//
//                GroupView(verticalPadding = 10.dp){
//                    Column {
//                        DoubleButtonGroup(label1 = "添加颜色", label2 = "批量添加", okAction1 = {
//                            showColor = true
//                        }, okAction2 = {
//                            showInputColor = true
//                        })
//                    }
//                }
//            }
//
//            if(showColor){
//                ShowColorPickerView(value = "", onValueChange = {
//                    showColor = false
//                    if(it != ""){
//                        viewModel.addCollectColor(it)
//                    }
//                })
//            }
//
//            if(showInputColor){
//                ShowInputDialog(value = "", tip="支持一次性填写多个颜色代码，以逗号分隔，单个颜色代码示例：#AEAB03", multiLine = true, onValueChange = {
//                    showInputColor = false
//                    if(it != ""){
//                        viewModel.batchAddCollectColor(it)
//                    }
//                })
//            }
//
//        }
//        if(viewModel.isForm("edit")) {
//            BottomSheet( title="执行操作",  sheets = listOf(
//                SheetModel("删除", MaterialTheme.colorScheme.error){
//                    viewModel.removeCollectColor(viewModel.formType.editItem as String)
//                    viewModel.reset()
//                },
//            ), onDismissRequest = { viewModel.reset() })
//        }
//
//    })
//
//}