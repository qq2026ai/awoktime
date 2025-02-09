package cn.jianyun.worktime.module.base.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.jianyun.worktime.util.MyEncryptTool
import cn.jianyun.worktime.util.MyWebdavTool

@Entity
data class WebDAVUser(
    @PrimaryKey
    var uuid: String = "",
    var url: String = "",
    var username: String = "",
    var password: String = "",
    var platform: String = "",
    var type: String = "webDAV",
    var defaultPath: String = "",
    var masterNode: Boolean = false,
    var bind: Boolean = false,
    var ordinal: Int = 0,
    var shown: Boolean = true,
    var message: String = "", //异常消息
    var cloud: Boolean = false
){

    fun nodeInfo(): String {
        return platform + username
    }


    fun isAdd(): Boolean{
        return uuid == ""
    }

    fun isValid(): String {
        if(username.trim() == "" || password.trim() == ""){
            return "账号密码均不能为空"
        }
        return "ok"
    }

    fun resolvePassword(): String {
        if(password == "" || password == null){
            return ""
        }
        else{
            return MyEncryptTool.decrypt(password)
        }
    }

}