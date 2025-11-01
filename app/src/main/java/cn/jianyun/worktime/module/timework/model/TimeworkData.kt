package cn.jianyun.worktime.module.timework.model



import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cn.jianyun.worktime.module.base.model.BaseRoomModel
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.TimeModel
import cn.jianyun.worktime.util.showTime
import cn.jianyun.worktime.util.timeToFloat


/**
 * 工时打卡记录
 */
@Entity(tableName = "TimeworkData")
data class TimeworkData(


    var day: String = "", //打卡日期

    var onlyOver:Boolean = false, //是否仅加班
    var overSalaryUuid:String = "", //加班薪水
    var overTime:Boolean = false, //是否加班
    var salaryUuid:String = "", //普通薪水
    var workUuid:String = "", //班次信息

    var baseSalaryTime: String = "", //普通上班时间
    var overSalaryTime: String = "", //加班时间

    var amount:String = "", //日结收入
    var beginTime: String = "", //开始时间
    var endTime: String = "", //结束时间
    var restTime: String = "", //休息时长
    var mode: String = "", //打卡类型
    var tag: String = "", //标签



    @PrimaryKey
    override var uuid:String = "",

    //通用字段
    var projectUuid: String = "",
    var shown: Boolean = true,

    var remark: String = "", //描述
    var gmtCreate: String = "", //创建时间
    var ordinal: Long = 0,
    var extraValue: String = "" //拓展字段


): BaseRoomModel(){

    @Ignore
    var baseSalaryInfo: String = ""
    @Ignore
    var baseSalaryPrice: Float = 0f
    @Ignore
    var overSalaryInfo: String = ""
    @Ignore
    var overSalaryPrice: Float = 0f

    @Ignore
    var totalSalaryPrice: Float = 0f



    override fun isValid(): String {

        if(mode == "hour") {
            if(!onlyOver){
                if(baseSalaryTime == ""){
                    return "正班时长不能为空"
                }
                if(salaryUuid == ""){
                    return "正班薪水不能为空"
                }
            }
            if(overTime){
                if(overSalaryTime == ""){
                    return "加班时长不能为空"
                }
                if(overSalaryUuid == ""){
                    return "加班薪水不能为空"
                }
            }
        }
        if(mode == "day"){
            if(amount == ""){
                return "日结工资不能为空"
            }

            if(beginTime != "" && endTime != "" && restTime != ""){
                if(MyDataTool.minusTime(MyDataTool.minusTime(endTime, beginTime, true), restTime, false).timeToFloat(1) < 0){
                    return "休息时长不能大于上班时长"
                }
            }

        }
        if(mode == "time") {
            if(beginTime == ""){
                return "上班开始时间不能为空"
            }
            if(salaryUuid == ""){
                return "上班薪水不能为空"
            }

            if(beginTime != "" && endTime != "" && restTime != ""){
                if(MyDataTool.minusTime(MyDataTool.minusTime(endTime, beginTime, true), restTime, false).timeToFloat(1) < 0){
                    return "休息时长不能大于上班时长"
                }
            }

        }
        return "ok"
    }

    fun fetchBaseHour(): String {
        if(mode == "hour") {
            if(!onlyOver){
                return baseSalaryTime
            }
        }
        else if(mode == "time"){
            if(endTime != "" && beginTime != ""){
                var t = MyDataTool.minusTime(endTime, beginTime, true)
                var t2 = MyDataTool.minusTime(t, restTime, false)
                return t2
            }
        }
        else if(mode == "day"){
            if(endTime != "" && beginTime != ""){
                var t = MyDataTool.minusTime(endTime, beginTime, true)
                var t2 = MyDataTool.minusTime(t, restTime, false)
                return t2
            }
            else if(baseSalaryTime != ""){
                return baseSalaryTime
            }
        }
        return ""
    }

    fun fetchBaseHourShownInfo(): String {
        return MyDataTool.getShownTime(fetchBaseHour())
    }

    fun fetchOverHourShownInfo(): String {
        return MyDataTool.getShownTime(fetchOverHour())
    }

    fun fetchOverHour(): String {
        if(mode == "hour") {
            if(overTime){
                return overSalaryTime
            }
        }
        return ""
    }

    fun fetchTotalHour(): String {
        return MyDataTool.plusTime(fetchBaseHour(), fetchOverHour())
    }

    fun fetchBaseMoney(price: Float): String {
        return TimeModel.getTimeMoneyString(fetchBaseHour(), price);
    }

    fun fetchOverMoney(price:Float): String {
        return TimeModel.getTimeMoneyString(fetchOverHour(), price);
    }

    fun fetchAliasName(): String {
        var result = ""
        if(mode == "hour") {
            if(!onlyOver){
                result += "正班${fetchBaseHour().showTime(true)}"
            }
            if(overTime){
                result += "加班${fetchOverHour().showTime(true)}"
            }
        }
        else if(mode == "time"){
            result += "正班${fetchBaseHour().showTime(true)}"
        }
        else{
            result = "日结${amount}元"
        }
        return result
    }
}