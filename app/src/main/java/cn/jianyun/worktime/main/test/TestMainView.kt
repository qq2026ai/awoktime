package cn.jianyun.worktime.main.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.ui.component.nav.NavItemView
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.SubAppMainPageView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@Composable
fun TestMainView(navHostController: NavHostController) {

    var viewModel = hiltViewModel<TestMainViewViewModel>()
    viewModel.tryReload("main")

    SubAppMainPageView(navHostController, headerTitle = "测试", nav = {
        Row(modifier= Modifier.background(MaterialTheme.colorScheme.background)) {
            NavItemView(label = "页面1", icon = IconFont.home, focus = viewModel.pageType == "v1", modifier = Modifier.weight(1f)) {
                viewModel.pageType = "v1"
            }
            NavItemView(label = "页面2", icon = IconFont.list, focus = viewModel.pageType == "v2", modifier = Modifier.weight(1f)) {
                viewModel.pageType = "v2"
            }
        }
    }) {


        if(viewModel.pageType == "v1") {
            TestV1()
        }

        if(viewModel.pageType == "v2"){
            TestV2()
        }
    }
}

@HiltViewModel
class TestMainViewViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : BaseViewModel() {


    var pageType by mutableStateOf("home")

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload() {

    }

    override fun reloadData(dataChanged: Boolean) {

    }

}