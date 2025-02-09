package cn.jianyun.worktime.main.base.model

import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.datetimeStr
import java.util.Date

data class BasicCloudData(
    var fileName: String = "",
    var size: String = "",
    var count: String = "",
    var date: String = "",
    var content: String = "",
    var source: String = ""
) {

    fun sizeInfo(): String {
       return MyDataTool.getFileSize(size)
    }

    fun dateInfo(): String {
        try{
            val d = Date(date.toLong())
            return d.datetimeStr()
        }
        catch (e: Exception){
            return "-"
        }
    }

}