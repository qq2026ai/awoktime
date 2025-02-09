package cn.jianyun.worktime.main.setting.user

import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.dateStr
import cn.jianyun.worktime.util.gapDay
import cn.jianyun.worktime.util.isValidEmail
import cn.jianyun.worktime.util.parseDate
import java.util.Date


data class User(
    var uuid: String = "",
    var username: String = "",
    var nickname: String = "",
    var email: String = "",
    var avatar: String = "",
    var gmtCreate: String = "", //注册时间
    var vipName: String = "", //vip名称
    var vipDate: String = "", //vip有效期
    var password: String = "",
    var confirmPassword: String = "", //确认密码
    var wechat: String = "", //微信号
    var qq: String = "", //QQ号
    var city: String = "", //城市
    var mobile: String = "",
    var cloudConfig: String = "",

    var registTime: Long = 0,

    //拓展字段
    var application: String = "",
    var platform: String = "",
    var deviceId: String = "",

){

    fun isLogin(): Boolean{
        return uuid != "" && username != ""
    }

    fun isVip(): Boolean {
        return (vipDate == "永久" || vipDate.parseDate() > Date()) && vipName != ""
    }

    fun showName(): String{
        if(nickname != ""){
            return nickname
        }
        return username
    }

    fun isLoginValid(): String {
        if(username.trim() == ""){
            return "邮箱账号不能为空"
        }
        if(password.trim() == ""){
            return "密码不能为空"
        }
        return "ok"
    }

    fun getRegistDay(): Int {
        return MyDateTool.getBetweenDays(registTime, System.currentTimeMillis())
    }

    fun isRegistValid(): String {
        if(username.trim() == ""){
            return "邮箱账号不能为空"
        }
        if(password.trim() == ""){
            return "密码不能为空"
        }
        if(confirmPassword == ""){
            return "确认密码不能为空"
        }
        if(!isValidEmail(username)) {
            return "邮箱账号格式不正确"
        }
        if(password.length < 6){
            return "密码长度不能少于6位"
        }
        if(password != confirmPassword){
            return "确认密码不正确"
        }
        return "ok"
    }

    fun makeVip(currentMode: String) {
        var startDate = vipDate.parseDate()
        if(currentMode == "forever"){
            this.vipName = "永久会员V3"
            this.vipDate = "永久"
        }
        if(currentMode == "year" && this.vipName != "永久会员V3"){
            this.vipName = "年度会员V2"
            this.vipDate = startDate.gapDay(370).dateStr()
        }
        if(currentMode == "month" && this.vipName != "永久会员V3" && this.vipName != "年度会员V2"){
            this.vipName = "月度会员V1"
            this.vipDate = startDate.gapDay(31).dateStr()
        }
    }

}