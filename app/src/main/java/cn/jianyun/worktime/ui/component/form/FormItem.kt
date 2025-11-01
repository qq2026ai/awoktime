package cn.jianyun.worktime.ui.component.form

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import cn.jianyun.worktime.ui.android.lunar.ChineseCalendar
import cn.jianyun.worktime.ui.android.lunar.GregorianLunarCalendarView
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.ClipboardUtil
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.SelectUtil
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.focusColor
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.isValidColor
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.util.readAssetFile
import cn.jianyun.worktime.util.stringify
import cn.jianyun.worktime.util.test
import cn.jianyun.worktime.util.toAndroid
import cn.jianyun.worktime.util.toIntData
import cn.jianyun.worktime.vm.AppViewModel
import cn.jianyun.worktime.vm.ColorViewModel
import cn.jianyun.worktime.ui.component.form.picker.FVerticalWheelPicker
import cn.jianyun.worktime.ui.component.form.picker.FWheelPickerFocusVertical
import cn.jianyun.worktime.ui.component.form.picker.FWheelPickerState
import cn.jianyun.worktime.ui.component.form.picker.rememberFWheelPickerState
import cn.jianyun.worktime.ui.component.model.kt.ColorModel
import cn.jianyun.worktime.ui.component.nav.CircleBoxView
import cn.jianyun.worktime.ui.component.nav.DialogTitleView
import cn.jianyun.worktime.ui.component.nav.FlowTagView
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import com.alibaba.fastjson2.JSON
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.round





@Composable
fun LabelItemView(label: String, required: Boolean = false){
    Row{
        Text(label, color=MaterialTheme.colorScheme.primary, fontSize = 15.sp)
        if(required){
            Text("*", color= ThemeColor)
        }
    }
}


@Composable
fun ValueItemView(value: String, multiLine: Boolean = false,password: Boolean=false, readonly: Boolean = false, unit:String = "", onClick: () -> Unit){

    val defaultValue = value == "请输入" || value == "选填" || value == "请选择" || value == "无" || readonly
    var showValue = value
    var fontSize = 16.sp
    if(value.count() > 30){
        showValue = value.substring(0, 12) + "..."
        fontSize = 15.sp
    }
    else{
        fontSize = 15.sp
    }
    if(!defaultValue && unit != ""){
        showValue += unit
    }

    if(password && value != "请输入"){
        showValue = "******"
    }

    Text(showValue, modifier= Modifier
        .clip(RoundedCornerShape(4.dp))
        .clickable(enabled = !readonly) {
            onClick()
        }
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 8.dp, vertical = 1.dp)
        .widthIn(min = 30.dp)
        ,
        fontSize = fontSize,
        color=ifv(defaultValue || readonly, Color.Gray, ThemeColor),  maxLines=ifv(multiLine, 3, 1), textAlign = TextAlign.Center)
}



@Composable
fun Value2ItemView(value: String, gray: Boolean, multiLine: Boolean = false, readonly: Boolean=false, unit: String="", onClick: () -> Unit){

    val defaultValue = value == "请输入" || value == "请选择" || readonly  || gray
    var showValue = value
    if(value.count() > 20){
        showValue = value.substring(0, 12) + "..."
    }
    if(!defaultValue && unit != ""){
        showValue += unit
    }



    Text(showValue, modifier= Modifier
        .clip(RoundedCornerShape(4.dp))
        .clickable(enabled = !readonly) {
            onClick()
        }
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 8.dp, vertical = 1.dp)
        .widthIn(min = 30.dp)
        , color=ifv(defaultValue, Color.Gray, ThemeColor),  maxLines=ifv(multiLine, 3, 1), textAlign = TextAlign.Center)
}



@Composable
fun ColorValueItemView(value: String, onClick: () -> Unit){
    Text("", modifier= Modifier
        .clip(RoundedCornerShape(4.dp))
        .clickable {
            onClick()
        }
        .background(color = value.color())
        .padding(horizontal = 8.dp, vertical = 1.dp)
        .widthIn(min = 30.dp)
        , color=MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
}

@Composable
fun BottomConfirmButtonGroup(onDismiss: () -> Unit, onConfirm: () -> Unit){
    Row(modifier= Modifier
        .fillMaxWidth()
        .padding(20.dp), horizontalArrangement = Arrangement.End) {
        Text(text="取消", modifier = Modifier
            .clickable {
                onDismiss()
            }
            .padding(horizontal = 10.dp), color= MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium)

        Text(text="确定", modifier= Modifier
            .clickable {
                onConfirm()
            }
            .padding(horizontal = 10.dp), color= ThemeColor, fontWeight = FontWeight.Medium)
    }
}



@Composable
fun SearchInputView(placeholder:String,defaultValue: String = "", height: Dp=50.dp, radius: Dp = 12.dp, fontSize: TextUnit = 15.sp, vpadding: Dp = 10.dp, background: Color=MaterialTheme.colorScheme.surface, onValueChange: (String) -> Unit) {
    var value by remember {
        mutableStateOf(defaultValue)
    }

    Box(contentAlignment = Alignment.CenterEnd) {
        Row(modifier = Modifier
            .padding(horizontal = 0.dp, vertical = vpadding)
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(radius)
            )
            .background(background)
            .height(height),
            verticalAlignment = Alignment.CenterVertically
        ){
            BasicTextField(value = value,
                onValueChange = {
                    value = it
                    onValueChange(it)
                },decorationBox = {innerTextField ->
                    if(value.isBlank()){
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()
                            Text(placeholder, fontSize = fontSize, color=Color.Gray)
                        }
                    }
                    else{
                        innerTextField()
                    }
                }
                ,modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .background(background)
                ,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 20.sp
                ), singleLine = true,
                cursorBrush = Brush.verticalGradient(colors = listOf(Color(0xFF20A162), Color(0xFF20A162)))
            )
        }
        IconView(IconFont.search2, color=Color.Gray, modifier=Modifier.offset(x=-10.dp))
    }
}

