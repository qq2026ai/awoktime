package cn.jianyun.worktime.module.timework.model


import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.jianyun.worktime.module.base.model.BaseRoomModel
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.toIntData


@Entity(tableName = "TimeworkAward")
data class TimeworkAward(
    var name:String = "", //名称

    var type:String = "award", //类型
    var defaultValue:String = "", //默认值




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
    fun isAward(): Boolean {
        return type == "award"
    }

    fun typeName(): String {
        return ifv(isAward(), "补贴", "扣款")
    }
    fun typeColor(): Color {
        return ifv(isAward(), ThemeColor, DeleteColor)
    }

    override fun isValid(): String {
        if(name == ""){
            return "不能为空"
        }
        if(defaultValue.toIntData() < 0){
            return "金额不能为负数"
        }
        return "ok"
    }

    fun toSelect(): SelectDO {
        return SelectDO(name, uuid)
    }
}