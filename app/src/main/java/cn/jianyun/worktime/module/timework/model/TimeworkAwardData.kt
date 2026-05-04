package cn.jianyun.worktime.module.timework.model



import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cn.jianyun.worktime.module.base.model.BaseRoomModel
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor


@Entity(tableName = "TimeworkAwardData")
data class TimeworkAwardData(

    var name:String = "", //名称
    var day: String = "", //日期
    var awardUuid: String = "",  //补贴id
    var awardType: String = "",  //类型，补贴或扣款
    var awardValue: String = "", //补贴金额
    var awardName: String = "", //补贴名称


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


    fun typeName(): String {
        return ifv(awardType == "award", "补贴", "扣款")
    }

    fun typeColor(): Color {
        return ifv(awardType == "award", ThemeColor, DeleteColor)
    }

    override fun isValid(): String {
        if(awardType == ""){
            return "补扣类型不能为空"
        }
        if(awardUuid == ""){
            return "${typeName()}项不能为空"
        }
        if(awardValue == ""){
            return "${typeName()}金额不能为空"
        }
        return "ok"
    }

    fun realAwardValue(): String {
        return ifv(awardType == "award", awardValue, "-" + awardValue)
    }

    fun fetchAliasName(): String {
        return typeName() + this.awardValue + "元"
    }

    fun isSettled(): Boolean {
        return extraValue == "settled"
    }

    fun settle(settled: Boolean): TimeworkAwardData {
        return copy(extraValue = if (settled) "settled" else "")
    }
}