@Composable
fun InputView(placeholder:String, align: TextAlign = TextAlign.Left, password:Boolean = false, height:Dp=50.dp, value: String = "",fontSize: TextUnit=15.sp, background: Color = MaterialTheme.colorScheme.surface, onValueChange: (String) -> Unit) {
    var inputValue by remember {
        mutableStateOf("")
    }

    LaunchedEffect(value){
        inputValue = value
    }

    Row(modifier = Modifier
        .padding(horizontal = 0.dp, vertical = 5.dp)
        .fillMaxWidth()
        .clip(
            RoundedCornerShape(12.dp)
        )
        .background(background)
        .height(height),
        verticalAlignment = Alignment.CenterVertically
    ){

        BasicTextField(value = inputValue,
            onValueChange = {
                inputValue = it
                onValueChange(it)
            },decorationBox = {innerTextField ->
                if(inputValue.isBlank()){
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(),
                        contentAlignment = ifv(align == TextAlign.Left, Alignment.CenterStart, Alignment.Center)
                    ) {
                        innerTextField()
                        Text(placeholder, fontSize = fontSize, color=Color.Gray)
                    }
                }
                else{
                    innerTextField()
                }
            }
            ,modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .background(background)
            ,
            visualTransformation = ifv(password, PasswordVisualTransformation(), VisualTransformation.None),
            keyboardOptions = KeyboardOptions(keyboardType = ifv(password, KeyboardType.Password, KeyboardType.Text)),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                textAlign = align,
                lineHeight = fontSize,
                fontSize = fontSize
            ), singleLine = true,

            cursorBrush = Brush.verticalGradient(colors = listOf(Color(0xFF20A162), Color(0xFF20A162)))
        )
    }
}


@Composable
fun TextAreaView(placeholder:String,height:Dp=120.dp,  value: String = "", background: Color = MaterialTheme.colorScheme.surface, onValueChange: (String) -> Unit) {
    var inputValue by remember {
        mutableStateOf(value)
    }
    BasicTextField(value = inputValue,
        onValueChange = {
            inputValue = it
            onValueChange(it)
        },decorationBox = {innerTextField ->
            if(inputValue.isBlank()){
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopStart
                ) {
                    innerTextField()
                    Text(placeholder,modifier=Modifier.offset(y=-3.dp), fontSize = 14f.sp, color=Color.Gray)
                }
            }
            else{
                innerTextField()
            }
        }
        ,modifier = Modifier
            .radius(6.dp)
            .background(background)
            .padding(10.dp)
            .fillMaxWidth()
            .heightIn(min = height)
            .background(background)
        ,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            lineHeight = 20.sp
        ), singleLine = false,

        cursorBrush = Brush.verticalGradient(colors = listOf(Color(0xFF20A162), Color(0xFF20A162)))
    )
}

@Composable
fun InnerInputItemView(label: String,placeholder: String="", readonly: Boolean = false, password: Boolean = false,  required: Boolean= false, value: String,  onValueChange: (String) -> Unit) {
    Column(modifier= Modifier
        .border(1.dp, Color.Gray.copy(0.3f), RoundedCornerShape(12.dp))
        .padding(10.dp, 3.dp)) {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment =  Alignment.CenterVertically ){
            LabelItemView(label, required)
            Blank()
            InputView(placeholder = ifv(placeholder == "", "请输入" + label , placeholder), value=value,password=password, height=30.dp, align= TextAlign.Left, onValueChange = {
                onValueChange(it)
            })
        }
    }

}


@Composable
fun PasswordTextView(value: String, modifier: Modifier) {

    var shown by remember { mutableStateOf(false) }

    Text(ifv(shown, value, "******"), modifier=modifier.tap {
        shown = !shown
    })

}


@Composable
fun InputItemView(label: String,readonly: Boolean = false, password: Boolean = false, single: Boolean = false, tip: String = "", required: Boolean= false, value: String, multiLine: Boolean = false, options: List<String> = listOf(), onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    Column {
        if(single){
            Value2ItemView(value = ifv(value, label), gray= value == "", readonly=readonly, multiLine=false){
                if(!readonly){
                    openDialog = true
                }
            }
        }
        else{
            Row(modifier= Modifier
                .fillMaxWidth()
                .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment =  Alignment.CenterVertically ){
                LabelItemView(label, required)
                Blank()
                ValueItemView(value = ifv(value, ifv(!readonly,"请输入", "")), readonly=readonly, password=password, multiLine=false){
                    if(!readonly){
                        openDialog = true
                    }
                }
            }
        }

        if(openDialog){
            ShowInputDialog(value = value, tip=tip, password=password, multiLine = multiLine,options=options, keyboardType = ifv(password, KeyboardType.Password, KeyboardType.Text), onValueChange = {
                openDialog = false
                onValueChange(it)
                true
            }, onDismiss = {
                openDialog = false
            })
        }
    }
}


@Composable
fun SegmentItemView(label: String, width:Dp = 70.dp, readonly: Boolean = false, value: String,  options: List<SelectDO> , onValueChange: (String) -> Unit) {
    Row(modifier= Modifier
        .fillMaxWidth()
        .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment =  Alignment.CenterVertically ){
        LabelItemView(label, false)
        Blank()

        if(readonly){
            Text(SelectUtil.getLabel(options, value), color= Color.Gray)
        }
        else{
            SegmentPickerView(value=value, width=width, options = options, onChange = {
                onValueChange(it)
            })
        }

    }
}





@Composable
fun ShownItemView(label: String, value: String, readonly: Boolean = true){
    Row(modifier= Modifier
        .fillMaxWidth()
        .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment =  Alignment.CenterVertically ){
        LabelItemView(label)
        Blank()
        ValueItemView(value = value, readonly=readonly,  multiLine=false){

        }
    }
}


@Composable
fun InputNumberView(label: String,readonly: Boolean = false, single: Boolean = false, tip: String = "",decimal:Boolean = false,unit:String = "", required: Boolean= false, value: String, multiLine: Boolean = false,minValue: Int = -1, maxValue: Int = -1, options: List<String> = listOf(), onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    Column {
        if(single){
            Value2ItemView(value = ifv(value, label), gray= value == "", readonly=readonly, unit=unit, multiLine=false){
                if(!readonly){
                    openDialog = true
                }
            }
        }
        else{
            Row(modifier= Modifier
                .fillMaxWidth()
                .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment =  Alignment.CenterVertically ){
                LabelItemView(label, required)
                Blank()
                ValueItemView(value = ifv(value, "请输入"),readonly=readonly,  unit=unit, multiLine=false){
                    if(!readonly){
                        openDialog = true
                    }
                }
            }
        }


        if(openDialog){
            ShowInputDialog(value = value, tip=tip,minValue=minValue, maxValue=maxValue, unit=unit, keyboardType = ifv(decimal, KeyboardType.Decimal, KeyboardType.Number), multiLine = multiLine,options=options,  onValueChange = {
                if(it.toFloatOrNull() == null){
                    false
                }
                else{
                    openDialog = false
                    onValueChange(it.trim())
                    true
                }

            }, onDismiss = {
                openDialog = false
            })
        }
    }

}


