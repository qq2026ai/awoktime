package cn.jianyun.worktime.ui.graph.model
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.toFloatData
import kotlin.math.absoluteValue

class PieGraphData(
    var title: String = "",
    var unit: String = "",
    var datalist: List<PieGraphItem> = listOf()
) {
    fun  isValid(): Boolean {
        return !datalist.isEmpty()
    }
}

var colors = listOf(

    "#46B887","#F07C82","#894276","#12AA9C","#E7A23F","#D99156",
    "#475164","#EE8055","#1661AB","#8A998E", "#22A2C3","#8DC269","#FA7E23","#8D91AA",
    "#9946B887","#99F07C82","#99894276","#9912AA9C","#99E7A23F","#99D99156",
    "#99475164","#99EE8055","#991661AB","#998A998E", "#9922A2C3","#998DC269","#99FA7E23","#998D91AA"

)

fun makePieStatData(title: String, unit: String, datalist: List<PieGraphItem>, decimal: Int = 0): PieGraphData{
    if(datalist.isEmpty()){
        return PieGraphData(title,unit, emptyList())
    }
    var templist = mutableListOf<PieGraphItem>()
    var k = datalist.sortedWith{o1, o2 -> (o2.value.toFloatData(2).absoluteValue * 100 - o1.value.toFloatData(2).absoluteValue * 100).toInt()}
    var i = 0
    var total = 0f

    k.forEach{
        total += it.value.toFloatData(2)
    }

    k.forEach{
        templist.add(it.copy(color = ifv(it.color == "", colors[i % colors.size], it.color), percent = it.getPercent(total)))
        i += 1
    }
    return PieGraphData(title,unit, templist.filter{it.value.toFloatData(decimal) != 0f}.sortedWith{v1, v2 -> (v2.percent - v1.percent).toInt() })
}

data class PieGraphItem(
    val name: String = "",
    val value: String = "",
    val color: String = "",
    var percent: Float = 0f
) {
    fun label(i: Int, all: List<PieGraphItem>): String {
        val showData = showName() + " " + MyDataTool.getShownPrice(percent.toString(), 0) + "%"
        if(i <= 1){
            return showData
        }
        if(all[i - 1].percent > 10){
            return showData
        }
        if(percent > 10){
            return showData
        }
        return ""
    }

    fun showName(): String {
        if(name.length > 5){
            return name.substring(0, 5)
        }
        return name
    }

    fun percentValue(): String {
        return MyDataTool.getShownPrice(percent.toString(), 0) + "%"
    }

    fun getPercent(total: Float): Float {
        if(total == 0f){
            return 0f
        }
        return (value.toFloatData(2).absoluteValue / total) * 100
    }

}