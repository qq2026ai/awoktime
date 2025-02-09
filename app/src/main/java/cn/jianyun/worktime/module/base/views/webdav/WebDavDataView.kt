package cn.jianyun.worktime.module.base.views.webdav

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.MyWebdavTool
import cn.jianyun.worktime.util.focusColor
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.nav.EmptyDataView
import cn.jianyun.worktime.ui.component.nav.FocusText
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.OnlySmallTipText
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.component.nav.SmallTipText
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ThemeColor
import com.alibaba.fastjson2.JSON
import com.thegrizzlylabs.sardineandroid.DavResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject





@Composable
fun WebDavDataView(navHostController: NavHostController, arguments: Bundle?){

    var viewModel = hiltViewModel<WebDavViewModel>()
    val model = arguments?.getString("model")
    val editInfo = JSON.parseObject(model, WebDAVUser::class.java)
    viewModel.initModel(editInfo)

    PageView(navHostController, "我的文件", backAction = {
        if(viewModel.isRootPath()){
            goBack(navHostController)
        }
        else{
            viewModel.loadData(MyWebdavTool.getParentPath(viewModel.currentPath))
        }
    }) {
        SmallTipText(text = "${viewModel.currentPath}")
        if(viewModel.msg == ""){
            viewModel.files.forEach{
                GroupView(verticalPadding = 15.dp, modifier=Modifier.clickable(enabled = it.isDirectory) {
                    if(it.path == viewModel.parentPath){
                        viewModel.loadData(MyWebdavTool.getParentPath(it.path))
                    }
                    else{
                        viewModel.loadData(it.path)
                    }
                }) {
                    if(it.path == viewModel.parentPath){
                        VerticalRow {
                            IconView(icon = IconFont.folder, iconSize = 16.sp, color = ThemeColor)
                            Blank()
                            Text("../", color= ThemeColor)
                        }
                        OnlySmallTipText(text = it.path)
                    }
                    else{
                        VerticalRow {
                            IconView(icon = ifv(it.isDirectory, IconFont.folder, IconFont.file), iconSize = 16.sp,  color= focusColor(it.isDirectory))
                            Blank()
                            FocusText(it.name, focus = it.isDirectory)
                        }
                        OnlySmallTipText(text = it.path)
                    }
                }
//                VerticalRow(modifier=Modifier.clickable {
//                    viewModel.loadData(it.path)
//                }) {
//                    Text( it.name)
////                    Text(it.path)
//                }
            }


//            LongOkButton("检查"){
//                viewModel.testCheck()
//            }
//            LongOkButton("写入"){
//                viewModel.testWrite()
//            }
        }
        else{
            EmptyDataView(label = viewModel.msg)
        }
    }

}

@HiltViewModel
class WebDavViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : ViewModel() {

    var loginUser by mutableStateOf(WebDAVUser())
    var currentPath by mutableStateOf("/")

    var parentPath by mutableStateOf("/")
    var files by mutableStateOf(listOf<DavResource>())
    var msg by mutableStateOf("")
    var inited by mutableStateOf(false)


    var defaultPath by mutableStateOf("")

    fun initModel(user: WebDAVUser){
        if(inited){
            return
        }
        inited = true
        loginUser = user
        defaultPath = MyWebdavTool.getDefaultPath(user)
        reload()
    }

    fun reload(){
        loadData(defaultPath)
    }

    fun isRootPath():Boolean {
        return currentPath == defaultPath
    }

    fun loadData(path: String){
        var newPath = path
        viewModelScope.launch {
            baseRepository.loading(false)
            delay(100)
            val u = loginUser
            val rst = withContext(Dispatchers.IO){
                var visitPath = newPath
                if(newPath.startsWith(defaultPath)) {
                    visitPath = visitPath.substring(defaultPath.length)
                }
                MyWebdavTool.list(u, visitPath)
            }
            if(rst.success){
                files = rst.fetchResult() as List<DavResource>
                msg = ""
            }
            else{
                msg = rst.message
                files = emptyList()
            }
            currentPath = newPath
            parentPath = path
            baseRepository.finish()
        }
    }

}

