package cn.jianyun.worktime.module.timework.dto

import cn.jianyun.worktime.util.MyDataTool

data class TimeworkSettleSummaryData(
    var name: String = "",
    var settledMoney: String = "",
    var totalMoney: String = "",
    var settledCount: Int = 0,
    var totalCount: Int = 0
) {

    fun unSettledMoney(): String {
        return MyDataTool.minusPriceWithString(totalMoney, settledMoney)
    }

    fun unSettledCount(): Int {
        return (totalCount - settledCount).coerceAtLeast(0)
    }

    fun progress(): Float {
        val total = MyDataTool.getPriceWithFloat(totalMoney, 2)
        if (total <= 0f) {
            return 0f
        }
        val settled = MyDataTool.getPriceWithFloat(settledMoney, 2)
        return (settled / total).coerceIn(0f, 1f)
    }

    fun percentText(): String {
        return "${MyDataTool.toFixed(progress() * 100f, 2)}%"
    }

    fun showMoney(value: String): String {
        return "${MyDataTool.toFixed(MyDataTool.getPriceWithFloat(value, 2), 2)}元"
    }
}
