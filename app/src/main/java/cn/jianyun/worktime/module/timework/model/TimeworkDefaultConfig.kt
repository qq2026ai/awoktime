package cn.jianyun.worktime.module.timework.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.jianyun.worktime.module.base.model.BaseRoomModel
import cn.jianyun.worktime.util.SelectDO


@Entity(tableName = "TimeworkDefaultConfig")
data class TimeworkDefaultConfig(

    var name:String = "", //名称

    var config: String = "",//配置内容
    var type: String = "", //所属类型

    var aliasName: String = "", //配置别名



    @PrimaryKey
    override var uuid:String = "",

    //通用字段
    var projectUuid: String = "",
    var shown: Boolean = false,

    var remark: String = "", //描述
    var gmtCreate: String = "", //创建时间
    var ordinal: Long = 0,
    var extraValue: String = "" //拓展字段

): BaseRoomModel(){

    override fun isValid(): String {
        if(name == ""){
            return "名称不能为空"
        }
//        if(type == "award" || type == "fine"){
//            var k = JSON.parseObject(config, TimeworkAwardData::class.java)
//            k.awardType = type
//            return k.isValid()
//        }
//        if(type == "sign"){
//            var workData = JSON.parseObject(config, TimeworkData::class.java)
//            return workData.isValid()
//        }
        return "ok"
    }

    fun toSelect(): SelectDO {
        return SelectDO(name, uuid)
    }

}