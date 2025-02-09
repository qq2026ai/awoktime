package cn.jianyun.worktime.ui.graph.model

import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.parseChineseMonth
import cn.jianyun.worktime.util.parseDate
import cn.jianyun.worktime.util.pushMapValue
import cn.jianyun.worktime.util.timeToFloat
import cn.jianyun.worktime.util.toFloatData

data class LineGraphData(
    var name: String = "",
    var unit: String = "",
    var mode: String = "",
    var color: String = "",
    var datalist: List<LineGraphItem> = listOf()
) {

    fun fetchXData(): List<Int>{
        return datalist.map{it.x}
    }

    fun fetchYData(): List<Int> {
        return datalist.map{toNumber(it.value)}
    }

    fun step(): Float{
        if(datalist.size >= 30){
            return datalist.size / 8f
        }
        if(datalist.size >= 15){
            return 2f
        }
        return 1f
    }

    private fun toNumber(y: String): Int{
        if(mode == "time"){
            return MyDataTool.getRealPrice(MyDataTool.timeToDecimal(y), 0).toInt()
        }
        return MyDataTool.getRealPrice(y, 0).toInt()
    }

    fun fetchMarker(x: Int): String {
        return datalist[x].day + name + " " + MyDataTool.withUnit(datalist[x].value, unit)
    }
}

data class LineGraphItem(
    var day: String = "",
    var showDay: String = "",
    var x: Int = 0,
    var value: String = "",
) {

}

private fun makeLineGraphDataByDay(beginDay: String, endDay: String,unit: String, name:String = "",color:String = "", mode: String="decimal", datalist: List<LineGraphItem>): LineGraphData{
    var resultList = mutableListOf<LineGraphItem>()
    var t = beginDay
    var i = 0
    while(t <= endDay){
        var datas = datalist.filter{it.day == t}
        var mk = LineGraphItem(x=i, showDay= MyDateTool.getDay(t), value = "0", day=t)
        if(!datas.isEmpty()){
            mk.value = makeSum(datas, mode)
        }
        resultList.add(mk)
        t = MyDateTool.nextDay(t)
        i += 1
    }
    return LineGraphData(name=name,color=color, unit=unit,  mode = mode, datalist=resultList.toList())
}

private fun makeLineGraphDataByMonth(beginDay: String, endDay: String,  unit: String, name: String = "",color:String = "",mode: String="decimal", datalist: List<LineGraphItem>): LineGraphData {
    var resultList = mutableListOf<LineGraphItem>()
    var t = beginDay
    var monthMap = mutableMapOf<String, Float>()
    while(t <= endDay){
        val month = MyDateTool.toChineseMonthString(t.parseDate())
        var datas = datalist.filter{it.day == t}
        if(!datas.isEmpty()){
            pushMapValue(monthMap, month, makeSumValue(datas, mode))
        }
        else{
            pushMapValue(monthMap, month, 0f)
        }
        t = MyDateTool.nextDay(t)
    }
    var i = 0
    monthMap.keys.toList().sortedWith{k1,k2 -> k1.compareTo(k2)}.forEach{
        resultList.add(LineGraphItem(day = it, showDay = "" + (MyDateTool.getMonth(it.parseChineseMonth()) ), x=i, value = monthMap.get(it)?.toString() ?: "无"))
        i += 1
    }
    return LineGraphData(name=name,color=color, unit=unit,  mode = mode, datalist=resultList.toList())
}

fun makeLineGraphStatData(beginDay: String, endDay: String, unit: String, name: String = "",color: String = "", mode: String="decimal", datalist: List<LineGraphItem>):LineGraphData{
    var betweenDays = MyDateTool.getBetweenDays(beginDay.parseDate(), endDay.parseDate())
    if(betweenDays > 60){
        return makeLineGraphDataByMonth(name=name, beginDay = beginDay, endDay = endDay,color=color, mode=mode, unit = unit, datalist = datalist)
    }
    return makeLineGraphDataByDay(name=name, beginDay = beginDay, endDay = endDay,color=color, mode=mode, unit = unit, datalist = datalist)
}

fun makeSum(data: List<LineGraphItem>, mode: String): String{
    var result = ""
    data.forEach{
        if(mode == "number"){
            result = MyDataTool.plusNum(result, it.value).toString()
        }
        else if(mode == "decimal"){
            result = MyDataTool.plusPriceWithString(result, it.value)
        }
        else if(mode == "time"){
            result = MyDataTool.plusTime(result, it.value)
        }
    }
    return result
}

fun makeSumValue(data: List<LineGraphItem>, mode: String): Float{
    var result = ""
    data.forEach{
        if(mode == "number"){
            result = MyDataTool.plusNum(result, it.value).toString()
        }
        else if(mode == "decimal"){
            result = MyDataTool.plusPriceWithString(result, it.value)
        }
        else if(mode == "time"){
            result = MyDataTool.plusTime(result, it.value)
        }
    }
    if(mode == "time"){
        return result.timeToFloat(2)
    }
    return result.toFloatData(2)
}