@Composable
fun SelfInputItemView(label: String,required: Boolean = false, value: String, onValueChange: (String) -> Unit, otherText: String = "", otherAction: ()-> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    var defaultValue by remember { mutableStateOf(value) }
    val context = LocalContext.current

    Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment =  Alignment.CenterVertically ){
            LabelItemView(label, required)
            Blank()
            Row {
                ValueItemView(value = ifv(value, "请输入"), multiLine=false){
                    openDialog = true
                }
                if(otherText != ""){
                    Blank(5.dp)
                    ValueItemView(value = otherText, multiLine=false){
                        otherAction()
                    }
                }
            }
        }

        if(openDialog){
            ShowInputDialog(value = value, multiLine = false,  onValueChange = {
                openDialog = false
                onValueChange(it)
                true
            }, onDismiss = {
                openDialog = false
            })
        }

    }

}


@Composable
fun ExtraInputItemView(label: String, required: Boolean = false, value: String, options: List<String> = listOf(), onValueChange: (String) -> Unit, extraView: @Composable () -> Unit) {
    var openDialog by remember { mutableStateOf(false) }

    Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment =  Alignment.CenterVertically ){
            LabelItemView(label, required)
            Blank()
            VerticalRow {
                ValueItemView(value = ifv(value, "请输入"), multiLine=false){
                    openDialog = true
                }
                extraView()
            }
        }

        if(openDialog){
            ShowInputDialog(value = value, multiLine = false, options=options,  onValueChange = {
                openDialog = false
                onValueChange(it)
                true
            }, onDismiss = {
                openDialog = false
            })
        }

    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowInputDialog(value: String, tip: String = "", password: Boolean=false,minValue: Int = -1, maxValue: Int = -1, unit:String = "", keyboardType: KeyboardType=KeyboardType.Text, height: Dp=80.dp,options: List<String> = listOf(),  multiLine: Boolean,selfView: @Composable (() -> Unit)? = null, onValueChange: (String) -> Boolean, onDismiss: () -> Unit) {

    var openDialog by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    var textFieldLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var msg by remember{ mutableStateOf("") }

    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = value, selection = when {
                    value.isEmpty() -> TextRange.Zero
                    else -> TextRange(value.length, value.length)
                }
            )
        )
    }

    fun makeOk(){

        val text = textFieldValueState.text
        if (keyboardType == KeyboardType.Decimal) {
            val dd = text.toFloatOrNull()
            if (dd == null) {
                msg = "格式非法,请正确填写"
                return
            }
            if(minValue >= 0){
                if(dd < minValue){
                    msg = "最小值是${minValue}"
                    return
                }
            }
            if(maxValue >= 0){
                if(dd > maxValue){
                    msg = "最大值是${maxValue}"
                    return
                }
            }
        } else if (keyboardType == KeyboardType.Number) {
            val dd = text.toIntOrNull()
            if (dd == null) {
                msg = "格式非法,请正确填写"
                return
            }
            if(minValue >= 0){
                if(dd < minValue){
                    msg = "最小值是${minValue}"
                    return
                }
            }
            if(maxValue >= 0){
                if(dd > maxValue){
                    msg = "最大值是${maxValue}"
                    return
                }
            }
        }
        msg = ""
        if (onValueChange(text)) {
            openDialog = false
        } else {
            mlog("禁止隐藏dialog")
        }
    }

    return Column {
        if(openDialog){
            AlertDialog(onDismissRequest = {
//                openDialog = false
//                onDismiss()
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    DialogTitleView(title = "请填写")

                    Row(modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .height(ifv(multiLine, height, 40.dp))
                        .padding(vertical = 5.dp),
                        verticalAlignment = ifv(multiLine,Alignment.Top, Alignment.CenterVertically)
                    ) {
                        BasicTextField(value = textFieldValueState, onValueChange = { newTextFieldValueState ->
                            textFieldValueState = newTextFieldValueState
                        },
                            keyboardActions = KeyboardActions(onDone = {
                                makeOk()
                            }),
                            visualTransformation = ifv(password, PasswordVisualTransformation(), VisualTransformation.None),
                            keyboardOptions= KeyboardOptions(keyboardType = keyboardType),
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .onGloballyPositioned {
                                    if (!textFieldLoaded) {
                                        focusRequester.requestFocus() // IMPORTANT
                                        textFieldLoaded = true // stop cyclic recompositions
                                    }
                                },

                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 20.sp
                            ), singleLine = !multiLine,
                            cursorBrush = Brush.verticalGradient(colors = listOf(Color(0xFF20A162), Color(0xFF20A162)))
                        )
                    }

                    if(msg != ""){
                        Text(msg, fontSize = 13.sp, color= DeleteColor)
                    }

                    if(!options.isEmpty()){
                        Blank()
                        FlowTagView(options, hpadding = 20.dp, unit){
                            textFieldValueState = TextFieldValue(
                                text = it,
                                selection = TextRange(it.length, it.length)
                            )
                        }
                    }

                    if(tip != ""){
                        LeadingHintView(tip,left=20.dp, fontSize = 12.sp)
                    }

                    if(selfView != null){
                        selfView()
                    }

                    Row(modifier= Modifier
                        .fillMaxWidth()
                        .padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {

                        Row{
                            if(textFieldValueState.text != ""){
                                Text(text="清空", fontSize = 12.sp, color= MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium, modifier = Modifier.clickable {
                                    textFieldValueState = TextFieldValue(text = "", selection = TextRange(0,0))
                                }.padding(10.dp))
                            }
                        }

                        Row(){
                            Text(text="取消", modifier = Modifier
                                .clickable {
                                    openDialog = false
                                    onDismiss()
                                }
                                .padding(horizontal = 10.dp), color= MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium)

                            Text(text="确定", modifier= Modifier
                                .clickable {
                                    makeOk()
                                }
                                .padding(horizontal = 10.dp), color= ThemeColor, fontWeight = FontWeight.Medium)
                        }
                    }


                }
            }
        }
    }



}



@Composable
fun SwitchItemView(label: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(modifier= Modifier
        .fillMaxWidth()
        .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        LabelItemView(label)
        SingleSwitch(value = value, onValueChange = onValueChange)
    }
}

