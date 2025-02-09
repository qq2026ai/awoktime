package cn.jianyun.worktime.api

import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.MyDateTool
import java.sql.Date

data class FeedbackModel(
    var uuid: String = "",
    var puuid: String = "",
    var platform: String = "",
    var app: String = "",
    var module: String = "",
    var userId: String = "",
    var nickname: String = "",
    var content: String = "",
    var type: String = "",
    var image: String = "",
    var vip: String = "",
    var root: String = "",
    var answer: String = "",
    var readed: String = "",
    var common: String = "",
    var rootId: String = "",
    var gmtCreate: String = "",
    var children: List<FeedbackModel> = listOf()
) {

    fun  isAdd():Boolean{
        return uuid == ""
    }

    fun isAdmin(): Boolean{
        return userId == "admin"
    }

    fun isValid(): String {
        if(module == ""){
            return "反馈模块不能为空"
        }
        if(type == ""){
            return "反馈类型不能为空"
        }
        if(content == ""){
            return "内容不能为空"
        }
        return "ok"
    }


    fun shortTime(): String {
        if(gmtCreate.length > 14){
            return gmtCreate.substring(5, 16)
        }
        return ""
    }
}