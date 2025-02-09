package cn.jianyun.worktime.module.timework.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.jianyun.worktime.module.base.model.BaseRoomModel
import cn.jianyun.worktime.util.SelectDO


@Entity
data class TimeworkProject(
    var name:String = "", //名称

    @PrimaryKey
    override var uuid:String = "",

    var remark: String = "", //描述
    var gmtCreate: String = "", //创建时间
    var ordinal: Long = 0,
    var extraValue: String = "" //拓展字段

):  BaseRoomModel() {
    override fun isValid(): String {
        if(name == ""){
            return "项目名称不能为空"
        }
        return "ok"
    }

    fun toSelect():SelectDO{
        return SelectDO(name, uuid)
    }



}