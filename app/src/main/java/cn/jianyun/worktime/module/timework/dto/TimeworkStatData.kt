package cn.jianyun.worktime.module.timework.dto

import cn.jianyun.worktime.util.MyDataTool

data class TimeworkStatData(

    var baseHour: String = "",
    var overHour: String = "",
    var totalHour: String = "",
    var baseSalary: String = "",
    var overSalary: String = "",
    var dayCount: String = "",
    var dayMoney: String = "",
    var awardMoney: String = "",
    var fineMoney: String = "",

    var normalDay: Int = 0,
    var overDay: Int = 0,
    var totalDay: Int = 0

){


    fun fetchTotalHour(): String {
        return MyDataTool.plusTime(baseHour, overHour);
    }

    fun fetchValue(value: String, defaultValue: String = "-"): String {
        if(value == ""){
            return defaultValue
        }
        return value
    }

    fun fetchTotalSalary(): String {
        return MyDataTool.plusPriceWithString(baseSalary, overSalary)
    }

    fun fetchTotalMoney(): String {
        val t1 = MyDataTool.plusPriceWithString(baseSalary, overSalary)
        val t2 = MyDataTool.plusPriceWithString(t1, dayMoney)
        val t3 = MyDataTool.plusPriceWithString(t2, awardMoney)
        val t4 = MyDataTool.plusPriceWithString(t3, "-$fineMoney")
        return t4
    }

}