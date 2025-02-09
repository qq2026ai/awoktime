package cn.jianyun.worktime.ui.component.nav

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mainBg
import cn.jianyun.worktime.ui.component.form.LoadingDialog
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PageView(navHostController: NavHostController,  title: String, rightTool:@Composable (() -> Unit)? = null, backAction: (() -> Unit)? = null, content: @Composable () -> Unit){

    Scaffold(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HeaderView(title = title,rightTool=rightTool, backAction = {
                goBack(navHostController, backAction)
            })
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)){
                content()
            }
            LoadingDialog()
        }

        BackHandler {
            goBack(navHostController, backAction)
        }
    })
}




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SubPageView(navHostController: NavHostController, paddingBottom:Dp = 80.dp, header:@Composable () -> Unit, rightTool:@Composable (() -> Unit)? = null, backAction: (() -> Unit)? = null, content: @Composable () -> Unit){

    Scaffold(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SubAppHeaderView(navHostController, rightTool) {
                header()
            }
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = paddingBottom)){
                content()
            }
            LoadingDialog()
        }

        BackHandler {
            goBack(navHostController, backAction)
        }
    })
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PageWithFooterView(navHostController: NavHostController, title: String = "", header: @Composable (() -> Unit)? = null, rightTool:@Composable (() -> Unit)? = null, backAction: (() -> Unit)? = null, footer: @Composable () -> Unit, paddingBottom: Dp =60.dp, content: @Composable () -> Unit){
    Scaffold(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if(header != null){
                SelfHeaderView(backAction= {
                     goBack(navHostController)
                }, rightTool=rightTool) {
                    header()
                }
            }
            else{
                HeaderView(title = title,rightTool=rightTool, backAction = {
                    goBack(navHostController, backAction)
                })
            }

            WithFooterView(footer, paddingBottom=0.dp){
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp)
                    .padding(bottom = paddingBottom)
                ){
                    content()
                }
            }
            LoadingDialog()
        }
        BackHandler {
            goBack(navHostController, backAction)
        }
    })
}

@Composable
fun WithFooterView(footer:@Composable () -> Unit, paddingBottom: Dp=60.dp, content:@Composable () -> Unit){
    Box(modifier=Modifier.fillMaxSize()) {
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(bottom = paddingBottom)) {
            content()
        }
        Row(modifier= Modifier
            .align(Alignment.BottomCenter)
            .mainBg(12.dp)
            .padding(10.dp, 5.dp)) {
            footer()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LazyAppPageView(navHostController: NavHostController? = null,title: String = "",rightTool: @Composable (() -> Unit)? = null,  body:@Composable  () -> Unit){
    Scaffold (content = {
        Column {
            AppHeaderView(navHostController=navHostController, title=title, rightTool=rightTool)
            Column(modifier= Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)) {
                body()
            }
            LoadingDialog()
        }
    })
}

//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun SubLazyAppPageView(navHostController: NavHostController? = null,header:@Composable () -> Unit,rightTool: @Composable (() -> Unit)? = null,  body:@Composable  () -> Unit){
//    Scaffold (content = {
//        Column {
//            SubAppHeaderView(navHostController=navHostController,  rightTool=rightTool) {
//                header()
//            }
//            Column(modifier= Modifier
//                .fillMaxSize()
//                .padding(horizontal = 0.dp)) {
//                body()
//            }
//            LoadingDialog()
//        }
//    })
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SelfPageView(navHostController: NavHostController, header: @Composable () -> Unit, content: @Composable () -> Unit){

    Scaffold(content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            header()
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)){
                content()
            }
        }
    })
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SinglePageView(navHostController: NavHostController, title: String,nav: @Composable () -> Unit, content: @Composable () -> Unit){
    Scaffold (content = {
        Box(modifier= Modifier, contentAlignment = Alignment.BottomCenter){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .background(MaterialTheme.colorScheme.background)) {
                HeaderView(title = title, backAction = {
                    goBack(navHostController)
                })
                Column(modifier= Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)){
                    content()
                }
            }
            nav()
        }
    })

}


@Composable
fun HeaderTitle(title: String) {
    Text(title,
        Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .wrapContentWidth(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
}


@Composable
fun HeaderView(title: String = "标题", backAction: (() -> Unit)? = null, rightTool:@Composable (() -> Unit)? = null){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)) {

        HeaderTitle(title)
        Row(modifier = Modifier
            .fillMaxSize()
            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)  {

            if(backAction != null){
                HeaderIcon(icon = IconFont.back) {
                    backAction()
                }
            }
            else{
                Text("")
            }
            if(rightTool != null){
                Row(Modifier){
                    rightTool!!()
                }
            }
        }
    }
}


