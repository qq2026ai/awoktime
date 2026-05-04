package cn.jianyun.worktime.ui.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.ui.component.nav.DialogTitleView
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.component.sort.DragNDropItemsList
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.GrayLinkText
import cn.jianyun.worktime.util.LinkText
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.WeekTool
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.views.base.PrivatePolicyView
import cn.jianyun.worktime.views.base.UserPolicyView
import cn.jianyun.worktime.vm.AppViewModel
import kotlinx.coroutines.delay

data class SheetModel(
    var name: String = "",
    var color: Color ,
    val shown: Boolean = true,
    var action: () -> Unit
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetDialog(title: String = "", onDismissRequest: () -> Unit, sheets: List<SheetModel>) {

    var openDialog by remember {
        mutableStateOf(true)
    }

    return Column {
        if(openDialog){
            AlertDialog(onDismissRequest = {
                openDialog = false
                onDismissRequest()
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                GroupView {
                    if(title != ""){
                        Blank(5.dp)
                        LeadingHintView(label=title)
                        Blank(10.dp)
                    }
                    sheets.forEach {
                        if(it.shown){
                            Text(it.name, lineHeight = 10.sp, color= it.color, modifier = Modifier
                                .radius(8.dp)
                                .clickable {
                                    it.action()
                                }
                                .height(48.dp)
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight())
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfDialog(cancelable:Boolean = false,  onDismiss: () -> Unit, content:@Composable () -> Unit){
    AlertDialog(onDismissRequest = {
        onDismiss()
    }, properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
    ) {
        Box(modifier= Modifier
            .fillMaxSize()
            .padding(20.dp)
            .tap {
                if (cancelable) {
                    onDismiss()
                }
            }, contentAlignment = Alignment.Center){
            Column(modifier= Modifier
                .tap {

                }
                .clip(RoundedCornerShape(20.dp))
                .background(colorScheme.surface)
                .padding(20.dp)
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullDialog(onDismiss: () -> Unit, content:@Composable () -> Unit){
    AlertDialog(onDismissRequest = {
        onDismiss()
    }, properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
    ) {
        Box(modifier= Modifier
            .fillMaxSize()
            .background(colorScheme.surface)
            .tap {

            }, contentAlignment = Alignment.TopStart){
            Column() {
                content()
            }
        }
    }
}

@Composable
fun DeleteDialog(title:String = "真的要删除吗?", okAction: () -> Unit, cancelAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            cancelAction()
        }, confirmButton = {
            LinkText(label = "确定", padding=5.dp, fontSize = 16.sp, color=MaterialTheme.colorScheme.error) {
                okAction()
            }
        }, dismissButton = {
            GrayLinkText(label = "取消",padding=5.dp, fontSize = 16.sp) {
                cancelAction()
            }
        }, title = {
            VerticalRow {
                IconView(icon = IconFont.info2, color= DeleteColor)
                Text("温馨提示", fontSize = 14.sp, color= DeleteColor)
            }
        }, text = {
            Text(title, fontSize = 18.sp, lineHeight = 25.sp)
        })
}


@Composable
fun ConfirmDialog(title:String, okLabel: String = "确定", cancelLabel: String = "取消", okAction: () -> Unit, cancelAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            cancelAction()
        }, confirmButton = {
            LinkText(label = okLabel, padding=5.dp, fontSize = 16.sp, color= ThemeColor) {
                okAction()
            }
        }, dismissButton = {
            GrayLinkText(label = cancelLabel, padding=5.dp, fontSize = 16.sp) {
                cancelAction()
            }
        }, title = {
            VerticalRow{
                IconView(icon = IconFont.info2, color= ThemeColor)
                Text("温馨提示", fontSize = 14.sp, color= ThemeColor)
            }
        }, text = {
            Text(title, fontSize = 18.sp, lineHeight = 30.sp)
        })
}



@Composable
fun TipDialog(title: String = "温馨提示", message: String = "这里是内容",  cancelAction: ()->Unit) {
    AlertDialog(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),onDismissRequest = {

    }, confirmButton = {
        LinkText(label = "我知道了", padding=5.dp, fontSize = 16.sp, color=colorScheme.error) {
            cancelAction()
        }
    }, dismissButton = null, title = {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }, text = {
        Text(message, fontSize = 16.sp, lineHeight = 22.sp)
    })
}

@Composable
fun CountdownTipDialog(
    title: String = "温馨提示",
    message: String = "这里是内容",
    waitSeconds: Int = 5,
    confirmText: String = "我知道了",
    confirmAction: () -> Unit
) {
    var remainSeconds by remember(title, message, waitSeconds) {
        mutableStateOf(waitSeconds.coerceAtLeast(0))
    }

    LaunchedEffect(title, message, waitSeconds) {
        remainSeconds = waitSeconds.coerceAtLeast(0)
        while (remainSeconds > 0) {
            delay(1000)
            remainSeconds -= 1
        }
    }

    val canConfirm = remainSeconds <= 0
    val buttonText = if (canConfirm) {
        confirmText
    } else {
        "$confirmText(${remainSeconds}s)"
    }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        onDismissRequest = {

        },
        confirmButton = {
            Text(
                text = buttonText,
                fontSize = 16.sp,
                color = if (canConfirm) colorScheme.error else colorScheme.tertiary,
                modifier = Modifier
                    .radius(4.dp)
                    .clickable(enabled = canConfirm) {
                        confirmAction()
                    }
                    .padding(5.dp)
            )
        },
        dismissButton = null,
        title = {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(message, fontSize = 16.sp, lineHeight = 22.sp)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WelcomeDialog(okAction: ()->Unit) {

    var formType by remember {
        mutableStateOf(FormType(type= ""))
    }

    AlertDialog(
        onDismissRequest = {
        System.exit(1)
    },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(0.99f)
            .padding(10.dp)) {

        if(formType.type == Router.PrivatePolicy.route){

            Column(modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(MaterialTheme.colorScheme.background)) {
                HeaderView(title= "App隐私政策", backAction = {
                    formType = FormType()
                })
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                ) {
                    PrivatePolicyView()
                }
            }

        }
        else if(formType.type == Router.UserPolicy.route){
            Column(modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(MaterialTheme.colorScheme.background)) {
                HeaderView(title= "用户使用协议", backAction = {
                    formType = FormType()
                })
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                ) {
                    UserPolicyView()
                }
            }
        }
        else{
            Column() {
                Column{
                    Text("你好", color=MaterialTheme.colorScheme.primary, fontSize = 22.sp, lineHeight = 35.sp)
                    Text("很高兴看到你下载极简记工时，为了保护您的隐私和个人信息，在你使用APP前请认真阅读以下协议:", color = MaterialTheme.colorScheme.primary, lineHeight = 26.sp)
                    Blank(size = 5.dp)
                    Text("用户使用协议", color = ThemeColor,  lineHeight = 26.sp, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable{
                        formType = formType.copy(type = Router.UserPolicy.route)
                    })
                    Blank(2.dp)
                    Text("App隐私政策", color = ThemeColor, lineHeight = 26.sp, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable{
                        formType = formType.copy(type = Router.PrivatePolicy.route)
                    })
                    Blank()
                    Text("如果你点击下方的「同意」按钮表示你愿意遵循以上协议，最后祝你使用愉快！", color = MaterialTheme.colorScheme.primary, lineHeight = 26.sp)
                }
                Blank()
                OkAndCancelButtonGroup(okLabel = "同意", cancelLabel="不同意", cancelAction = {
                    System.exit(1)
                }, okAction = {
                    okAction()
                })
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(label: String = "") {

    val strokeWidth = 2.dp
    var appModel = hiltViewModel<AppViewModel>()

    if(appModel.baseRepository.loading){
        AlertDialog(onDismissRequest = {

        }, modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .width(100.dp)) {
            Column(modifier= Modifier
                .width(100.dp)
                .height(100.dp)
                .background(colorScheme.surface), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp),
                    color = ThemeColor,
                    strokeWidth = strokeWidth
                )
                if(label != ""){
                    Text(label, color=MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekChooseDialog(totalWeeks: Int, fixWeeks: List<Int>, onChange: (List<Int>) -> Unit, dismissAction: () -> Unit){

    var weekOptions = SelectUtil.initWithUnit("", "",1, totalWeeks)
    var choosedWeeks = remember {
        mutableStateListOf<String>()
    }

    var inited by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit){
        if(!inited){
            choosedWeeks.addAll(fixWeeks.map{it.toString()})
            inited = true
        }
    }

//    if(choosedWeeks.isEmpty()){
//
//    }

    fun make(type: Int){
        if(type == 0){
            choosedWeeks.clear()
            return
        }
        var list = mutableListOf<String>()
        for (i in 1..totalWeeks) {
            val si = i.toString()
            if(type == 3){
                list.add(si)
            }
            else if(type == 2){
                if(i % 2 == 0){
                    list.add(si)
                }
            }
            else if(type == 1){
                if(i % 2 == 1){
                    list.add(si)
                }
            }
            else if(type == -1){
                if(!choosedWeeks.contains(si)){
                    list.add(si)
                }
            }
        }
        choosedWeeks.clear()
        choosedWeeks.addAll(list)
    }


    AlertDialog(onDismissRequest = {
        dismissAction()
    }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
        Column(modifier= Modifier
            .padding(0.dp)
            .background(color = colorScheme.surfaceVariant)
            .clip(
                RoundedCornerShape(20.dp)
            ), horizontalAlignment = Alignment.CenterHorizontally) {

            Row(modifier=Modifier.fillMaxWidth()){
                Text("选择周数", modifier = Modifier.padding(10.dp))
                Text(WeekTool.getWeekInfo(choosedWeeks.map{it.toInt()}, ""), modifier = Modifier.padding(10.dp), fontSize = 12.sp)
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(6) ,
            ) {
                items(count = weekOptions.size) {
                    val value = weekOptions[it].value


                    Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.Center){
                        Text(weekOptions[it].label, modifier = Modifier
                            .radius(16.dp)
                            .clickable {
                                if (choosedWeeks.contains(value)) {
                                    choosedWeeks.remove(value)
                                } else {
                                    choosedWeeks.add(value)
                                }
                            }
                            .background(color = if (choosedWeeks.contains(value)) ThemeColor else MaterialTheme.colorScheme.background)
                            .width(32.dp)
                            .height(32.dp)
                            .wrapContentSize()
                            ,
                            lineHeight = 13.sp,
                            fontSize = 13.sp,
                            color=if(choosedWeeks.contains(value)) Color.White else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Row(modifier = Modifier.padding(vertical = 10.dp)){

                GrayLinkText("全部"){
                    make(3)
                }
                Blank()
                GrayLinkText("单周"){
                    make(1)
                }
                Blank()
                GrayLinkText("双周"){
                    make(2)
                }
                Blank()
                GrayLinkText("清空"){
                    make(0)
                }
                Blank()
                GrayLinkText("反选"){
                    make(-1)
                }

            }

            BottomConfirmButtonGroup(onDismiss = {
                dismissAction()
            }, onConfirm = {
                onChange(choosedWeeks.map{it.toInt()}.sorted())
            })
        }

    }
}

@Composable
fun SortDialogView(title: String,  options: List<SelectDO>, renderAction:@Composable ((String) -> Unit)? = null, onChange: (List<String>) -> Unit, onDismiss: () -> Unit) {
    var newList by remember {
        mutableStateOf(options)
    }

    BottomDialogView(title = title, tip="先长按列表项，然后拖动即可排序", height = 500.dp,  onDismiss = {
        onDismiss()
    }) {
        Column(modifier = Modifier.height(420.dp)) {
            DragNDropItemsList(newList, renderAction){
                newList = it
            }
        }
        OkAndCancelButtonGroup(padding= 10.dp, okAction = {
            onChange(newList.map{it.value})
        }) {
            onDismiss()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomDialogView(title: String, lazy: Boolean = false, tip: String = "", rightTool: @Composable (() -> Unit)? = null, cancelable:Boolean = true, height: Dp = 500.dp, onDismiss: () -> Unit, content:@Composable () -> Unit){
    val configuration = LocalConfiguration.current

    AlertDialog(onDismissRequest = {
        onDismiss()
    }, properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
    ) {
        Box(modifier= Modifier
            .fillMaxSize()
            .tap {
                if (cancelable) {
                    onDismiss()
                }
            }, contentAlignment = Alignment.BottomEnd){
            Column(modifier= Modifier
                .tap {

                }
                .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                .background(colorScheme.surface)
            ) {
                if(title == "" || lazy){
                    if(title != ""){
                        DialogTitleView(title = title, tip=tip, rightTool=rightTool)
                    }
                    Column(
                        modifier = Modifier
                            .padding(20.dp, 0.dp)
                            .height(ifv(height == 0.dp, configuration.screenHeightDp.dp, height))
                    ) {
                        content()
                    }
                }
                else{
                    DialogTitleView(title = title, tip=tip, rightTool=rightTool)
                    Column(
                        modifier = Modifier
                            .padding(20.dp, 0.dp)
                            .height(ifv(height == 0.dp, configuration.screenHeightDp.dp, height))
                            .verticalScroll(rememberScrollState())
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