@Composable
fun SingleSwitch(value: Boolean, onValueChange: (Boolean) -> Unit) {
    Switch(checked= value, onCheckedChange=onValueChange,
        modifier = Modifier
            .scale(0.8f)
            .offset(x = 6.dp),
        colors= SwitchDefaults.colors(
            checkedThumbColor= Color.White,
            checkedTrackColor = ThemeColor,
            checkedBorderColor = ThemeColor,
            uncheckedThumbColor =  Color.White,
            uncheckedTrackColor = MaterialTheme.colorScheme.tertiary,
            uncheckedBorderColor = MaterialTheme.colorScheme.tertiary
        ))
}



@Composable
fun AdderView(label: String, value: Int, baseValue: Int = 0, showValue: Boolean = true, step: Int = 1, minValue: Int = 0, maxValue: Int = 100, onValueChange: (Int) -> Unit) {

    Row(modifier= Modifier
        .fillMaxWidth()
        .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        LabelItemView(label)

        Row{
            
            CircleBoxView(size = 20.dp, background = MaterialTheme.colorScheme.surface, action= {
                val newValue = value - step
                if(newValue >= minValue){
                    onValueChange(newValue)
                }
            }) {
                IconView(icon = IconFont.sub)
            }

            if(showValue){
                Text("${baseValue + value}", fontSize = 13.sp, modifier=Modifier.padding(horizontal = 5.dp))
            }
            else{
                Blank()
            }

            CircleBoxView(size = 20.dp, background = MaterialTheme.colorScheme.surface, action= {
                val newValue = value + step
                if(newValue <= maxValue){
                    onValueChange(newValue)
                }
            }) {
                IconView(icon = IconFont.add)
            }

        }
    }
}




@Composable
fun FloatAdderView(label: String, value: Float, baseValue: Int = 0, showValue: Boolean = true, step: Float = 0.05f, minValue: Float = 0f, maxValue: Float = 1f, onValueChange: (Float) -> Unit) {

    Row(modifier= Modifier
        .fillMaxWidth()
        .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        LabelItemView(label)

        Row{

            CircleBoxView(size = 20.dp, background = MaterialTheme.colorScheme.surface, action= {
                val newValue = value - step
                if(newValue >= minValue){
                    onValueChange(newValue)
                }
            }) {
                IconView(icon = IconFont.sub)
            }

            if(showValue){
                Text("${getNoMoreThanTwoDigits(baseValue + value)}", fontSize = 13.sp, modifier=Modifier.padding(horizontal = 5.dp))
            }
            else{
                Blank()
            }

            CircleBoxView(size = 20.dp, background = MaterialTheme.colorScheme.surface, action= {
                val newValue = round((value + step) * 100) / 100
                if(newValue < minValue){
                    onValueChange(minValue)
                }
                else if(newValue <= maxValue){
                    onValueChange(newValue)
                }
            }) {
                IconView(icon = IconFont.add)
            }

        }
    }
}

fun getNoMoreThanTwoDigits(number: Float): String {
    val format = DecimalFormat("0.##")
    //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。
    format.roundingMode = RoundingMode.HALF_DOWN
    return format.format(number)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerItemView(label: String, value: String, readonly: Boolean=false, single: Boolean = false, onValueChange: (String) -> Unit, selfValue: @Composable ((String) -> Unit)? = null) {
    var openDialog by remember { mutableStateOf(false) }
    var showValue = value
    if(value == ""){
        showValue = MyDateTool.toDateString(Date())
    }
    val mdate = MyDateTool.parseDateString(showValue)
    //需要进一步转化为农历
    val focusManager = LocalFocusManager.current
    val themeColor = ThemeColor.toAndroid()
    val dividerColor = MaterialTheme.colorScheme.surfaceVariant.toAndroid()

    return Column {

        if(single){
            if(selfValue != null){
                Row(modifier= Modifier
                    .tap {
                        openDialog = true
                    }
                    .fillMaxSize()){
                    selfValue(ifv(value == "", label, value))
                }
            }
            else{
                Value2ItemView(value = ifv(value == "", label, value),readonly=readonly, gray = value == ""){
                    openDialog = true
                }
            }
        }
        else{
            Row(modifier= Modifier
                .fillMaxWidth()
                .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                LabelItemView(label)
                ValueItemView(value = ifv(value == "", "请选择", value),readonly=readonly){
                    openDialog = true
                }
            }
        }

        if(openDialog){

            var calendar = Calendar.getInstance()
            calendar.time = mdate

            var state by remember {
                mutableStateOf(calendar)
            }

            focusManager.clearFocus(true)
            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    DialogTitleView(title = "选择日期")
                    AndroidView(factory = { ctx ->
                        GregorianLunarCalendarView(ctx)
                    }, modifier= Modifier
                        .height(200.dp)
                        .fillMaxWidth(), update = {
                        it.init(state, true)
                        it.setThemeColor(themeColor)
                        it.setDividerColor(dividerColor)
                        it.setOnDateChangedListener {
                            state = it.calendar
                        }
                    })

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                    }, onConfirm = {
                        openDialog = false
                        onValueChange(MyDateTool.toDateString(state.time))
                    })
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunarDatePickerItemView(label: String, value: String, onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    var showValue = value
    if(value == ""){
        showValue = MyDateTool.toDateString(Date())
    }
    val mdate = MyDateTool.parseDateString(showValue)
    //需要进一步转化为农历
    val lunarDate = ChineseCalendar(mdate).chineseDateString

    var calendar = Calendar.getInstance()
    calendar.time = mdate

    val themeColor = ThemeColor.toAndroid()
    val dividerColor = MaterialTheme.colorScheme.surfaceVariant.toAndroid()

    var state by remember {
        mutableStateOf(ChineseCalendar(mdate))
    }

    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label)
            ValueItemView(value = ifv(value == "", "请选择", lunarDate)){
                openDialog = true
            }
        }

        if(openDialog){
                    AlertDialog(onDismissRequest = {
                        openDialog = false
                    }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                        Column(modifier= Modifier
                            .background(color = MaterialTheme.colorScheme.surface)
                            .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                            DialogTitleView(title = "选择日期")
                            AndroidView(factory = { ctx ->
                                GregorianLunarCalendarView(ctx).apply {
                                    this.init(state, false)
                                    this.setThemeColor(themeColor)
                                    this.setDividerColor(dividerColor)
                                    this.setOnDateChangedListener {
                                        state = it.chineseCalendar
                                    }
                                }
                            }, modifier= Modifier
                                .height(200.dp)
                                .fillMaxWidth())

                            BottomConfirmButtonGroup(onDismiss = {
                                openDialog = false
                            }, onConfirm = {
                                openDialog = false
                                mlog(MyDateTool.toDateString(state.time))
                                mlog(state.simpleGregorianDateString)
                                onValueChange(MyDateTool.toDateString(state.time))
                            })
                        }

                    }

                }
    }
}



