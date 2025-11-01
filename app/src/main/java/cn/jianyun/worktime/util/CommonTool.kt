package cn.jianyun.worktime.util


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.unit.ColorProvider
import androidx.navigation.NavHostController
import cn.jianyun.worktime.BuildConfig
import cn.jianyun.worktime.api.ApiResult
import cn.jianyun.worktime.main.Router
import cn.jianyun.worktime.main.navigateTo
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import com.alibaba.fastjson2.toJSONString
import kotlinx.coroutines.flow.first
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException


fun repeatValue(length: Int, value: String = ""): List<String> {
    var s = mutableListOf<String>()
    repeat(length){
        s.add(value)
    }
    return s
}

fun <T> append(list: List<T>, newEle: T): List<T> {
    var newList = list.toMutableList()
    newList.add(newEle)
    return newList
}

fun uuid(): String{
    return MyRandomTool.uuids()
}

fun makeKey(key: String, module: String):String {
    return key + "_" + module
}
fun makePKey(key: String, module: String = ""): Preferences.Key<String>{
    if(module == ""){
        return stringPreferencesKey(key)
    }
    return stringPreferencesKey(makeKey(key, module))
}

fun makePIKey(key: String, module: String = ""): Preferences.Key<Int>{
    if(module == ""){
        return intPreferencesKey(key)
    }
    return intPreferencesKey(makeKey(key, module))
}


suspend fun getStringValue(datastore: DataStore<Preferences>, key:String, defaultValue: String): String {
    val preferences = datastore.data.first()
    return preferences[makePKey(key)] ?: defaultValue
}


fun mlog(vararg arg: Any?){
    if(!BuildConfig.IS_DEV) {
        return
    }
    var group = mutableListOf<String>()
    arg.forEach {
        if(it != null){
            group.add(it.toString())
        }
        else{
            group.add("null")
        }
    }
    Log.d("qzLog", group.joinToString(", "))
}

fun mlog2(arg: Any){
    Log.d("qzLog", arg.toJSONString())
}


fun String.color(): Color {
    if(this.length != 7 && this.length != 9 || !this.startsWith("#")){
        return Color.Gray
    }
    else{
        var cc = this
        if(this.length == 9){
            cc = this.substring(0, 7)
        }
        try{
            return Color(android.graphics.Color.parseColor(cc))
        }
        catch(e:Exception){
            return Color.Gray
        }
    }
}

//
fun String.toFloatData(decimal: Int = 0): Float{
    return MyDataTool.getPriceWithFloat(this, decimal)
}
fun String.toIntData(): Int{
    return MyDataTool.getPriceWithFloat(this, 0).toInt()
}
fun String.toLongData (): Long{
    return MyDataTool.getPriceWithFloat(this, 0).toLong()
}
fun String.withUnit(decimal: Int = -1, unit: String): String{
    return MyDataTool.withUnit(this, decimal, unit)
}

fun String.timeToFloat(decimal: Int = 1): Float{
    return MyDataTool.timeToDecimal(this).toFloatData(decimal)
}
fun String.showTime(small: Boolean = true): String{
    return MyDataTool.getShownTime(this, small)
}


fun String.betweenIn(beginDate: String, endDate: String): Boolean{
    return MyDateTool.between(this, beginDate, endDate)
}

fun String.isValidColor(): Boolean {
    if(this.length != 7 && this.length != 9 && !this.startsWith("#")){
        return false
    }
    else{
        return true
    }
}

fun String.widgetColor(): ColorProvider {
    return ColorProvider(this.color())
}

fun isOk(msg: String): Boolean{
    return msg == "ok"
}

fun String.unique(): Int {
    val array = this.toCharArray()
    var total = 0
    for (ch in array){
        val result = ch.toInt()
        total += result
    }
    return total;
}

fun randomColor(value: Int = 0): Color {
    if(value > 0){
        val red = Math.min(20, value - 30) % 255
        val green =  (value + 30) % 255
        val blue =  (value + 80) % 255
        return String.format("#%02X%02X%02X", red, green, blue).color()
    }

    val red = MyRandomTool.randomNumber(0, 255)
    val green = MyRandomTool.randomNumber(0, 255)
    val blue = MyRandomTool.randomNumber(0, 255)
    return String.format("#%02X%02X%02X", red, green, blue).color()
}

val opacityColor = "#00FFFFFF".color()

fun Color.toAndroid(): Int {
    return android.graphics.Color.parseColor(this.stringify())
}

