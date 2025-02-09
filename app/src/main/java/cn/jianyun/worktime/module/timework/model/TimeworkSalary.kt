package cn.jianyun.worktime.module.timework.model



import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.jianyun.worktime.module.base.model.BaseRoomModel
import cn.jianyun.worktime.util.MyDataTool
import cn.jianyun.worktime.util.SelectDO


/**
 * 工时薪水记录表
 */
@Entity(tableName = "TimeworkSalary")
data class TimeworkSalary(

    var name:String = "", //名称

    var type: String = "normal", //薪水类型
    var calcType:String = "fix", //薪水计算类型
    var value: String = "", //手动填写薪水
    var monthValue: String = "", //手动填写薪水
    var workType: String = "", //月度工作类型

    var overType: String = "times", //加班薪水类型
    var overValue: String = "", //加班工时薪水
    var refSalary: String = "", //引用薪水
    var amount:String = "", //薪水倍数
    var baseHour:String = "8", //上班小时数

    var overValueStr:String = "",

    var showValue: String = "", //显示内容

    var calcInfo: String = "",

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


    fun fetchRealHourSalary(salarys:List<TimeworkSalary>): Float {
        if(type == "normal"){
            if(calcType == "fix"){
                return MyDataTool.getPriceWithFloat(value, 2);
            }
            else{
                return MyDataTool.divideWithFloat(monthValue,  (MyDataTool.getPriceWithFloat(workType, 2) * 8f).toString(), 2);
            }
        }
        else{
            if(overType == "fix"){
                return MyDataTool.getPriceWithFloat(overValue, 2);
            }
            else{
                val t0 = salarys.find{it.uuid == refSalary}
                if(t0 == null || refSalary == uuid){
                    return 0f
                }
                val u = t0.fetchRealHourSalary(salarys)
                return u * MyDataTool.getPriceWithFloat(amount, 2)
            }
        }
    }

    fun makeShowValue(salarys:List<TimeworkSalary> = listOf()): String {
        return "${MyDataTool.getShownPrice(fetchRealHourSalary(salarys).toString(), 2)}元/时"
    }

    override fun isValid(): String {
        if(name == ""){
            return "薪水名称不能为空"
        }

        //正班薪水
        if(type == "normal"){
            if(calcType == "fix"){
                if(value == ""){
                    return "时薪不能为空"
                }
            }
            else{
                if(workType == ""){
                    return "休息类型不能为空"
                }
                if(monthValue == ""){
                    return "月薪不能为空"
                }
            }
        }
        else{
            if(overType == "fix"){
                if(overValue == ""){
                    return "时薪不能为空"
                }
            }
            else{
                if(refSalary == ""){
                    return "基础薪水不能为空"
                }
                if(amount == ""){
                    return "倍数不能为空"
                }
            }
        }
        return "ok"
    }

    fun getBaseSalary(): String {
        if(type != "normal"){
            return ""
        }
        var v = value
        if(calcType == "month"){
            v = MyDataTool.divide(monthValue,  (MyDataTool.getPriceWithFloat(workType, 2) * 8f).toString(), 1);
        }
        return name + "(" + v + "元/时)"
    }

    fun toBaseSelect():SelectDO{
        return SelectDO(getBaseSalary(), uuid)
    }

    fun toSelect(): SelectDO {
        return SelectDO(name + "(" + showValue +")", uuid)
    }

}