//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TimePickerItemView(label: String, value: String, onValueChange: (String) -> Unit) {
//    var openDialog by remember { mutableStateOf(false) }
//
//    val calendar = Calendar.getInstance()
//    calendar.set(1990, 0, 22)
//
//    var state = rememberTimePickerState(initialHour = 20, initialMinute = 0, is24Hour = true)
//
//    return Column {
//        Row(modifier= Modifier
//            .fillMaxWidth()
//            .height(50.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically){
//
//            LabelItemView(label)
//            ValueItemView(value = value){
//                openDialog = true
//            }
//
//            //, maxLines = 1, colors= TextFieldDefaults.textFieldColors(textColor=MaterialTheme.colorScheme.primary, containerColor = MaterialTheme.colorScheme.background)
//        }
//
//        if(openDialog){
//
//           AlertDialog(onDismissRequest = {
//                openDialog = false
//            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
//                Column(modifier= Modifier
//                    .padding(0.dp)
//                    .background(color = MaterialTheme.colorScheme.surface)
//                    .clip(
//                        RoundedCornerShape(20.dp)
//                    ), horizontalAlignment = Alignment.CenterHorizontally) {
//
//                    Row(modifier=Modifier.fillMaxWidth()){
//                        Text("选择时间", modifier = Modifier.padding(20.dp))
//                    }
//
//                    TimePicker(state= state)
//                    BottomConfirmButtonGroup(onDismiss = {
//                        openDialog = false
//                    }, onConfirm = {
//                        openDialog = false
//                        onValueChange("${state.hour}:${state.minute}")
//                    })
//                }
//
//            }
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TimePickerItemView2(label: String, value: String, onValueChange: (String) -> Unit) {
//    var openDialog by remember { mutableStateOf(false) }
//
//    var hours = mutableListOf<String>()
//    for(i in 0 until 24){
//        hours.add(if(i < 10) ("0$i") else "$i")
//    }
//
//    var minutes = mutableListOf<String>()
//    for(i in 0 until 60){
//        minutes.add(if(i < 10) ("0$i") else "$i")
//    }
//
//
//    var hourIdx = 0
//    var minuteIdx = 0
//    val vv = value.split(":")
//    if(vv.size == 2){
//        hourIdx = hours.indexOf(vv[0])
//        minuteIdx = minutes.indexOf(vv[1])
//    }
//
//
//    return Column {
//        Row(modifier= Modifier
//            .fillMaxWidth()
//            .height(50.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically){
//            LabelItemView(label)
//            ValueItemView(value = ifv(value == "", "请选择", value)){
//                openDialog = true
//            }
//
//            //, maxLines = 1, colors= TextFieldDefaults.textFieldColors(textColor=MaterialTheme.colorScheme.primary, containerColor = MaterialTheme.colorScheme.background)
//        }
//
//        var hourState = rememberPickerState()
//        var minuteState = rememberPickerState()
//        if(openDialog){
//
//            AlertDialog(onDismissRequest = {
//                openDialog = false
//            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
//                Column(modifier= Modifier
//                    .padding(0.dp)
//                    .background(color = MaterialTheme.colorScheme.surface)
//                    .clip(
//                        RoundedCornerShape(20.dp)
//                    ), horizontalAlignment = Alignment.CenterHorizontally) {
//
//                    Row(modifier=Modifier.fillMaxWidth()){
//                        Text("选择时间", modifier = Modifier.padding(20.dp))
//                    }
//
//                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
//
////                       WheelTimePicker()
//
//                        Picker(
//                            items= hours,
//                            state= hourState,
//                            modifier = Modifier.weight(0.5f),
//                            startIndex = hourIdx,
//                            textModifier = Modifier.padding(vertical = 10.dp),
//                            visibleItemsCount = 5,
//                            textStyle = TextStyle(fontSize = 15.sp)
//                        )
//                        Text(":")
//                        Picker(items= minutes,
//                            state= minuteState,
//                            modifier = Modifier.weight(0.5f),
//                            startIndex = minuteIdx,
//                            textModifier = Modifier.padding(vertical = 10.dp),
//                            visibleItemsCount = 5,
//                            textStyle = TextStyle(fontSize = 15.sp)
//                            )
//                    }
//
//                    BottomConfirmButtonGroup(onDismiss = {
//                        openDialog = false
//                    }, onConfirm = {
//                        openDialog = false
//                        onValueChange("${hourState.selectedItem}:${minuteState.selectedItem}")
//                    })
//                }
//
//            }
//        }
//    }
//}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerItemView3(label: String, allowEmpty:Boolean = false, minuteStep: Int = 1,  placeholder: String = "请选择", value: String, onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    var hours = mutableListOf<String>()

//    var type by remember{ mutableStateOf("5") }

    for(i in 0 until 25){
        hours.add(if(i < 10) ("0$i") else "$i")
    }

    var minutes = mutableListOf<String>()
    for (i in 0 until 60 step minuteStep) {
        minutes.add(if (i < 10) ("0$i") else "$i")
    }

    var hourIdx = 0
    var minuteIdx = 0
    val vv = value.split(":")
    if(vv.size == 2){
        hourIdx = hours.indexOf(vv[0])
        minuteIdx = minutes.indexOf(vv[1])
    }

    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label)
            ValueItemView(value = ifv(value == "", placeholder, value)){
                openDialog = true
            }
        }

        var hourState = rememberFWheelPickerState(initialIndex = hourIdx)
        var minuteState = rememberFWheelPickerState(initialIndex = minuteIdx)

        if(openDialog){

            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                        Text("选择时间", modifier = Modifier.padding(20.dp))


                        if(allowEmpty){
                            Text("清空", fontSize = 12.sp, modifier = Modifier.clickable {
                                onValueChange("")
                                openDialog = false
                            }.padding(10.dp))
                        }

                    }

                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        FVerticalWheelPicker(
                            modifier = Modifier.weight(0.5f),
                            // Specified item count.
                            count = hours.size,
                            unfocusedCount = 2,
                            state = hourState,
                            focus = {
                                // Custom divider.
                                FWheelPickerFocusVertical(dividerColor = MaterialTheme.colorScheme.surfaceVariant, dividerSize = 1.dp)
                            }
                        ) { index ->
                            Text(hours.get(index), color= focusColor(flag = index == hourState.currentIndexSnapshot))
                        }
                        Text(":")
                        FVerticalWheelPicker(
                            modifier = Modifier.weight(0.5f),
                            // Specified item count.
                            count = minutes.size,
                            unfocusedCount = 2,
                            state = minuteState,
                            focus = {
                                // Custom divider.
                                FWheelPickerFocusVertical(dividerColor = MaterialTheme.colorScheme.surfaceVariant, dividerSize = 1.dp)
                            }
                        ) { index ->
                            Text(minutes.get(index), color= focusColor(flag = index == minuteState.currentIndexSnapshot))
                        }
                    }

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                    }, onConfirm = {
                        openDialog = false

                        onValueChange("${hours[hourState.currentIndexSnapshot]}:${minutes[minuteState.currentIndexSnapshot]}")
                    })
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePeriodPickerItemView3(label: String,placeholder: String = "请选择", allowEmpty: Boolean= false, minuteStep: Int = 5, value: String, onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }

    var inited by remember { mutableStateOf(false) }