fun Color.stringify(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

fun Color.stringify2(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    val alpha = (this.alpha * 255).toInt()
    return String.format("#%02X%02X%02X%02X", red, green, blue, alpha)
}

fun chooseColor(flag: Boolean, trueColor: Color, falseColor: Color): Color {
    return if(flag) trueColor else falseColor
}

@Composable
fun focusColor(flag: Boolean, colorScheme: ColorScheme = MaterialTheme.colorScheme): Color {
    return if(flag) ThemeColor else MaterialTheme.colorScheme.primary
}

fun tipColor(focus: Boolean): Color {
    return if(focus) ThemeColor else Color.Gray
}



fun <T : Any> ifv(flag: Boolean, value: T, defaultValue: T): T {
    if(flag){
        return value
    }
    return defaultValue
}

fun ifv(flag: Boolean, value: String): String {
    if(flag){
        return value
    }
    return ""
}


fun ifv(value: String, defaultValue: String): String {
    if("".equals(value)){
        return defaultValue
    }
    return value
}

fun withPrefix(value: String, prefix: String = " "): String {
    if(value != ""){
        return prefix + value
    }
    return value
}

fun ifv(value: Int, defaultValue: Int): Int {
    if(value < 0){
        return defaultValue
    }
    return value
}


fun getToastMessageLength(msg: String): Int{
    if(msg.length < 10){
        return Toast.LENGTH_SHORT
    }
    return Toast.LENGTH_LONG
}


fun px2dp(context: Context, px: Int): Int {
    return (px.toFloat() / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun dp2px(context: Context, dp: Int): Int{
    return (dp.toFloat() * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun winHeightWithDp(context: Context): Int{
    return px2dp(context, winHeightWithPx(context))
}

fun winHeightWithPx(context: Context): Int{
    return context.resources.displayMetrics.heightPixels
}

fun winWidthWithDp(context: Context): Int {
    return px2dp(context, winWidthWithPx(context))
}

fun winWidthWithPx(context: Context): Int {
    return context.resources.displayMetrics.widthPixels
}


fun readAssetFile(context: Context, dir: String, file: String): String? {
    var reader: BufferedReader? = null
    val sb = StringBuffer()
    try {
        reader = BufferedReader(
            InputStreamReader(context.assets.open("$dir/$file"))
        )

        // do reading, usually loop until end of file reading
        var mLine: String?
        while (reader.readLine().also { mLine = it } != null) {
            sb.append(mLine)
            sb.append("\n")
        }
    } catch (e: IOException) {
        //log the exception
    } finally {
        if (reader != null) {
            try {
                reader.close()
            } catch (e: IOException) {
                //log the exception
            }
        }
    }
    return sb.toString()
}


fun base64(str: String): String? {
    try {
        return Base64.encodeToString(str.toByteArray(charset("UTF-8")), Base64.DEFAULT)
            .replace("\n", " ")
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return ""
}

var lastBack:Long = 0
fun goBack(navHostController: NavHostController, backAction: (() -> Unit)? = null) {
    nextPage = ""
    if(backAction != null){
        backAction()
        return
    }
    if(System.currentTimeMillis() - lastBack < 1000){
        mlog("ignore back", System.currentTimeMillis() - lastBack)
        return
    }
    lastBack = System.currentTimeMillis()
    if(navHostController.currentBackStackEntry == null){
        mlog("toTop", "已经到顶部路由了")
        return
    }

    if(navHostController.currentDestination?.route == Router.Home.route){
        mlog("toTop", "已经到顶部路由了")
        return
    }

    mlog("回到上一页", navHostController.currentDestination?.navigatorName ?: "", navHostController.currentDestination?.route ?: "")
    try{
        navHostController.popBackStack()
        nextPage = ""
    }
    catch (ee: Exception){
        mlog("go back error", ee)
    }
}

var nextPage: String = ""
var nextTime: Long = 0
fun toPage(navHostController: NavHostController, newPage: String, bundle: Bundle? = null) {
    if(nextPage == newPage && System.currentTimeMillis() - nextTime < 1200){
        return;
    }
    nextPage = newPage
    nextTime = System.currentTimeMillis()
    if(bundle != null){
        navHostController.navigateTo(newPage, bundle)
    }
    else{
        navHostController.navigate(newPage)
    }
}


fun toVipPage(navHostController: NavHostController){
    toPage(navHostController, Router.VipPage.route)
}

fun pushMapValue(map: MutableMap<String, Float>, key: String, value: Float) {
    var old = map.get(key) ?: 0f
    map.put(key, old + value)
}


fun String.oneLine(): String {
    return this.splitToSequence("\n").map{it.trim()}.joinToString("").replace("<br>", "\n").replace("<bb>", "    ")
}

fun String.toMultiData(): List<String>{
    return this.split("^")
}


fun Context.findAndroidActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

suspend fun <T> withApi(api:suspend () -> ApiResult<T>): ApiResult<T>{
    try{
        return api()
    }
    catch (e: Exception){
        mlog(e.localizedMessage)
        return ApiResult.fail("暂时不可用，请稍后再试")
    }
}