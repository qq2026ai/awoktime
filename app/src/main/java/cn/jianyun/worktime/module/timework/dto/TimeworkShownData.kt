package cn.jianyun.worktime.module.timework.dto

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.toIntData


data class TimeworkShownData(
    var hour: String = "",
    var hourColor: Color = "#EF632B".color(),
    var money: String = "",
    var moneyColor: Color = "#40A070".color(),
    var rest: Boolean = false,
    var restColor: Color = Color.Gray,
    var leave: Boolean = false,
    var leaveColor: Color = Color.Gray,
    var showHour: Boolean = true,
    var showMoney: Boolean = true,
    var dayMoney: String = "",
    var awardValue: String = "",
    var fineValue: String = "",
    var hourSize: Int = 12,
    var moneySize: Int = 12
){

    fun isEmpty(): Boolean{
        return MyDataTool.getShownTime(hour) == "无" && money.toIntData() == 0 && awardValue.toIntData() == 0 && fineValue.toIntData() == 0 && dayMoney.toIntData() == 0 && !(leave || rest)
    }

    fun hasAward(): Boolean{
        return awardValue != ""
    }

    fun hasFine(): Boolean{
        return fineValue != ""
    }

    fun fetchFontSize(): TextUnit {
        var a = MyDataTool.timeToDecimal(hour)
        val baseSize = (hourSize - 1).sp
        if(a == ""){
            if(dayMoney != ""){
                return baseSize
            }
            if(leave){
                return baseSize
            }
            if(rest){
                return baseSize
            }
            if(awardValue != ""){
                return baseSize
            }
            if(fineValue != ""){
                return baseSize
            }
            return hourSize.sp
        }
        return hourSize.sp
    }

    fun fetchShownHour(): String{
        if(dayMoney != ""){
            return "日结"
        }
        if(leave){
            return "请假"
        }
        if(rest){
            return "休息"
        }
        var a = MyDataTool.timeToDecimal(hour)
        if(a == ""){
            if(awardValue != ""){
                return "补贴"
            }
            if(fineValue != ""){
                return "扣款"
            }
            return ""
        }
        if(MyDataTool.getPriceWithFloat(a, 0) == MyDataTool.getPriceWithFloat(a, 1)) {
            a = MyDataTool.getShownPrice(a, 0);
        }
        else if(MyDataTool.getPriceWithFloat(a, 1) == MyDataTool.getPriceWithFloat(a, 2)) {
            a = MyDataTool.getShownPrice(a, 1);
        }
        return a + "h"
    }

    fun getTotalMoney(withUnit: Boolean = false): String {
        return MyDataTool.toFixed(MyDataTool.getPriceWithFloat(money, 2) +
                MyDataTool.getPriceWithFloat(dayMoney, 2) +
                MyDataTool.getPriceWithFloat(awardValue, 2) -
                MyDataTool.getPriceWithFloat(fineValue, 2), 2)
    }

    fun fetchRealFirstColor(): Color {
        if(leave){
            return leaveColor
        }
        if(rest){
            return restColor
        }
        return hourColor
    }
}