//    var type by remember{ mutableStateOf("5") }
    val vv = value.split(":")
    mlog("inited1....", value)

    var hours = mutableListOf<String>()
    for(i in 0 until 25){
        hours.add("$i")
    }

    var minutes = mutableListOf<String>()
    for(i in 0 until 60 step minuteStep){
        minutes.add("$i")
    }

    var tempMinute by remember { mutableStateOf( "0")}

    var hourIdx = 0
    var minuteIdx = 0
    var showValue = placeholder

    if(vv.size == 2){
        hourIdx = hours.indexOf(vv[0])
        minuteIdx = minutes.indexOf(vv[1])
        tempMinute = vv[1]
        var txt = ifv(hourIdx == 0, "", vv[0] + "小时") + ifv(minuteIdx > 0, vv[1] + "分钟", "")
        showValue = ifv(txt == "", placeholder, txt)
        mlog("reset index", hourIdx, minuteIdx)
    }

    var hourState = rememberFWheelPickerState(initialIndex = hourIdx)
    var minuteState = rememberFWheelPickerState(initialIndex = minuteIdx)
    inited = true
    showValue = ifv(showValue == placeholder && allowEmpty, "无", showValue)

    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label)
            ValueItemView(value = showValue){
                openDialog = true
            }
        }

        if(openDialog){

            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                        Text("选择时长", modifier = Modifier.padding(20.dp))
                    }

                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        FVerticalWheelPicker(
                            modifier = Modifier.weight(0.5f),
                            // Specified item count.
                            count = hours.size,
                            unfocusedCount = 2,
                            state = hourState,
                            focus = {
                                // Custom divider.
                                FWheelPickerFocusVertical(dividerColor = MaterialTheme.colorScheme.surfaceVariant,dividerSize = 1.dp)
                            }
                        ) { index ->
                            Text(hours.get(index), color= focusColor(flag = index == hourState.currentIndexSnapshot))
                        }
                        Text("小时", modifier=Modifier.padding(3.dp), fontSize = 12.sp, color=Color.Gray)
                        FVerticalWheelPicker(
                            modifier = Modifier.weight(0.5f),
                            // Specified item count.
                            count = minutes.size,
                            unfocusedCount = 2,
                            state = minuteState,
                            focus = {
                                // Custom divider.
                                FWheelPickerFocusVertical(dividerColor = MaterialTheme.colorScheme.surfaceVariant, dividerSize = 1.dp)
                            }
                        ) { index ->
                            Text(minutes.get(index), color= focusColor(flag = index == minuteState.currentIndexSnapshot))
                            tempMinute = minutes.get(index)
                        }
                        Text("分钟", modifier=Modifier.padding(3.dp), fontSize = 12.sp, color=Color.Gray)
                    }

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                    }, onConfirm = {
                        openDialog = false
                        onValueChange("${hours[hourState.currentIndexSnapshot]}:${minutes[minuteState.currentIndexSnapshot]}")
                    })
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectItemView(label: String,required: Boolean = false, readonly: Boolean = false, nav:Boolean = false, value: String, onValueChange: (String) -> Unit, options: List<SelectDO>) {
    var openDialog by remember { mutableStateOf(false) }
    var selectState = rememberFWheelPickerState(initialIndex = 0)
    var showValue = "请选择"
    if(value != "" && options.isNotEmpty()){
        showValue = options[SelectUtil.findPosition(options, value)].label
    }

    if(options.isNotEmpty()){
        selectState = FWheelPickerState(SelectUtil.findPosition(options, value))
    }

    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(ifv(nav, 10.dp, 0.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label, required)
            ValueItemView(value = showValue, readonly=readonly) {
                if(!readonly){
                    openDialog = true
                }
            }
        }

        if(openDialog){

            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    DialogTitleView(title = "请选择")

                    FVerticalWheelPicker(
                        modifier = Modifier.height(200.dp),
                        // Specified item count.
                        count = options.size,
                        unfocusedCount = 2,
                        state = selectState,
                        focus = {
                            // Custom divider.
                            FWheelPickerFocusVertical(dividerColor = MaterialTheme.colorScheme.surfaceVariant, dividerSize = 1.dp)
                        }
                    ) { index ->
                        Text(options.get(index).label, color= focusColor(index == selectState.currentIndexSnapshot))
                    }

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                    }, onConfirm = {
                        openDialog = false
                        if(options.size > selectState.currentIndexSnapshot && options.isNotEmpty()){
                            onValueChange(options[selectState.currentIndexSnapshot].value)
                        }
                    })
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetButtonView(label: String, options: List<SelectDO>, onAction: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }


    return Column {

        LinkButton(label, width = 70.dp){
            openDialog = true
        }

        if(openDialog){

            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(5.dp)
                    .background(color = MaterialTheme.colorScheme.background)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    DialogTitleView(title = "请选择一项操作")

                    options.forEach{
                        Row(modifier = Modifier
                            .padding(20.dp, 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                openDialog = false
                                onAction(it.value)
                            }){
                            Text(it.label, color = ThemeColor)
                        }
                    }
                }

            }

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MultiSelectItemView(label: String, columnCount: Int = 1, readonly: Boolean = false, value: String, onValueChange: (String) -> Unit, options: List<SelectDO>, maxCount: Int = 3) {
    var openDialog by remember { mutableStateOf(false) }
    var selectedOptions = remember { mutableStateListOf<String>() }

    val appMode = hiltViewModel<AppViewModel>()
    var showValue = ""

    if(value != ""){
        showValue =  "${value.split("^").filter{SelectUtil.contains(options, it)}.size}条记录"
        selectedOptions.clear()
        selectedOptions.addAll(value.split("^") )
    }

    var fration = 1f
    if(columnCount == 2){
        fration = 0.5f
    }
    else if(columnCount == 3){
        fration = 0.3333f
    }

    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label)
            ValueItemView(value = ifv(showValue == "", "请选择", showValue), readonly = readonly){
                openDialog = true
            }
        }

        if(openDialog){

            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    DialogTitleView(title = "请选择", tip=ifv(maxCount > 0, "最多选择${maxCount}个", ""))
                    Column(modifier= Modifier
                        .height(300.dp)
                        .padding(10.dp, 0.dp)
                        .verticalScroll(rememberScrollState(0), reverseScrolling = false)) {

                        FlowRow(modifier=Modifier.fillMaxWidth()){
                            options.forEach{
                                val k = it.value
                                Row( modifier= Modifier
                                    .fillMaxWidth(fration)
                                    .radius(6.dp)
                                    .height(50.dp)
                                    .clickable {
                                        if (selectedOptions.contains(it.value)) {
                                            selectedOptions.remove(it.value)
                                        } else {
                                            if (selectedOptions.size < maxCount || maxCount <= 0) {
                                                selectedOptions.add(it.value)
                                            }
                                        }
                                    }, horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedOptions.contains(it.value),
                                        onCheckedChange  = {
                                            if(it){
                                                if(selectedOptions.size < maxCount || maxCount <= 0){
                                                    selectedOptions.add(k)
                                                }
                                            }
                                            else{
                                                selectedOptions.remove(k)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = ThemeColor,
                                            checkmarkColor = Color.White
                                        )
                                    )
                                    Text(it.label)
                                }
                            }
                        }
                    }

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                    }, onConfirm = {
                        if(selectedOptions.size > maxCount && maxCount > 0){
                            appMode.toast("最多选择${maxCount}个")
                        }
                        else{
                            openDialog = false
                            val rst =selectedOptions.sortedWith{v1, v2 -> SelectUtil.getDefaultIdx(options, v1) - SelectUtil.getDefaultIdx(options, v2)}.joinToString("^")
                            onValueChange(rst)
                        }
                    })
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SelfSelectItemView(label: String, value: String, onValueChange: (String) -> Unit, options: List<SelectDO>) {
    var openDialog by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(value)}
    var scrollState = rememberScrollState()
    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label)
            ValueItemView(value = value){
                openDialog = true
            }
        }

        if(openDialog){

            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    DialogTitleView(title = "请选择")

                    FlowRow() {

                        options.forEach{
                            Text(it.label, modifier = Modifier
                                .padding(10.dp)
                                .radius(5.dp)
                                .clickable {
                                    selectedOption = it.value
                                }
                                .background(color = if (it.value == selectedOption) ThemeColor else MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .offset(y = -1.dp)
                                ,
                                color=if(it.value == selectedOption) Color.White else MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                    }, onConfirm = {
                        openDialog = false
                        onValueChange(selectedOption)
                    })

                }
            }
        }
    }
}

