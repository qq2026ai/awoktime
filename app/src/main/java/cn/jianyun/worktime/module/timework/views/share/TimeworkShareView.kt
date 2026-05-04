package cn.jianyun.worktime.module.timework.views.share



import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.timework.vm.TimeworkShareViewModel
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.InputItemView
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SegmentPickerView
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.goBack

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkShareView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<TimeworkShareViewModel>()
    viewModel.tryReload()

    Scaffold(content = {

        Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HeaderView(title = "分享和导入工时", backAction = {
                    goBack(navHostController)
                })
                Column(
                    modifier = Modifier
                        .padding(10.dp, 25.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 80.dp)
                ) {

                    CenterRow(padding= 10.dp, paddingBottom = 10.dp) {
                        SegmentPickerView(value=viewModel.curPage, options = SelectUtil.initValues("分享", "share", "导入", "import")) {
                            viewModel.curPage = it
                        }
                    }

                    if(viewModel.curPage == "share"){
                        GroupView {
                            Text("默认情况下，工时数据仅保存在当前设备本地。如果你想把数据同步到其他设备或平台，可以先分享工时数据获得秘钥，再在新设备里通过秘钥导入。")
                        }
                        if(viewModel.shared){
                            LeadingHintView(label="分享秘钥")
                            GroupView(){
                                CenterRow {
                                    Text(viewModel.shareId, fontSize = 22.sp, modifier = Modifier.padding(10.dp))
                                }
                            }
                            Blank()
                            LongOkButton(label= "复制秘钥"){
                                viewModel.baseRepository.copyData(viewModel.shareId, true)
                            }
                        }
                        else{
                            Blank()
                            LongOkButton(label="分享工时数据"){
                                viewModel.doShare()
                            }
                        }
                    }
                    else{
                        GroupView{
                            Text("请在下方输入其他设备分享的工时数据秘钥")
                        }

                        GroupView{
                            InputItemView(label = "秘钥", value = viewModel.importSecret) {
                                viewModel.importSecret = it
                            }
                        }

                        LongOkButton(label= "确定导入"){
                            viewModel.doImport(navHostController)
                        }


                        Blank()

//                        FlowTagView(viewModel.samples.split("\n")) {
//                            viewModel.importSecret = it
//                            viewModel.doImport(navHostController)
//                        }

                    }

                }
            }
            LoadingDialog()
        }
    })
}
