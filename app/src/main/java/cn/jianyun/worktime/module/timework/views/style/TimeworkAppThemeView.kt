
import android.annotation.SuppressLint
import android.widget.GridView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.module.timework.vm.TimeworkAppConfigViewModel
import cn.jianyun.worktime.ui.component.form.AdderView
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.ColorSelectItemView
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.form.SegmentItemView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.form.ZeroGroupView
import cn.jianyun.worktime.ui.component.nav.CircleBoxView
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeworkAppThemeView(navHostController: NavHostController) {
    Scaffold(content = {
        Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HeaderView(title = "切换主题色", backAction = {
                    goBack(navHostController)
                })

                Column(
                    modifier= Modifier
                        .padding(10.dp, 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    TimeworkThemeView(padding = 10.dp){
                        goBack(navHostController)
                    }
                }
            }
        }
    })
}

var colors = listOf("#45B787", "#A6BAB0","#5E5D65","#B7B7BD","#B09279","#9EA886",
    "#5B7494","#A05B52","#D19977", "#B98A82", "#ACAB79", "#6F7887",
    "#7F8976","#9AA2AE","#BFCAC3","#79677B","#8B7C93", "#8D91AA",
     "#F07C82","#894276","#97846C","#12AA9C","#E7A23F", "#8DC269",
    "#D99156", "#475164","#EE8055","#8A998E", "#22A2C3", "#FA7E23"
  )

@Composable
fun TimeworkThemeView(modifier:Modifier = Modifier, padding: Dp = 0.dp, onDismiss: () -> Unit){
    val viewModel = hiltViewModel<TimeworkAppConfigViewModel>()
    viewModel.tryReload()

    Column {

        LeadingHintView("当前主题色")

        Row(modifier=Modifier.fillMaxWidth().height(100.dp).radius(10.dp).background(viewModel.nowColor.color())){

        }

        Blank(20.dp)
        LeadingHintView("点击切换主题色")

        ZeroGroupView(horizonPadding = padding) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                // 固定两列
                columns = GridCells.Fixed(6) ,
                contentPadding = PaddingValues(10.dp),
                content = {
                    items(colors.size, key = {it}) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)){
                            CircleBoxView(size= 30.dp, background = colors[it].color(), action = {
                                viewModel.viewModelScope.launch {
                                    viewModel.baseRepository.cache("themeColor", colors[it])
                                }
                                viewModel.nowColor = colors[it]
                                ThemeColor = colors[it].color()
                            }){
                                Text(ifv(colors[it] == viewModel.nowColor, "√", ""), fontSize = 20.sp)
                            }
                        }
                    }
                }
            )
        }

    }


}