@Composable
fun ColorSelectItemView(label: String, value: String, onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label)
            ColorValueItemView(value = value){
                openDialog = true
            }
        }

        if(openDialog){
            ShowColorPickerView(value = value, onValueChange = {
                openDialog = false
                onValueChange(it)
            })
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShowColorPickerView(value: String, onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableStateOf(value)}
    var colorViewModel = hiltViewModel<ColorViewModel>()
    var inputColor by remember {
        mutableStateOf("#")
    }

    return Column {
        if(openDialog){

            var colors = readAssetFile(LocalContext.current, "data", "color.json")!!
            val colorList = JSON.parseArray(colors, ColorModel::class.java)
            var collectColorList = colorViewModel.listColor()

            AlertDialog(onDismissRequest = {
                openDialog = false
                onValueChange("")
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    DialogTitleView(title = "请选择${ifv(colorViewModel.defaultColorMode == "collect", "(长按可以取消收藏)", "")}")

                    SegmentPickerView(value = colorViewModel.defaultColorMode, options = SelectUtil.COLOR_MODES) { it
                        colorViewModel.defaultColorMode = it
                    }

                    Blank()

                    if(colorViewModel.defaultColorMode == "chinese"){
                        Column(modifier = Modifier
                            .verticalScroll(rememberScrollState())
                        ) {

                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                // 固定两列
                                columns = GridCells.Fixed(3) ,
                                contentPadding = PaddingValues(10.dp),
                                content = {
                                    items(colorList.size, key = {it}) {
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(6.dp)){
                                            Text(colorList[it].name,
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .clickable {
                                                        selectedOption = colorList[it].color
                                                    }
                                                    .radius(6.dp)
                                                    .background(colorList[it].color.color())
                                                    .fillMaxWidth()
                                                    .height(32.dp)
                                                    .align(Alignment.Center)
                                                    .wrapContentSize()
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }

                    if(colorViewModel.defaultColorMode == "collect"){
                        Column(modifier = Modifier
                            .verticalScroll(rememberScrollState())
                        ) {
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                // 固定两列
                                columns = GridCells.Fixed(3) ,
                                contentPadding = PaddingValues(10.dp),
                                content = {
                                    items(collectColorList.size, key = {it}) {
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(6.dp)){
                                            Text(collectColorList[it],
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .combinedClickable(
                                                        onClick = {
                                                            selectedOption = collectColorList[it]
                                                        },
                                                        onLongClick = {
                                                            colorViewModel.removeCollectColor(collectColorList[it])
                                                            collectColorList = colorViewModel.listColor()
                                                        }
                                                    )
                                                    .radius(6.dp)
                                                    .background(collectColorList[it].color())
                                                    .fillMaxWidth()
                                                    .height(32.dp)
                                                    .align(Alignment.Center)
                                                    .wrapContentSize()
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }

                    if(colorViewModel.defaultColorMode == "self"){
                        ClassicColorPicker(
                            modifier = Modifier
                                .padding(20.dp)
                                .height(200.dp),
                            color= HsvColor.from(selectedOption.color()),
                            showAlphaBar= false,
                            onColorChanged = { color: HsvColor ->
                                selectedOption = color.toColor().stringify()
                            }
                        )
                    }
                    Blank()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(selectedOption, modifier= Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(color = MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 3.dp, vertical = 1.dp), fontSize = 10.sp, lineHeight = 10.sp)

                            Box(modifier= Modifier
                                .padding(top = 2.dp)
                                .width(80.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(selectedOption.color()))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("手动输入", modifier= Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(color = MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 3.dp, vertical = 1.dp), fontSize = 10.sp, lineHeight = 10.sp)

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .padding(top = 2.dp)
                                    .radius(6.dp)
                                    .width(100.dp)
                                    .height(30.dp)
                                    .background(MaterialTheme.colorScheme.background),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(value = inputColor, onValueChange = {
                                    inputColor = it
                                    if(it.length == 7){
                                        if(it.isValidColor()){
                                            selectedOption = it.uppercase()
                                        }
                                    }
                                },modifier = Modifier.padding(horizontal = 10.dp),
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        lineHeight = 30.sp,
                                        textAlign = TextAlign.Center
                                    ), singleLine = true,
                                    cursorBrush = Brush.verticalGradient(colors = listOf(ThemeColor, ThemeColor))
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("收藏颜色", modifier= Modifier
                                .offset(y = 5.dp)
                                .radius(6.dp)
                                .clickable {
                                    val f = colorViewModel.addCollectColor(selectedOption)
                                    if (f) {
                                        colorViewModel.toast("收藏成功")
                                    }
                                }
                                .background(color = MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 3.dp, vertical = 2.dp), fontSize = 14.sp, lineHeight = 14.sp,
                                color= ThemeColor
                            )
                        }

                    }

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                        onValueChange("")
                    }, onConfirm = {
                        openDialog = false
                        onValueChange(selectedOption)
                    })

                }
            }
        }
    }
}



@Composable
fun SegmentPickerView(value: String = "", padding: Dp =0.dp, options: List<SelectDO>, width: Dp = 70.dp, onChange: (String) -> Unit) {

    var current by remember {
        mutableStateOf(value)
    }

    LaunchedEffect(value){
        current = value
        mlog("seg changed", value)
    }

    Row(
        modifier = Modifier
            .padding(padding)
            .radius(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .width((width - 2.dp) * options.size + 4.dp)
            .height(30.dp)
            .padding(2.dp)
    ){
        options.forEach{
            Text(it.label,
                modifier =
                Modifier
                    .radius(6.dp)
                    .clickable {
                        current = it.value
                        onChange(current)
                    }
                    .background(
                        ifv(
                            current == it.value,
                            ThemeColor,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    .width((width - 2.dp))
                    .height(26.dp)
                    .wrapContentSize()
                , lineHeight = 12.sp, color= ifv(current == it.value, Color.White, Color.Gray), fontSize = 12.sp, fontWeight = FontWeight.Medium)

        }

    }

}


@Composable
fun LinkItemView(label: String, onClick: () -> Unit) {

    Row(modifier= Modifier
        .clickable {
            onClick()
        }
        .radius(6.dp)
        .fillMaxWidth()
        .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment =  Alignment.CenterVertically ){
        LabelItemView(label)
        Blank()
    }
    
}


//
//@Composable
//fun SelfCourseTime(label: String, course:CourseModel, onValueChange: (CourseModel) -> Unit) {
//    var openDialog by remember { mutableStateOf(false) }
//
//    Column {
//        Row(modifier= Modifier
//            .fillMaxWidth()
//            .height(50.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment =  Alignment.CenterVertically ){
//            LabelItemView(label)
//            Blank()
//            ValueItemView(value = ifv(course.selfStartTime == "", "无", course.selfStartTime + "-" + course.selfEndTime), multiLine=false){
//                openDialog = true
//            }
//        }
//        if(openDialog){
//
//
//        }
//    }
//}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatConfigItemView(label: String, allowEmpty: Boolean = true,  value: String, onValueChange: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }

    var numIdx = 0
    var unitIdx = 0
    val vv = value.split(":")

    var nums = SelectUtil.initWithUnit("每", "", 1, 60)

    var units = SelectUtil.initValues("天","d", "周","w", "月","m", "年", "y")
    if(allowEmpty){
        nums.add(0, SelectDO("不重复","n"))
        units.add(0, SelectDO("不重复","n"))
    }
    var showValue = ifv(allowEmpty, "不重复", "请选择")

    if(vv.size == 2){
        numIdx = SelectUtil.findPosition(nums, vv[0])
        unitIdx = SelectUtil.findPosition(units, vv[1])
        if(allowEmpty && (numIdx == 0 || unitIdx == 0)){
            showValue = "不重复"
        }
        else{
            showValue = nums[numIdx].label + units[unitIdx].label
        }
    }

    Log.d("picker render", label + ":" +  value)

    return Column {
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            LabelItemView(label)
            ValueItemView(value = showValue){
                openDialog = true
            }
        }

        var hourState = rememberFWheelPickerState(initialIndex = numIdx)
        var minuteState = rememberFWheelPickerState(initialIndex = unitIdx)

        if(openDialog){

            AlertDialog(onDismissRequest = {
                openDialog = false
            }, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                Column(modifier= Modifier
                    .padding(0.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {

                    DialogTitleView(title = "选择重复机制")

                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        FVerticalWheelPicker(
                            modifier = Modifier.weight(0.5f),
                            // Specified item count.
                            count = nums.size,
                            unfocusedCount = 2,
                            state = hourState,
                            focus = {
                                // Custom divider.
                                FWheelPickerFocusVertical(dividerColor = MaterialTheme.colorScheme.surfaceVariant, dividerSize = 1.dp)
                            }
                        ) { index ->
                            Text(nums.get(index).label, color= focusColor(index == hourState.currentIndexSnapshot))
                        }
                        Text(":")

                        FVerticalWheelPicker(
                            modifier = Modifier.weight(0.5f),
                            // Specified item count.
                            count = units.size,
                            unfocusedCount = 2,
                            state = minuteState,
                            focus = {
                                // Custom divider.
                                FWheelPickerFocusVertical(dividerColor = MaterialTheme.colorScheme.surfaceVariant, dividerSize = 1.dp)
                            }
                        ) { index ->
                            Text(units.get(index).label, color= focusColor(index == minuteState.currentIndexSnapshot))
                        }
                    }

                    BottomConfirmButtonGroup(onDismiss = {
                        openDialog = false
                    }, onConfirm = {
                        openDialog = false
                        onValueChange("${nums[hourState.currentIndexSnapshot].value}:${units[minuteState.currentIndexSnapshot].value}")
                    })
                }
            }
        }
    }
}