@Composable
fun AppHeaderView(title: String = "标题", navHostController: NavHostController? = null, rightTool:@Composable (() -> Unit)? = null){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)) {

        HeaderTitle(title)
        Row(modifier = Modifier
            .fillMaxSize()
            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)  {

            if(navHostController != null){
                HeaderIcon(icon = IconFont.back) {
                    goBack(navHostController)
                }
            }
            else{
                Text("")
            }
            if(rightTool != null){
                Row(Modifier.padding(end= 10.dp)){
                    rightTool!!()
                }
            }
        }
    }
}

@Composable
fun SelfHeaderView(backAction: (() -> Unit)? = null, rightTool:@Composable (() -> Unit)? = null, content: @Composable () -> Unit){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp), contentAlignment = Alignment.Center) {

        content()

        Row(modifier = Modifier
            .fillMaxSize()
            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)  {

            if(backAction != null){
                HeaderIcon(icon = IconFont.back) {
                    backAction()
                }
            }
            else{
                Text("")
            }
            if(rightTool != null){
                Row(Modifier.padding(end= 10.dp)){
                    rightTool!!()
                }
            }
        }
    }
}


@Composable
fun SubAppHeaderView(navHostController: NavHostController? = null, rightTool:@Composable (() -> Unit)? = null, content: @Composable () -> Unit){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp), contentAlignment = Alignment.Center) {

        content()

        Row(modifier = Modifier
            .fillMaxSize()
            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)  {

            if(navHostController != null){
                HeaderIcon(icon = IconFont.home) {
                    goBack(navHostController)
                }
            }
            else{
                Text("")
            }
            if(rightTool != null){
                rightTool!!()
            }
        }
    }
}

@Composable
fun SubLazyAppPageView(navHostController: NavHostController? = null,rightTool: @Composable (() -> Unit)? = null, header:@Composable  () -> Unit, body:@Composable  () -> Unit){
    Column {
        SubAppHeaderView(navHostController, rightTool) {
            header()
        }
        Column {
            body()
        }
    }
}

@Composable
fun SimpleSubAppPageView(navHostController: NavHostController? = null,rightTool: @Composable (() -> Unit)? = null, headerTitle:String, body:@Composable  () -> Unit){
    Column {
        SubAppHeaderView(navHostController, rightTool) {
            HeaderTitle(title = headerTitle)
        }
        Column(modifier= Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)) {
            body()
        }
    }
}


@Composable
fun BigHeaderView(title: String = "标题", rightTool:@Composable () -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)  {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        rightTool()
    }
}



@Composable
fun <T> PageListDataView(datalist: List<T>, emptyInfo: String = "暂无记录", paddingBottom: Dp=0.dp, end: Boolean = true, before:@Composable (() -> Unit)? = null,  content:@Composable (T) -> Unit) {
    ListDataView(datalist, emptyInfo, paddingBottom, end,  before, content)
}

@Composable
fun <T> ListDataView(datalist: List<T>, emptyInfo: String = "暂无记录",  paddingBottom: Dp=50.dp, end: Boolean = true, before:@Composable (() -> Unit)? = null,  content:@Composable (T) -> Unit) {
    val listState = rememberLazyListState()
    var allSize = datalist.size + 2
    LazyColumnScrollbar(
        state = listState,
        settings = ScrollbarSettings.Default.copy(scrollbarPadding=0.dp, thumbSelectedColor= MaterialTheme.colorScheme.primary.copy(0.1f), thumbUnselectedColor = MaterialTheme.colorScheme.primary.copy(0.2f))
    ) {
        LazyColumn(modifier= Modifier
            .fillMaxHeight()
            .padding(bottom = paddingBottom), state = listState) {
            items(allSize, key = {it}) {v ->
                if(v == 0){
                    if(before != null){
                        before()
                    }
                }
                else if(v == allSize - 1) {
                    if(datalist.isEmpty()){
                        EmptyDataView(label = emptyInfo)
                    }
                    else{
                        CenterSmallTipText(text = ifv(end , "共${datalist.size}条记录", ""))
                    }
                }
                else{
                    content(datalist[v - 1])
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SubAppMainPageView(navHostController: NavHostController? = null, nav: @Composable (() -> Unit), rightTool: @Composable (() -> Unit)? = null, headerTitle:String, body:@Composable  () -> Unit){
    Scaffold{
        Box(modifier= Modifier, contentAlignment = Alignment.BottomCenter){
            Column {
                SubAppHeaderView(navHostController, rightTool) {
                    HeaderTitle(title = headerTitle)
                }
                Column(modifier= Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)) {
                    body()
                }
            }
            nav()
        }
    }
}

